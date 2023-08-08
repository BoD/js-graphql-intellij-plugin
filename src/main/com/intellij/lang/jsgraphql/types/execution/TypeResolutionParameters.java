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

import com.intellij.lang.jsgraphql.types.PublicApi;
import com.intellij.lang.jsgraphql.types.collect.ImmutableMapWithNullValues;
import com.intellij.lang.jsgraphql.types.schema.GraphQLInterfaceType;
import com.intellij.lang.jsgraphql.types.schema.GraphQLSchema;
import com.intellij.lang.jsgraphql.types.schema.GraphQLUnionType;

import java.util.Map;

@PublicApi
public class TypeResolutionParameters {

  private final GraphQLInterfaceType graphQLInterfaceType;
  private final GraphQLUnionType graphQLUnionType;
  private final MergedField field;
  private final Object value;
  private final ImmutableMapWithNullValues<String, Object> argumentValues;
  private final GraphQLSchema schema;
  private final Object context;

  private TypeResolutionParameters(GraphQLInterfaceType graphQLInterfaceType,
                                   GraphQLUnionType graphQLUnionType,
                                   MergedField field,
                                   Object value,
                                   ImmutableMapWithNullValues<String, Object> argumentValues,
                                   GraphQLSchema schema,
                                   final Object context) {
    this.graphQLInterfaceType = graphQLInterfaceType;
    this.graphQLUnionType = graphQLUnionType;
    this.field = field;
    this.value = value;
    this.argumentValues = argumentValues;
    this.schema = schema;
    this.context = context;
  }

  public GraphQLInterfaceType getGraphQLInterfaceType() {
    return graphQLInterfaceType;
  }

  public GraphQLUnionType getGraphQLUnionType() {
    return graphQLUnionType;
  }

  public MergedField getField() {
    return field;
  }

  public Object getValue() {
    return value;
  }

  public Map<String, Object> getArgumentValues() {
    return argumentValues;
  }

  public GraphQLSchema getSchema() {
    return schema;
  }

  public static Builder newParameters() {
    return new Builder();
  }

  public Object getContext() {
    return context;
  }

  public static class Builder {

    private MergedField field;
    private GraphQLInterfaceType graphQLInterfaceType;
    private GraphQLUnionType graphQLUnionType;
    private Object value;
    private ImmutableMapWithNullValues<String, Object> argumentValues;
    private GraphQLSchema schema;
    private Object context;

    public Builder field(MergedField field) {
      this.field = field;
      return this;
    }

    public Builder graphQLInterfaceType(GraphQLInterfaceType graphQLInterfaceType) {
      this.graphQLInterfaceType = graphQLInterfaceType;
      return this;
    }

    public Builder graphQLUnionType(GraphQLUnionType graphQLUnionType) {
      this.graphQLUnionType = graphQLUnionType;
      return this;
    }

    public Builder value(Object value) {
      this.value = value;
      return this;
    }

    public Builder argumentValues(Map<String, Object> argumentValues) {
      this.argumentValues = ImmutableMapWithNullValues.copyOf(argumentValues);
      return this;
    }

    public Builder schema(GraphQLSchema schema) {
      this.schema = schema;
      return this;
    }

    public Builder context(Object context) {
      this.context = context;
      return this;
    }

    public TypeResolutionParameters build() {
      return new TypeResolutionParameters(graphQLInterfaceType, graphQLUnionType, field, value, argumentValues, schema, context);
    }
  }
}
