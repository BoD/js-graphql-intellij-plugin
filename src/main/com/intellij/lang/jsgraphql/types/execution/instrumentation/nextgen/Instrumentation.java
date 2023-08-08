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
package com.intellij.lang.jsgraphql.types.execution.instrumentation.nextgen;

import com.intellij.lang.jsgraphql.types.ExecutionInput;
import com.intellij.lang.jsgraphql.types.ExecutionResult;
import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.DocumentAndVariables;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.InstrumentationContext;
import com.intellij.lang.jsgraphql.types.execution.instrumentation.InstrumentationState;
import com.intellij.lang.jsgraphql.types.language.Document;
import com.intellij.lang.jsgraphql.types.schema.GraphQLSchema;
import com.intellij.lang.jsgraphql.types.validation.ValidationError;

import java.util.List;

import static com.intellij.lang.jsgraphql.types.execution.instrumentation.SimpleInstrumentationContext.noOp;

@Internal
public interface Instrumentation {

  default InstrumentationState createState(InstrumentationCreateStateParameters parameters) {
    return new InstrumentationState() {
    };
  }

  default ExecutionInput instrumentExecutionInput(ExecutionInput executionInput, InstrumentationExecutionParameters parameters) {
    return executionInput;
  }

  default DocumentAndVariables instrumentDocumentAndVariables(DocumentAndVariables documentAndVariables,
                                                              InstrumentationExecutionParameters parameters) {
    return documentAndVariables;
  }

  default GraphQLSchema instrumentSchema(GraphQLSchema graphQLSchema, InstrumentationExecutionParameters parameters) {
    return graphQLSchema;
  }

  default ExecutionResult instrumentExecutionResult(ExecutionResult result, InstrumentationExecutionParameters parameters) {
    return result;
  }

  default InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
    return noOp();
  }

  default InstrumentationContext<Document> beginParse(InstrumentationExecutionParameters parameters) {
    return noOp();
  }

  default InstrumentationContext<List<ValidationError>> beginValidation(InstrumentationValidationParameters parameters) {
    return noOp();
  }
}
