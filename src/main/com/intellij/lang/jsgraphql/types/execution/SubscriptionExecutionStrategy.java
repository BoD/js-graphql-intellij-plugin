/*
    The MIT License (MIT)

    Copyright (c) 2015 Andreas Marek and Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
    (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
    publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
    so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
    OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
    LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
    CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.intellij.lang.jsgraphql.types.execution;

import com.intellij.lang.jsgraphql.types.ExecutionResult;
import com.intellij.lang.jsgraphql.types.ExecutionResultImpl;
import com.intellij.lang.jsgraphql.types.PublicApi;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.ExecutionStrategyInstrumentationContext;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.Instrumentation;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.InstrumentationContext;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.parameters.InstrumentationFieldParameters;
import com.intellij.lang.jsgraphql.types.execution.reactive.CompletionStageMappingPublisher;
import com.intellij.lang.jsgraphql.types.language.Field;
import com.intellij.lang.jsgraphql.types.schema.GraphQLFieldDefinition;
import com.intellij.lang.jsgraphql.types.schema.GraphQLObjectType;
import org.reactivestreams.Publisher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static com.intellij.lang.jsgraphql.types.Assert.assertTrue;
import static java.util.Collections.singletonMap;

/**
 * An execution strategy that implements graphql subscriptions by using reactive-streams
 * as the output result of the subscription query.
 * <p>
 * Afterwards each object delivered on that stream will be mapped via running the original selection set over that object and hence producing an ExecutionResult
 * just like a normal graphql query.
 * <p>
 * See https://github.com/facebook/graphql/blob/master/spec/Section%206%20--%20Execution.md
 * See http://www.reactive-streams.org/
 */
@PublicApi
public class SubscriptionExecutionStrategy extends ExecutionStrategy {

  public SubscriptionExecutionStrategy() {
    super();
  }

  public SubscriptionExecutionStrategy(DataFetcherExceptionHandler dataFetcherExceptionHandler) {
    super(dataFetcherExceptionHandler);
  }

  @Override
  public CompletableFuture<ExecutionResult> execute(ExecutionContext executionContext, ExecutionStrategyParameters parameters)
    throws NonNullableFieldWasNullException {

    Instrumentation instrumentation = executionContext.getInstrumentation();
    InstrumentationExecutionStrategyParameters instrumentationParameters =
      new InstrumentationExecutionStrategyParameters(executionContext, parameters);
    ExecutionStrategyInstrumentationContext executionStrategyCtx = instrumentation.beginExecutionStrategy(instrumentationParameters);

    CompletableFuture<Publisher<Object>> sourceEventStream = createSourceEventStream(executionContext, parameters);

    //
    // when the upstream source event stream completes, subscribe to it and wire in our adapter
    CompletableFuture<ExecutionResult> overallResult = sourceEventStream.thenApply((publisher) -> {
      if (publisher == null) {
        return new ExecutionResultImpl(null, executionContext.getErrors());
      }
      Function<Object, CompletionStage<ExecutionResult>> mapperFunction =
        eventPayload -> executeSubscriptionEvent(executionContext, parameters, eventPayload);
      CompletionStageMappingPublisher<ExecutionResult, Object> mapSourceToResponse =
        new CompletionStageMappingPublisher<>(publisher, mapperFunction);
      return new ExecutionResultImpl(mapSourceToResponse, executionContext.getErrors());
    });

    // dispatched the subscription query
    executionStrategyCtx.onDispatched(overallResult);
    overallResult.whenComplete(executionStrategyCtx::onCompleted);

    return overallResult;
  }


    /*
        https://github.com/facebook/graphql/blob/master/spec/Section%206%20--%20Execution.md

        CreateSourceEventStream(subscription, schema, variableValues, initialValue):

            Let {subscriptionType} be the root Subscription type in {schema}.
            Assert: {subscriptionType} is an Object type.
            Let {selectionSet} be the top level Selection Set in {subscription}.
            Let {rootField} be the first top level field in {selectionSet}.
            Let {argumentValues} be the result of {CoerceArgumentValues(subscriptionType, rootField, variableValues)}.
            Let {fieldStream} be the result of running {ResolveFieldEventStream(subscriptionType, initialValue, rootField, argumentValues)}.
            Return {fieldStream}.
     */

  private CompletableFuture<Publisher<Object>> createSourceEventStream(ExecutionContext executionContext,
                                                                       ExecutionStrategyParameters parameters) {
    ExecutionStrategyParameters newParameters = firstFieldOfSubscriptionSelection(parameters);

    CompletableFuture<FetchedValue> fieldFetched = fetchField(executionContext, newParameters);
    return fieldFetched.thenApply(fetchedValue -> {
      Object publisher = fetchedValue.getFetchedValue();
      if (publisher != null) {
        assertTrue(publisher instanceof Publisher,
                   () -> "Your data fetcher must return a Publisher of events when using graphql subscriptions");
      }
      //noinspection unchecked
      return (Publisher<Object>)publisher;
    });
  }

    /*
        ExecuteSubscriptionEvent(subscription, schema, variableValues, initialValue):

        Let {subscriptionType} be the root Subscription type in {schema}.
        Assert: {subscriptionType} is an Object type.
        Let {selectionSet} be the top level Selection Set in {subscription}.
        Let {data} be the result of running {ExecuteSelectionSet(selectionSet, subscriptionType, initialValue, variableValues)} normally (allowing parallelization).
        Let {errors} be any field errors produced while executing the selection set.
        Return an unordered map containing {data} and {errors}.

        Note: The {ExecuteSubscriptionEvent()} algorithm is intentionally similar to {ExecuteQuery()} since this is how each event result is produced.
     */

  private CompletableFuture<ExecutionResult> executeSubscriptionEvent(ExecutionContext executionContext,
                                                                      ExecutionStrategyParameters parameters,
                                                                      Object eventPayload) {
    Instrumentation instrumentation = executionContext.getInstrumentation();

    ExecutionContext newExecutionContext = executionContext.transform(builder -> builder
      .root(eventPayload)
      .resetErrors()
    );
    ExecutionStrategyParameters newParameters = firstFieldOfSubscriptionSelection(parameters);
    ExecutionStepInfo subscribedFieldStepInfo = createSubscribedFieldStepInfo(executionContext, newParameters);

    InstrumentationFieldParameters i13nFieldParameters =
      new InstrumentationFieldParameters(executionContext, () -> subscribedFieldStepInfo);
    InstrumentationContext<ExecutionResult> subscribedFieldCtx = instrumentation.beginSubscribedFieldEvent(i13nFieldParameters);

    FetchedValue fetchedValue = unboxPossibleDataFetcherResult(newExecutionContext, parameters, eventPayload);
    FieldValueInfo fieldValueInfo = completeField(newExecutionContext, newParameters, fetchedValue);
    CompletableFuture<ExecutionResult> overallResult = fieldValueInfo
      .getFieldValue()
      .thenApply(executionResult -> wrapWithRootFieldName(newParameters, executionResult));

    // dispatch instrumentation so they can know about each subscription event
    subscribedFieldCtx.onDispatched(overallResult);
    overallResult.whenComplete(subscribedFieldCtx::onCompleted);

    // allow them to instrument each ER should they want to
    InstrumentationExecutionParameters i13nExecutionParameters = new InstrumentationExecutionParameters(
      executionContext.getExecutionInput(), executionContext.getGraphQLSchema(), executionContext.getInstrumentationState());

    overallResult =
      overallResult.thenCompose(executionResult -> instrumentation.instrumentExecutionResult(executionResult, i13nExecutionParameters));
    return overallResult;
  }

  private ExecutionResult wrapWithRootFieldName(ExecutionStrategyParameters parameters, ExecutionResult executionResult) {
    String rootFieldName = getRootFieldName(parameters);
    return new ExecutionResultImpl(
      singletonMap(rootFieldName, executionResult.getData()),
      executionResult.getErrors()
    );
  }

  private String getRootFieldName(ExecutionStrategyParameters parameters) {
    Field rootField = parameters.getField().getSingleField();
    return rootField.getResultKey();
  }

  private ExecutionStrategyParameters firstFieldOfSubscriptionSelection(ExecutionStrategyParameters parameters) {
    MergedSelectionSet fields = parameters.getFields();
    MergedField firstField = fields.getSubField(fields.getKeys().get(0));

    ResultPath fieldPath = parameters.getPath().segment(mkNameForPath(firstField.getSingleField()));
    return parameters.transform(builder -> builder.field(firstField).path(fieldPath));
  }

  private ExecutionStepInfo createSubscribedFieldStepInfo(ExecutionContext executionContext, ExecutionStrategyParameters parameters) {
    Field field = parameters.getField().getSingleField();
    GraphQLObjectType parentType = (GraphQLObjectType)parameters.getExecutionStepInfo().getUnwrappedNonNullType();
    GraphQLFieldDefinition fieldDef = getFieldDef(executionContext.getGraphQLSchema(), parentType, field);
    return createExecutionStepInfo(executionContext, parameters, fieldDef, parentType);
  }
}
