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
package com.intellij.lang.jsgraphql.types.language;


import com.google.common.collect.ImmutableList;
import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.PublicApi;
import com.intellij.lang.jsgraphql.types.util.TraversalControl;
import com.intellij.lang.jsgraphql.types.util.TraverserContext;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.intellij.lang.jsgraphql.types.Assert.assertNotNull;
import static com.intellij.lang.jsgraphql.types.collect.ImmutableKit.emptyList;
import static com.intellij.lang.jsgraphql.types.collect.ImmutableKit.emptyMap;
import static com.intellij.lang.jsgraphql.types.language.NodeChildrenContainer.newNodeChildrenContainer;
import static com.intellij.lang.jsgraphql.types.language.NodeUtil.assertNewChildrenAreEmpty;

@PublicApi
public class StringValue extends AbstractNode<StringValue> implements ScalarValue<StringValue> {

  private final String value;

  @Internal
  protected StringValue(String value,
                        SourceLocation sourceLocation,
                        List<Comment> comments,
                        IgnoredChars ignoredChars,
                        Map<String, String> additionalData,
                        @Nullable PsiElement element,
                        @Nullable List<? extends Node> sourceNodes
  ) {
    super(sourceLocation, comments, ignoredChars, additionalData, element, sourceNodes);
    this.value = value;
  }

  /**
   * alternative to using a Builder for convenience
   *
   * @param value of the String
   */
  public StringValue(String value) {
    this(value, null, emptyList(), IgnoredChars.EMPTY, emptyMap(), null, null);
  }

  public String getValue() {
    return value;
  }

  @Override
  public List<Node> getChildren() {
    return emptyList();
  }

  @Override
  public NodeChildrenContainer getNamedChildren() {
    return newNodeChildrenContainer().build();
  }

  @Override
  public StringValue withNewChildren(NodeChildrenContainer newChildren) {
    assertNewChildrenAreEmpty(newChildren);
    return this;
  }

  @Override
  public String toString() {
    return "StringValue{" +
           "value='" + value + '\'' +
           '}';
  }

  @Override
  public boolean isEqualTo(Node o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    StringValue that = (StringValue)o;

    return !(value != null ? !value.equals(that.value) : that.value != null);
  }

  @Override
  public StringValue deepCopy() {
    return new StringValue(value, getSourceLocation(), getComments(), getIgnoredChars(), getAdditionalData(), getElement(),
                           getSourceNodes());
  }

  @Override
  public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
    return visitor.visitStringValue(this, context);
  }

  public static Builder newStringValue() {
    return new Builder();
  }

  public static Builder newStringValue(String value) {
    return new Builder().value(value);
  }

  public StringValue transform(Consumer<Builder> builderConsumer) {
    Builder builder = new Builder(this);
    builderConsumer.accept(builder);
    return builder.build();
  }

  public static final class Builder implements NodeBuilder {
    private SourceLocation sourceLocation;
    private String value;
    private ImmutableList<Comment> comments = emptyList();
    private IgnoredChars ignoredChars = IgnoredChars.EMPTY;
    private Map<String, String> additionalData = new LinkedHashMap<>();
    private @Nullable PsiElement element;
    private @Nullable List<? extends Node> sourceNodes;

    private Builder() {
    }

    private Builder(StringValue existing) {
      this.sourceLocation = existing.getSourceLocation();
      this.comments = ImmutableList.copyOf(existing.getComments());
      this.value = existing.getValue();
      this.ignoredChars = existing.getIgnoredChars();
      this.additionalData = new LinkedHashMap<>(existing.getAdditionalData());
      this.element = existing.getElement();
      this.sourceNodes = existing.getSourceNodes();
    }


    public Builder sourceLocation(SourceLocation sourceLocation) {
      this.sourceLocation = sourceLocation;
      return this;
    }

    public Builder value(String value) {
      this.value = value;
      return this;
    }

    public Builder comments(List<Comment> comments) {
      this.comments = ImmutableList.copyOf(comments);
      return this;
    }

    public Builder ignoredChars(IgnoredChars ignoredChars) {
      this.ignoredChars = ignoredChars;
      return this;
    }

    public Builder additionalData(Map<String, String> additionalData) {
      this.additionalData = assertNotNull(additionalData);
      return this;
    }

    public Builder additionalData(String key, String value) {
      this.additionalData.put(key, value);
      return this;
    }

    public Builder element(@Nullable PsiElement element) {
      this.element = element;
      return this;
    }

    public Builder sourceNodes(@Nullable List<? extends Node> sourceNodes) {
      this.sourceNodes = sourceNodes;
      return this;
    }

    public StringValue build() {
      return new StringValue(value, sourceLocation, comments, ignoredChars, additionalData, element, sourceNodes);
    }
  }
}
