/*
 * Copyright (c) 2018-present, Jim Kynde Meyer
 * All rights reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.intellij.lang.jsgraphql.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jsgraphql.psi.*;
import com.intellij.lang.jsgraphql.schema.GraphQLSchemaProvider;
import com.intellij.lang.jsgraphql.types.schema.GraphQLList;
import com.intellij.lang.jsgraphql.types.schema.GraphQLNonNull;
import com.intellij.lang.jsgraphql.types.schema.GraphQLSchema;
import com.intellij.lang.jsgraphql.types.schema.GraphQLType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public abstract class GraphQLInputValueDefinitionMixin extends GraphQLNamedElementImpl
  implements GraphQLInputValueDefinition, GraphQLTypeScopeProvider {

  public GraphQLInputValueDefinitionMixin(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public GraphQLType getTypeScope() {
    final com.intellij.lang.jsgraphql.psi.GraphQLType psiType = getType();
    if (psiType != null) {
      final GraphQLIdentifier typeIdentifier = PsiTreeUtil.findChildOfType(psiType, GraphQLIdentifier.class);
      if (typeIdentifier != null) {
        final GraphQLSchema schema = GraphQLSchemaProvider.getInstance(getProject()).getSchemaInfo(this).getSchema();
        GraphQLType schemaType = schema.getType(typeIdentifier.getText());
        if (schemaType != null) {
          GraphQLElement parent = typeIdentifier;
          while (parent != null && parent != psiType) {
            if (parent instanceof GraphQLListType) {
              schemaType = new GraphQLList(schemaType);
            }
            else if (parent instanceof GraphQLNonNullType) {
              schemaType = new GraphQLNonNull(schemaType);
            }
            parent = PsiTreeUtil.getParentOfType(parent, GraphQLElement.class);
          }
        }
        return schemaType;
      }
    }
    return null;
  }
}
