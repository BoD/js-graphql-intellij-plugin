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
import com.intellij.lang.jsgraphql.types.collect.ImmutableKit;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.intellij.lang.jsgraphql.types.Assert.assertNotNull;
import static com.intellij.lang.jsgraphql.types.collect.ImmutableKit.emptyList;

@PublicApi
public class EnumTypeExtensionDefinition extends EnumTypeDefinition {

  @Internal
  protected EnumTypeExtensionDefinition(String name,
                                        List<EnumValueDefinition> enumValueDefinitions,
                                        List<Directive> directives,
                                        Description description,
                                        SourceLocation sourceLocation,
                                        List<Comment> comments,
                                        IgnoredChars ignoredChars,
                                        Map<String, String> additionalData,
                                        @Nullable PsiElement element,
                                        @Nullable List<? extends Node> sourceNodes) {
    super(name, enumValueDefinitions, directives, description,
          sourceLocation, comments, ignoredChars, additionalData, element, sourceNodes);
  }

  @Override
  public EnumTypeExtensionDefinition deepCopy() {
    return new EnumTypeExtensionDefinition(getName(),
                                           deepCopy(getEnumValueDefinitions()),
                                           deepCopy(getDirectives()),
                                           getDescription(),
                                           getSourceLocation(),
                                           getComments(),
                                           getIgnoredChars(),
                                           getAdditionalData(),
                                           getElement(),
                                           getSourceNodes());
  }

  @Override
  public String toString() {
    return "EnumTypeDefinition{" +
           "name='" + getName() + '\'' +
           ", enumValueDefinitions=" + getEnumValueDefinitions() +
           ", directives=" + getDirectives() +
           '}';
  }

  public static Builder newEnumTypeExtensionDefinition() {
    return new Builder();
  }

  @Override
  public EnumTypeExtensionDefinition withNewChildren(NodeChildrenContainer newChildren) {
    return transformExtension(builder -> builder
      .enumValueDefinitions(newChildren.getChildren(CHILD_ENUM_VALUE_DEFINITIONS))
      .directives(newChildren.getChildren(CHILD_DIRECTIVES))
    );
  }

  public EnumTypeExtensionDefinition transformExtension(Consumer<Builder> builderConsumer) {
    Builder builder = new Builder(this);
    builderConsumer.accept(builder);
    return builder.build();
  }

  public static final class Builder implements NodeDirectivesBuilder {
    private SourceLocation sourceLocation;
    private ImmutableList<Comment> comments = emptyList();
    private String name;
    private Description description;
    private ImmutableList<EnumValueDefinition> enumValueDefinitions = emptyList();
    private ImmutableList<Directive> directives = emptyList();
    private IgnoredChars ignoredChars = IgnoredChars.EMPTY;
    private Map<String, String> additionalData = new LinkedHashMap<>();
    private @Nullable PsiElement element;
    private @Nullable List<? extends Node> sourceNodes;

    private Builder() {
    }

    private Builder(EnumTypeExtensionDefinition existing) {
      this.sourceLocation = existing.getSourceLocation();
      this.comments = ImmutableList.copyOf(existing.getComments());
      this.name = existing.getName();
      this.description = existing.getDescription();
      this.directives = ImmutableList.copyOf(existing.getDirectives());
      this.enumValueDefinitions = ImmutableList.copyOf(existing.getEnumValueDefinitions());
      this.ignoredChars = existing.getIgnoredChars();
      this.additionalData = new LinkedHashMap<>(existing.getAdditionalData());
      this.element = existing.getElement();
      this.sourceNodes = existing.getSourceNodes();
    }

    public Builder sourceLocation(SourceLocation sourceLocation) {
      this.sourceLocation = sourceLocation;
      return this;
    }

    public Builder comments(List<Comment> comments) {
      this.comments = ImmutableList.copyOf(comments);
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(Description description) {
      this.description = description;
      return this;
    }

    public Builder enumValueDefinitions(List<EnumValueDefinition> enumValueDefinitions) {
      this.enumValueDefinitions = ImmutableList.copyOf(enumValueDefinitions);
      return this;
    }

    @Override
    public Builder directives(List<Directive> directives) {
      this.directives = ImmutableList.copyOf(directives);
      return this;
    }

    public Builder directive(Directive directive) {
      this.directives = ImmutableKit.addToList(directives, directive);
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


    public EnumTypeExtensionDefinition build() {
      return new EnumTypeExtensionDefinition(name,
                                             enumValueDefinitions,
                                             directives,
                                             description,
                                             sourceLocation,
                                             comments,
                                             ignoredChars,
                                             additionalData,
                                             element,
                                             sourceNodes);
    }
  }
}
