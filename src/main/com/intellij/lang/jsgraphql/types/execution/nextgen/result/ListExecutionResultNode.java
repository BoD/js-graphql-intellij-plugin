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
package com.intellij.lang.jsgraphql.types.execution.nextgen.result;

import com.intellij.lang.jsgraphql.types.GraphQLError;
import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.execution.ExecutionStepInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Internal
public class ListExecutionResultNode extends ExecutionResultNode {

  public ListExecutionResultNode(ExecutionStepInfo executionStepInfo,
                                 ResolvedValue resolvedValue,
                                 List<ExecutionResultNode> children) {
    this(executionStepInfo, resolvedValue, children, Collections.emptyList());
  }

  public ListExecutionResultNode(ExecutionStepInfo executionStepInfo,
                                 ResolvedValue resolvedValue,
                                 List<ExecutionResultNode> children,
                                 List<GraphQLError> errors) {
    super(executionStepInfo, resolvedValue, ResultNodesUtil.newNullableException(executionStepInfo, children), children, errors);
  }

  @Override
  public ExecutionResultNode withNewChildren(List<ExecutionResultNode> children) {
    return new ListExecutionResultNode(getExecutionStepInfo(), getResolvedValue(), children, getErrors());
  }

  @Override
  public ExecutionResultNode withNewResolvedValue(ResolvedValue resolvedValue) {
    return new ListExecutionResultNode(getExecutionStepInfo(), resolvedValue, getChildren(), getErrors());
  }

  @Override
  public ExecutionResultNode withNewExecutionStepInfo(ExecutionStepInfo executionStepInfo) {
    return new ListExecutionResultNode(executionStepInfo, getResolvedValue(), getChildren(), getErrors());
  }

  @Override
  public ExecutionResultNode withNewErrors(List<GraphQLError> errors) {
    return new ListExecutionResultNode(getExecutionStepInfo(), getResolvedValue(), getChildren(), new ArrayList<>(errors));
  }
}
