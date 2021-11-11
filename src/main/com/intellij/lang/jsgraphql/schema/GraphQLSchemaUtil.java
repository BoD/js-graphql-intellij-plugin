/*
 * Copyright (c) 2018-present, Jim Kynde Meyer
 * All rights reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.intellij.lang.jsgraphql.schema;

import com.intellij.lang.jsgraphql.types.language.*;
import com.intellij.lang.jsgraphql.types.schema.*;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static com.intellij.lang.jsgraphql.types.schema.GraphQLTypeUtil.*;

public class GraphQLSchemaUtil {

    /**
     * Provides the IDL string version of a type including handling of types wrapped in non-null/list-types
     */
    public static String typeString(@NotNull GraphQLType rawType) {

        final StringBuilder sb = new StringBuilder();
        final Stack<String> stack = new Stack<>();

        GraphQLType type = rawType;
        while (true) {
            if (type instanceof GraphQLNonNull) {
                type = ((GraphQLNonNull) type).getWrappedType();
                stack.push("!");
            } else if (type instanceof GraphQLList) {
                type = ((GraphQLList) type).getWrappedType();
                sb.append("[");
                stack.push("]");
            } else if (type instanceof GraphQLUnmodifiedType) {
                sb.append(((GraphQLUnmodifiedType) type).getName());
                break;
            } else {
                sb.append(type.toString());
                break;
            }
        }
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }

        return sb.toString();

    }

    @Nullable
    public static String getTypeDescription(@NotNull GraphQLType graphQLType) {
        String description = null;
        if (graphQLType instanceof GraphQLObjectType) {
            description = ((GraphQLObjectType) graphQLType).getDescription();
        } else if (graphQLType instanceof GraphQLInterfaceType) {
            description = ((GraphQLInterfaceType) graphQLType).getDescription();
        } else if (graphQLType instanceof GraphQLEnumType) {
            description = ((GraphQLEnumType) graphQLType).getDescription();
        } else if (graphQLType instanceof GraphQLScalarType) {
            description = ((GraphQLScalarType) graphQLType).getDescription();
        } else if (graphQLType instanceof GraphQLInputObjectType) {
            description = ((GraphQLInputObjectType) graphQLType).getDescription();
        } else if (graphQLType instanceof GraphQLUnionType) {
            description = ((GraphQLUnionType) graphQLType).getDescription();
        }
        return description;
    }

    public static @NotNull String getTypeName(@Nullable GraphQLType type) {
        String typeName = null;
        if (type instanceof GraphQLNamedSchemaElement) {
            typeName = ((GraphQLNamedSchemaElement) type).getName();
        }
        return StringUtil.notNullize(typeName);
    }

    /**
     * Gets the raw named type that sits within a non-null/list modifier type, or the type as-is if no unwrapping is needed
     *
     * @param graphQLType the type to unwrap
     * @return the raw type as-is, or the type wrapped inside a non-null/list modifier type
     */
    public static GraphQLUnmodifiedType getUnmodifiedType(GraphQLType graphQLType) {
        if (graphQLType instanceof GraphQLModifiedType) {
            return getUnmodifiedType(((GraphQLModifiedType) graphQLType).getWrappedType());
        }
        return (GraphQLUnmodifiedType) graphQLType;
    }

    public static GraphQLType unwrapListType(GraphQLType type) {
        while (isWrapped(type)) {
            if (isList(type)) {
                return unwrapOne(type);
            }
            type = unwrapOne(type);
        }
        return type;
    }

    public static @NotNull String getValueTypeName(@NotNull Object value) {
        if (value instanceof IntValue) {
            return "Int";
        } else if (value instanceof FloatValue) {
            return "Float";
        } else if (value instanceof StringValue) {
            return "String";
        } else if (value instanceof EnumValue) {
            return "Enum";
        } else if (value instanceof BooleanValue) {
            return "Boolean";
        } else if (value instanceof NullValue) {
            return "null";
        } else if (value instanceof ArrayValue) {
            return "Array";
        } else if (value instanceof ObjectValue) {
            return "Object";
        } else if (value instanceof VariableReference) {
            return "Reference";
        }
        return value.getClass().getSimpleName();
    }

    @NotNull
    public static Set<String> getSchemaOperationTypeNames(@NotNull GraphQLSchema schema) {
        HashSet<String> types = new HashSet<>();

        GraphQLObjectType queryType = schema.getQueryType();
        types.add(queryType != null && queryType.getName() != null
            ? queryType.getName() : GraphQLKnownTypes.QUERY_TYPE);

        GraphQLObjectType mutationType = schema.getMutationType();
        types.add(mutationType != null && mutationType.getName() != null
            ? mutationType.getName() : GraphQLKnownTypes.MUTATION_TYPE);

        GraphQLObjectType subscriptionType = schema.getSubscriptionType();
        types.add(subscriptionType != null && subscriptionType.getName() != null
            ? subscriptionType.getName() : GraphQLKnownTypes.SUBSCRIPTION_TYPE);

        return types;
    }
}
