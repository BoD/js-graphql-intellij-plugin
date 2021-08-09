/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.lang.jsgraphql.ide.project.graphqlconfig;

import com.intellij.lang.jsgraphql.ide.project.graphqlconfig.model.GraphQLResolvedConfigData;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.scope.packageSet.NamedScope;
import com.intellij.psi.search.scope.packageSet.PackageSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Named scope for use with multiple GraphQL schemas.
 * Implements hash and equals to work in computed maps.
 */
public class GraphQLNamedScope extends NamedScope {

    private VirtualFile myConfigBaseDir;
    private GraphQLResolvedConfigData myConfigData;

    public GraphQLNamedScope(@NotNull String scopeId, @Nullable GraphQLConfigPackageSet value) {
        super(scopeId, value);
        if (value != null) {
            myConfigData = value.getConfigData();
            myConfigBaseDir = value.getConfigBaseDir();
        }
    }

    public GraphQLResolvedConfigData getConfigData() {
        return myConfigData;
    }

    public VirtualFile getConfigBaseDir() {
        return myConfigBaseDir;
    }

    public GraphQLConfigPackageSet getPackageSet() {
        return (GraphQLConfigPackageSet) getValue();
    }

    @NotNull
    @Override
    public NamedScope createCopy() {
        final PackageSet value = getValue();
        return new GraphQLNamedScope(getScopeId(), value != null ? (GraphQLConfigPackageSet) value.createCopy() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GraphQLNamedScope that = (GraphQLNamedScope) o;
        return Objects.equals(getScopeId(), that.getScopeId()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScopeId(), getValue());
    }

}
