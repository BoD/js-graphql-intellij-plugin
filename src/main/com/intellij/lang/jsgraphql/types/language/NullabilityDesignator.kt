package com.intellij.lang.jsgraphql.types.language

import com.intellij.lang.jsgraphql.types.PublicApi
import com.intellij.lang.jsgraphql.types.util.TraversalControl
import com.intellij.lang.jsgraphql.types.util.TraverserContext
import com.intellij.psi.PsiElement

@PublicApi
class NullabilityDesignator(
    val designator: NullabilityDesignator.Designator,
    sourceLocation: SourceLocation,
    comments: List<Comment>,
    ignoredChars: IgnoredChars,
    additionalData: Map<String, String>,
    element: PsiElement?,
    sourceNodes: List<Node<*>>?,
) : AbstractNode<NullabilityDesignator>(
    sourceLocation,
    comments,
    ignoredChars,
    additionalData,
    element,
    sourceNodes,
) {
    override fun getChildren(): List<Node<*>> = emptyList()

    override fun getNamedChildren(): NodeChildrenContainer = NodeChildrenContainer.newNodeChildrenContainer().build()

    override fun withNewChildren(newChildren: NodeChildrenContainer): NullabilityDesignator {
        NodeUtil.assertNewChildrenAreEmpty(newChildren)
        return this
    }

    override fun isEqualTo(node: Node<out Node<*>>?): Boolean {
        return this === node || node != null && node::class.java == NullabilityDesignator::class.java && (node as NullabilityDesignator).designator == designator
    }

    override fun deepCopy(): NullabilityDesignator {
        return NullabilityDesignator(
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

    class Builder : NodeBuilder {
        private var sourceLocation: SourceLocation? = null
        private var comments: MutableList<Comment>? = null
        private var ignoredChars: IgnoredChars? = null
        private var additionalData: MutableMap<String, String>? = null
        private var element: PsiElement? = null
        private var sourceNodes: MutableList<out Node<Node<*>>>? = null
        private var designator: NullabilityDesignator.Designator? = null

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

        fun build(): NullabilityDesignator {
            return NullabilityDesignator(
                requireNotNull(designator) { "designator can't be null" },
                requireNotNull(sourceLocation) { "sourceLocation can't be null" },
                comments.orEmpty(),
                ignoredChars ?: IgnoredChars.EMPTY,
                additionalData ?: emptyMap(),
                element,
                sourceNodes
            )
        }
    }

    enum class Designator {
        NULLABLE,
        NON_NULLABLE,
    }
}
