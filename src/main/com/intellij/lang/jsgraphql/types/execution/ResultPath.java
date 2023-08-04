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

import com.google.common.collect.ImmutableList;
import com.intellij.lang.jsgraphql.types.Assert;
import com.intellij.lang.jsgraphql.types.AssertException;
import com.intellij.lang.jsgraphql.types.PublicApi;
import com.intellij.lang.jsgraphql.types.collect.ImmutableKit;

import java.util.*;

import static com.intellij.lang.jsgraphql.types.Assert.assertNotNull;
import static com.intellij.lang.jsgraphql.types.Assert.assertTrue;
import static java.lang.String.format;


/**
 * As a graphql query is executed, each field forms a hierarchical path from parent field to child field and this
 * class represents that path as a series of segments.
 */
@PublicApi
public class ResultPath {
  private static final ResultPath ROOT_PATH = new ResultPath();

  /**
   * All paths start from here
   *
   * @return the root path
   */
  public static ResultPath rootPath() {
    return ROOT_PATH;
  }

  private final ResultPath parent;
  private final Object segment;

  // hash is effective immutable but lazily initialized similar to the hash code of java.lang.String
  private int hash;

  private ResultPath() {
    parent = null;
    segment = null;
  }

  private ResultPath(ResultPath parent, String segment) {
    this.parent = assertNotNull(parent, () -> "Must provide a parent path");
    this.segment = assertNotNull(segment, () -> "Must provide a sub path");
  }

  private ResultPath(ResultPath parent, int segment) {
    this.parent = assertNotNull(parent, () -> "Must provide a parent path");
    this.segment = segment;
  }

  public int getLevel() {
    int counter = 0;
    ResultPath currentPath = this;
    while (currentPath != null) {
      if (currentPath.segment instanceof String) {
        counter++;
      }
      currentPath = currentPath.parent;
    }
    return counter;
  }

  public ResultPath getPathWithoutListEnd() {
    if (ROOT_PATH.equals(this)) {
      return ROOT_PATH;
    }
    if (segment instanceof String) {
      return this;
    }
    return parent;
  }

  /**
   * @return true if the end of the path has a list style segment eg 'a/b[2]'
   */
  public boolean isListSegment() {
    return segment instanceof Integer;
  }

  /**
   * @return true if the end of the path has a named style segment eg 'a/b[2]/c'
   */
  public boolean isNamedSegment() {
    return segment instanceof String;
  }


  public String getSegmentName() {
    return (String)segment;
  }

  public int getSegmentIndex() {
    return (int)segment;
  }

  public Object getSegmentValue() {
    return segment;
  }

  public ResultPath getParent() {
    return parent;
  }

  /**
   * Parses an execution path from the provided path string in the format /segment1/segment2[index]/segmentN
   *
   * @param pathString the path string
   * @return a parsed execution path
   */
  public static ResultPath parse(String pathString) {
    pathString = pathString == null ? "" : pathString;
    String finalPathString = pathString.trim();
    StringTokenizer st = new StringTokenizer(finalPathString, "/[]", true);
    ResultPath path = ResultPath.rootPath();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if ("/".equals(token)) {
        assertTrue(st.hasMoreTokens(), () -> String.format(mkErrMsg(), finalPathString));
        path = path.segment(st.nextToken());
      }
      else if ("[".equals(token)) {
        assertTrue(st.countTokens() >= 2, () -> String.format(mkErrMsg(), finalPathString));
        path = path.segment(Integer.parseInt(st.nextToken()));
        String closingBrace = st.nextToken();
        assertTrue(closingBrace.equals("]"), () -> String.format(mkErrMsg(), finalPathString));
      }
      else {
        throw new AssertException(format(mkErrMsg(), pathString));
      }
    }
    return path;
  }

  /**
   * This will create an execution path from the list of objects
   *
   * @param objects the path objects
   * @return a new execution path
   */
  public static ResultPath fromList(List<?> objects) {
    assertNotNull(objects);
    ResultPath path = ResultPath.rootPath();
    for (Object object : objects) {
      if (object instanceof String) {
        path = path.segment(((String)object));
      }
      else {
        path = path.segment((int)object);
      }
    }
    return path;
  }

  private static String mkErrMsg() {
    return "Invalid path string : '%s'";
  }

  /**
   * Takes the current path and adds a new segment to it, returning a new path
   *
   * @param segment the string path segment to add
   * @return a new path containing that segment
   */
  public ResultPath segment(String segment) {
    return new ResultPath(this, segment);
  }

  /**
   * Takes the current path and adds a new segment to it, returning a new path
   *
   * @param segment the int path segment to add
   * @return a new path containing that segment
   */
  public ResultPath segment(int segment) {
    return new ResultPath(this, segment);
  }

  /**
   * Drops the last segment off the path
   *
   * @return a new path with the last segment dropped off
   */
  public ResultPath dropSegment() {
    if (this == rootPath()) {
      return null;
    }
    return this.parent;
  }

  /**
   * Replaces the last segment on the path eg ResultPath.parse("/a/b[1]").replaceSegment(9)
   * equals "/a/b[9]"
   *
   * @param segment the integer segment to use
   * @return a new path with the last segment replaced
   */
  public ResultPath replaceSegment(int segment) {
    Assert.assertTrue(!ROOT_PATH.equals(this), () -> "You MUST not call this with the root path");
    return new ResultPath(parent, segment);
  }

  /**
   * Replaces the last segment on the path eg ResultPath.parse("/a/b[1]").replaceSegment("x")
   * equals "/a/b/x"
   *
   * @param segment the string segment to use
   * @return a new path with the last segment replaced
   */
  public ResultPath replaceSegment(String segment) {
    Assert.assertTrue(!ROOT_PATH.equals(this), () -> "You MUST not call this with the root path");
    return new ResultPath(parent, segment);
  }


  /**
   * @return true if the path is the {@link #rootPath()}
   */
  public boolean isRootPath() {
    return this == ROOT_PATH;
  }

  /**
   * Appends the provided path to the current one
   *
   * @param path the path to append
   * @return a new path
   */
  public ResultPath append(ResultPath path) {
    List<Object> objects = new ArrayList<>(this.toList());
    objects.addAll(assertNotNull(path).toList());
    return fromList(objects);
  }


  public ResultPath sibling(String siblingField) {
    Assert.assertTrue(!ROOT_PATH.equals(this), () -> "You MUST not call this with the root path");
    return new ResultPath(this.parent, siblingField);
  }

  public ResultPath sibling(int siblingField) {
    Assert.assertTrue(!ROOT_PATH.equals(this), () -> "You MUST not call this with the root path");
    return new ResultPath(this.parent, siblingField);
  }

  /**
   * @return converts the path into a list of segments
   */
  public List<Object> toList() {
    if (parent == null) {
      return ImmutableKit.emptyList();
    }
    LinkedList<Object> list = new LinkedList<>();
    ResultPath p = this;
    while (p.segment != null) {
      list.addFirst(p.segment);
      p = p.parent;
    }
    return ImmutableList.copyOf(list);
  }

  /**
   * @return this path as a list of result keys, without any indices
   */
  public List<String> getKeysOnly() {
    if (parent == null) {
      return new LinkedList<>();
    }
    LinkedList<String> list = new LinkedList<>();
    ResultPath p = this;
    while (p.segment != null) {
      if (p.segment instanceof String) {
        list.addFirst((String)p.segment);
      }
      p = p.parent;
    }
    return list;
  }


  /**
   * @return the path as a string which represents the call hierarchy
   */
  @Override
  public String toString() {
    if (parent == null) {
      return "";
    }

    if (ROOT_PATH.equals(parent)) {
      return segmentToString();
    }

    return parent.toString() + segmentToString();
  }

  public String segmentToString() {
    if (segment instanceof String) {
      return "/" + segment;
    }
    else {
      return "[" + segment + "]";
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResultPath self = this;
    ResultPath that = (ResultPath)o;
    while (self.segment != null && that.segment != null) {
      if (!Objects.equals(self.segment, that.segment)) {
        return false;
      }
      self = self.parent;
      that = that.parent;
    }

    return self.isRootPath() && that.isRootPath();
  }

  @Override
  public int hashCode() {
    int h = hash;
    if (h == 0) {
      h = 1;
      ResultPath self = this;
      while (self != null) {
        Object value = self.segment;
        h = 31 * h + (value == null ? 0 : value.hashCode());
        self = self.parent;
      }
      hash = h;
    }
    return h;
  }
}
