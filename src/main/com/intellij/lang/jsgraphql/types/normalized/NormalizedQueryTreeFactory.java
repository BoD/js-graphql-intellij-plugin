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
package com.intellij.lang.jsgraphql.types.normalized;

import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.execution.MergedField;
import com.intellij.lang.jsgraphql.types.execution.nextgen.Common;
import com.intellij.lang.jsgraphql.types.language.*;
import com.intellij.lang.jsgraphql.types.normalized.FieldCollectorNormalizedQuery.CollectFieldResult;
import com.intellij.lang.jsgraphql.types.schema.FieldCoordinates;
import com.intellij.lang.jsgraphql.types.schema.GraphQLObjectType;
import com.intellij.lang.jsgraphql.types.schema.GraphQLSchema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Internal
public class NormalizedQueryTreeFactory {

  public static NormalizedQueryTree createNormalizedQuery(GraphQLSchema graphQLSchema,
                                                          Document document,
                                                          String operationName,
                                                          Map<String, Object> variables) {
    NodeUtil.GetOperationResult getOperationResult = NodeUtil.getOperation(document, operationName);
    return createNormalizedQuery(graphQLSchema, getOperationResult.operationDefinition, getOperationResult.fragmentsByName, variables);
  }


  public static NormalizedQueryTree createNormalizedQuery(GraphQLSchema graphQLSchema,
                                                          OperationDefinition operationDefinition,
                                                          Map<String, FragmentDefinition> fragments,
                                                          Map<String, Object> variables) {
    return new NormalizedQueryTreeFactory().createNormalizedQueryImpl(graphQLSchema, operationDefinition, fragments, variables);
  }

  /**
   * Creates a new Query execution tree for the provided query
   */
  private NormalizedQueryTree createNormalizedQueryImpl(GraphQLSchema graphQLSchema,
                                                        OperationDefinition operationDefinition,
                                                        Map<String, FragmentDefinition> fragments,
                                                        Map<String, Object> variables) {


    FieldCollectorNormalizedQuery fieldCollector = new FieldCollectorNormalizedQuery();
    FieldCollectorNormalizedQueryParams parameters = FieldCollectorNormalizedQueryParams
      .newParameters()
      .fragments(fragments)
      .schema(graphQLSchema)
      .variables(variables)
      .build();

    GraphQLObjectType rootType = Common.getOperationRootType(graphQLSchema, operationDefinition);
    CollectFieldResult topLevelFields = fieldCollector.collectFromOperation(parameters, operationDefinition, rootType);

    Map<Field, List<NormalizedField>> fieldToNormalizedField = new LinkedHashMap<>();
    Map<NormalizedField, MergedField> normalizedFieldToMergedField = new LinkedHashMap<>();
    Map<FieldCoordinates, List<NormalizedField>> coordinatesToNormalizedFields = new LinkedHashMap<>();

    List<NormalizedField> realRoots = new ArrayList<>();

    for (NormalizedField topLevel : topLevelFields.getChildren()) {

      MergedField mergedField = topLevelFields.getMergedFieldByNormalized().get(topLevel);
      NormalizedField realTopLevel =
        buildFieldWithChildren(topLevel, mergedField, fieldCollector, parameters, fieldToNormalizedField, normalizedFieldToMergedField,
                               coordinatesToNormalizedFields, 1);
      fixUpParentReference(realTopLevel);

      normalizedFieldToMergedField.put(realTopLevel, mergedField);
      FieldCoordinates coordinates = FieldCoordinates.coordinates(realTopLevel.getObjectType(), realTopLevel.getFieldDefinition());
      coordinatesToNormalizedFields.computeIfAbsent(coordinates, k -> new ArrayList<>()).add(realTopLevel);
      updateByAstFieldMap(realTopLevel, mergedField, fieldToNormalizedField);
      realRoots.add(realTopLevel);
    }
    return new NormalizedQueryTree(realRoots, fieldToNormalizedField, normalizedFieldToMergedField, coordinatesToNormalizedFields);
  }

  private void fixUpParentReference(NormalizedField rootNormalizedField) {
    for (NormalizedField child : rootNormalizedField.getChildren()) {
      child.replaceParent(rootNormalizedField);
    }
  }


  private NormalizedField buildFieldWithChildren(NormalizedField field,
                                                 MergedField mergedField,
                                                 FieldCollectorNormalizedQuery fieldCollector,
                                                 FieldCollectorNormalizedQueryParams fieldCollectorNormalizedQueryParams,
                                                 Map<Field, List<NormalizedField>> fieldToMergedField,
                                                 Map<NormalizedField, MergedField> normalizedFieldToMergedField,
                                                 Map<FieldCoordinates, List<NormalizedField>> coordinatesToNormalizedFields,
                                                 int curLevel) {
    CollectFieldResult fieldsWithoutChildren =
      fieldCollector.collectFields(fieldCollectorNormalizedQueryParams, field, mergedField, curLevel + 1);
    List<NormalizedField> realChildren = new ArrayList<>();
    for (NormalizedField fieldWithoutChildren : fieldsWithoutChildren.getChildren()) {
      MergedField mergedFieldForChild = fieldsWithoutChildren.getMergedFieldByNormalized().get(fieldWithoutChildren);
      NormalizedField realChild =
        buildFieldWithChildren(fieldWithoutChildren, mergedFieldForChild, fieldCollector, fieldCollectorNormalizedQueryParams,
                               fieldToMergedField, normalizedFieldToMergedField, coordinatesToNormalizedFields, curLevel + 1);
      fixUpParentReference(realChild);

      normalizedFieldToMergedField.put(realChild, mergedFieldForChild);
      FieldCoordinates coordinates = FieldCoordinates.coordinates(realChild.getObjectType(), realChild.getFieldDefinition());
      coordinatesToNormalizedFields.computeIfAbsent(coordinates, k -> new ArrayList<>()).add(realChild);

      realChildren.add(realChild);

      updateByAstFieldMap(realChild, mergedFieldForChild, fieldToMergedField);
    }
    return field.transform(builder -> builder.children(realChildren));
  }

  private void updateByAstFieldMap(NormalizedField normalizedField,
                                   MergedField mergedField,
                                   Map<Field, List<NormalizedField>> fieldToNormalizedField) {
    for (Field astField : mergedField.getFields()) {
      fieldToNormalizedField.computeIfAbsent(astField, ignored -> new ArrayList<>()).add(normalizedField);
    }
  }
}
