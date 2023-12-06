package com.intellij.lang.jsgraphql.types.language

import com.intellij.lang.jsgraphql.types.PublicApi
import com.intellij.lang.jsgraphql.types.util.TraversalControl
import com.intellij.lang.jsgraphql.types.util.TraverserContext
import com.intellij.psi.PsiElement
import com.intellij.refactoring.extractMethod.newImpl.structures.DataOutput.ArtificialBooleanOutput.nullability
import java.util.function.Consumer

@PublicApi
class ListNullability(
    val nullability: Nullability?,
    sourceLocation: SourceLocation,
    comments: List<Comment>,
    ignoredChars: IgnoredChars,
    additionalData: Map<String, String>,
    element: PsiElement?,
    sourceNodes: List<Node<*>>?,
) : AbstractNode<ListNullability>(
    sourceLocation,
    comments,
    ignoredChars,
    additionalData,
    element,
    sourceNodes,
) {
    override fun getChildren(): List<Node<*>> = if (nullability == null) emptyList() else listOf(nullability)

    override fun getNamedChildren(): NodeChildrenContainer = NodeChildrenContainer
        .newNodeChildrenContainer()
        .child(CHILD_NULLABILITY, nullability)
        .build()

    override fun withNewChildren(newChildren: NodeChildrenContainer): ListNullability {
        return transform {}
    }

    override fun isEqualTo(node: Node<out Node<*>>?): Boolean {
        return this === node || node != null && node::class.java == ListNullability::class.java && (node as ListNullability).designator == designator
    }

    override fun deepCopy(): ListNullability {
        return ListNullability(
            designator,
            sourceLocation,
            comments,
            ignoredChars,
            additionalData,
            element,
            sourceNodes
        )
    }

    override fun accept(context: TraverserContext<Node<Node<*>>>, visitor: NodeVisitor): TraversalControl {
        return visitor.visitNullabilityDesignator(this, context)
    }

    override fun toString(): String {
        return "NullabilityDesignator(designator=$designator)"
    }

    fun transform(builderConsumer: Consumer<ListNullability.Builder?>): ListNullability {
        val builder = ListNullability.Builder(this)
        builderConsumer.accept(builder)
        return builder.build()
    }


    class Builder() : NodeBuilder {
        private var sourceLocation: SourceLocation? = null
        private var comments: MutableList<Comment>? = null
        private var ignoredChars: IgnoredChars? = null
        private var additionalData: MutableMap<String, String>? = null
        private var element: PsiElement? = null
        private var sourceNodes: MutableList<out Node<Node<*>>>? = null

        constructor(existing: ListNullability) : this() {
            sourceLocation = existing.sourceLocation
            comments = existing.comments
            ignoredChars = existing.ignoredChars
            additionalData = existing.additionalData
            element = existing.element
            sourceNodes = existing.sourceNodes
        }

        override fun sourceLocation(sourceLocation: SourceLocation?): NodeBuilder = apply {
            this.sourceLocation = sourceLocation
        }

        override fun comments(comments: MutableList<Comment>?): NodeBuilder = apply {
            this.comments = comments
        }

        override fun ignoredChars(ignoredChars: IgnoredChars?): NodeBuilder = apply {
            this.ignoredChars = ignoredChars
        }

        override fun additionalData(additionalData: MutableMap<String, String>?) = apply {
            this.additionalData = additionalData
        }

        override fun additionalData(key: String?, value: String?): NodeBuilder = apply {
            if (additionalData == null) {
                additionalData = mutableMapOf()
            }
            additionalData!![key!!] = value!!
        }

        override fun element(element: PsiElement?): NodeBuilder = apply {
            this.element = element
        }

        override fun sourceNodes(sourceNodes: MutableList<out Node<Node<*>>>?): NodeBuilder = apply {
            this.sourceNodes = sourceNodes
        }

        fun build(): ListNullability {
            return ListNullability(
                nullability,
                sourceLocation,
                comments,
                ignoredChars,
                additionalData,
                element,
                sourceNodes
            )
        }
    }

    companion object {
        const val CHILD_NULLABILITY = "nullability"
    }
}
