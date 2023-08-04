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
package com.intellij.lang.jsgraphql.types.util;

import com.intellij.lang.jsgraphql.types.Internal;

import java.util.function.Supplier;

import static com.intellij.lang.jsgraphql.types.Assert.assertNotNull;

/**
 * This memoizing supplier does NOT use synchronised double locking to set its value
 * so on multiple threads it MAY call the delegate again to get a value.
 *
 * @param <T> for two
 */
@Internal
class IntraThreadMemoizedSupplier<T> implements Supplier<T> {
  private final static Object SENTINEL = new Object() {
  };

  @SuppressWarnings("unchecked")
  private T value = (T)SENTINEL;
  private final Supplier<T> delegate;

  IntraThreadMemoizedSupplier(Supplier<T> delegate) {
    this.delegate = assertNotNull(delegate);
  }

  @Override
  public T get() {
    T t = value;
    if (t == SENTINEL) {
      t = delegate.get();
      value = t;
    }
    return t;
  }
}
