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

import com.intellij.lang.jsgraphql.types.PublicApi;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@PublicApi
public class Comment implements Serializable {
  public final String content;
  public final SourceLocation sourceLocation;
  private final @Nullable PsiElement element;

  public Comment(String content, SourceLocation sourceLocation) {
    this(content, sourceLocation, null);
  }

  public Comment(String content, SourceLocation sourceLocation, @Nullable PsiElement element) {
    this.content = content;
    this.sourceLocation = sourceLocation;
    this.element = element;
  }

  public String getContent() {
    return content;
  }

  public SourceLocation getSourceLocation() {
    return sourceLocation;
  }

  public @Nullable PsiElement getElement() {
    return element;
  }
}
