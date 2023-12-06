package com.intellij.lang.jsgraphql.types.language

import com.intellij.lang.jsgraphql.types.PublicApi
import com.intellij.lang.jsgraphql.types.util.TraversalControl
import com.intellij.lang.jsgraphql.types.util.TraverserContext
import com.intellij.psi.PsiElement

@PublicApi
class Nullability(
    listNullability: ListNullability,
    nullabilityDesignator: NullabilityDesignator,
    sourceLocation: SourceLocation,
    comments: List<Comment>,
    ignoredChars: IgnoredChars,
    additionalData: Map<String, String>,
    element: PsiElement?,
    sourceNodes: List<Node<*>>?,
) : AbstractNode<Nullability>(
    sourceLocation,
    comments,
    ignoredChars,
    additionalData,
    element,
    sourceNodes,
) {
    override fun getChildren(): List<Node<Node<*>>> {
        TODO("Not yet implemented")
    }

    override fun getNamedChildren(): NodeChildrenContainer {
        TODO("Not yet implemented")
    }

    override fun withNewChildren(newChildren: NodeChildrenContainer?): Nullability {
        TODO("Not yet implemented")
    }

    override fun isEqualTo(node: Node<out Node<*>>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun deepCopy(): Nullability {
        TODO("Not yet implemented")
    }

    override fun accept(context: TraverserContext<Node<Node<*>>>, visitor: NodeVisitor): TraversalControl {
        TODO("Not yet implemented")
    }

    class Builder : NodeBuilder {
        private var sourceLocation: SourceLocation? = null
        private var comments: MutableList<Comment>? = null
        private var ignoredChars: IgnoredChars? = null
        private var additionalData: MutableMap<String, String>? = null
        private var element: PsiElement? = null
        private var sourceNodes: MutableList<out Node<Node<*>>>? = null

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
    }
}
