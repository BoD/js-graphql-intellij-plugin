@file:JvmName("GraphQLConfigConstants")

package com.intellij.lang.jsgraphql.ide.config

const val CONFIG_OVERRIDE_COMMENT_LEGACY = ".graphqlconfig="
const val CONFIG_OVERRIDE_COMMENT = "config="
const val CONFIG_OVERRIDE_PROJECT_SEP = "!"

const val GRAPHQLCONFIG = ".graphqlconfig"
const val GRAPHQLCONFIG_JSON = ".graphqlconfig.json"
const val GRAPHQLCONFIG_YML = ".graphqlconfig.yml"
const val GRAPHQLCONFIG_YAML = ".graphqlconfig.yaml"

val LEGACY_CONFIG_NAMES: Set<String> = linkedSetOf(
    GRAPHQLCONFIG,
    GRAPHQLCONFIG_JSON,
    GRAPHQLCONFIG_YML,
    GRAPHQLCONFIG_YAML,
)

const val GRAPHQL_CONFIG_JSON = "graphql.config.json"
const val GRAPHQL_CONFIG_YAML = "graphql.config.yaml"
const val GRAPHQL_CONFIG_YML = "graphql.config.yml"
const val GRAPHQL_RC = ".graphqlrc"
const val GRAPHQL_RC_JSON = ".graphqlrc.json"
const val GRAPHQL_RC_YAML = ".graphqlrc.yaml"
const val GRAPHQL_RC_YML = ".graphqlrc.yml"

val MODERN_CONFIG_NAMES: Set<String> = linkedSetOf(
//    "graphql.config.ts",
//    "graphql.config.js",
//    "graphql.config.cjs",
    GRAPHQL_CONFIG_JSON,
    GRAPHQL_CONFIG_YAML,
    GRAPHQL_CONFIG_YML,
//    "graphql.config.toml",
    GRAPHQL_RC,
//    ".graphqlrc.ts",
//    ".graphqlrc.js",
//    ".graphqlrc.cjs",
    GRAPHQL_RC_JSON,
    GRAPHQL_RC_YAML,
    GRAPHQL_RC_YML,
//    ".graphqlrc.toml",
//    "package.json",
)

val CONFIG_NAMES: Set<String> = LinkedHashSet<String>().apply {
    addAll(MODERN_CONFIG_NAMES)
    addAll(LEGACY_CONFIG_NAMES)
}

val OPTIONAL_CONFIG_NAMES = setOf("package.json")
