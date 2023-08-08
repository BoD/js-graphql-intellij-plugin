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
package com.intellij.lang.jsgraphql.types;

import com.intellij.lang.jsgraphql.types.execution.instrumentation.DocumentAndVariables;
import com.intellij.lang.jsgraphql.types.language.Document;
import com.intellij.lang.jsgraphql.types.parser.InvalidSyntaxException;
import com.intellij.lang.jsgraphql.types.validation.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A result object used in {@link ParseAndValidate} helper that indicates the outcomes of a parse
 * and validate operation.
 */
@PublicApi
public class ParseAndValidateResult {

  private final Document document;
  private final Map<String, Object> variables;
  private final InvalidSyntaxException syntaxException;
  private final List<ValidationError> validationErrors;

  private ParseAndValidateResult(Builder builder) {
    this.document = builder.document;
    this.variables = builder.variables == null ? Collections.emptyMap() : builder.variables;
    this.syntaxException = builder.syntaxException;
    this.validationErrors = builder.validationErrors == null ? Collections.emptyList() : builder.validationErrors;
  }

  /**
   * @return true if there was a parse exception or the validation failed
   */
  public boolean isFailure() {
    return syntaxException != null || !validationErrors.isEmpty();
  }

  /**
   * @return the parsed document or null if its syntactically invalid.
   */
  public Document getDocument() {
    return document;
  }

  /**
   * @return the document variables or null if its syntactically invalid.
   */
  public Map<String, Object> getVariables() {
    return variables;
  }

  /**
   * @return the parsed document and variables or null if its syntactically invalid.
   */
  public DocumentAndVariables getDocumentAndVariables() {
    if (document != null) {
      return DocumentAndVariables.newDocumentAndVariables().document(document).variables(variables).build();
    }
    return null;
  }

  /**
   * @return the syntax exception or null if its syntactically valid.
   */
  public InvalidSyntaxException getSyntaxException() {
    return syntaxException;
  }

  /**
   * @return a list of validation errors, which might be empty if its syntactically invalid.
   */
  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  /**
   * A list of all the errors (parse and validate) that have occurred
   *
   * @return the errors that have occurred or empty list if there are none
   */
  public List<GraphQLError> getErrors() {
    List<GraphQLError> errors = new ArrayList<>();
    if (syntaxException != null) {
      errors.add(syntaxException.toInvalidSyntaxError());
    }
    errors.addAll(validationErrors);
    return errors;
  }

  public ParseAndValidateResult transform(Consumer<Builder> builderConsumer) {
    Builder builder = new Builder()
      .document(document).variables(variables).syntaxException(syntaxException).validationErrors(validationErrors);
    builderConsumer.accept(builder);
    return builder.build();
  }

  public static Builder newResult() {
    return new Builder();
  }

  public static class Builder {
    private Document document;
    private Map<String, Object> variables = Collections.emptyMap();
    private InvalidSyntaxException syntaxException;
    private List<ValidationError> validationErrors = Collections.emptyList();

    public Builder document(Document document) {
      this.document = document;
      return this;
    }

    public Builder variables(Map<String, Object> variables) {
      this.variables = variables;
      return this;
    }

    public Builder validationErrors(List<ValidationError> validationErrors) {
      this.validationErrors = validationErrors;
      return this;
    }

    public Builder syntaxException(InvalidSyntaxException syntaxException) {
      this.syntaxException = syntaxException;
      return this;
    }

    public ParseAndValidateResult build() {
      return new ParseAndValidateResult(this);
    }
  }
}
