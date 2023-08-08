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
package com.intellij.lang.jsgraphql.types.analysis;

import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.language.Value;
import com.intellij.lang.jsgraphql.types.schema.GraphQLArgument;
import com.intellij.lang.jsgraphql.types.schema.GraphQLInputObjectField;
import com.intellij.lang.jsgraphql.types.schema.GraphQLInputType;
import com.intellij.lang.jsgraphql.types.schema.GraphQLInputValueDefinition;

@Internal
public class QueryVisitorFieldArgumentInputValueImpl implements QueryVisitorFieldArgumentInputValue {
  private final GraphQLInputValueDefinition inputValueDefinition;
  private final Value value;
  private final QueryVisitorFieldArgumentInputValue parent;

  private QueryVisitorFieldArgumentInputValueImpl(QueryVisitorFieldArgumentInputValue parent,
                                                  GraphQLInputValueDefinition inputValueDefinition,
                                                  Value value) {
    this.parent = parent;
    this.inputValueDefinition = inputValueDefinition;
    this.value = value;
  }

  static QueryVisitorFieldArgumentInputValue incompleteArgumentInputValue(GraphQLArgument graphQLArgument) {
    return new QueryVisitorFieldArgumentInputValueImpl(
      null, graphQLArgument, null);
  }

  QueryVisitorFieldArgumentInputValueImpl incompleteNewChild(GraphQLInputObjectField inputObjectField) {
    return new QueryVisitorFieldArgumentInputValueImpl(
      this, inputObjectField, null);
  }

  QueryVisitorFieldArgumentInputValueImpl completeArgumentInputValue(Value<?> value) {
    return new QueryVisitorFieldArgumentInputValueImpl(
      this.parent, this.inputValueDefinition, value);
  }


  @Override
  public QueryVisitorFieldArgumentInputValue getParent() {
    return parent;
  }

  public GraphQLInputValueDefinition getInputValueDefinition() {
    return inputValueDefinition;
  }

  @Override
  public String getName() {
    return inputValueDefinition.getName();
  }

  @Override
  public GraphQLInputType getInputType() {
    return inputValueDefinition.getType();
  }

  @Override
  public Value getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "QueryVisitorFieldArgumentInputValueImpl{" +
           "inputValue=" + inputValueDefinition +
           ", value=" + value +
           '}';
  }
}
