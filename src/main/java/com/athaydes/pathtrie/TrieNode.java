package com.athaydes.pathtrie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class TrieNode<E> {

    private final Map<String, TrieNode<E>> childrenByPath = new HashMap<>(2);
    private ParameterizedTrieNode parameterizedNode;

    private final TrieNode<E> parent;
    private E element;

    TrieNode(TrieNode<E> parent) {
        this.parent = parent;
    }

    Optional<TrieNode<E>> getParent() {
        return Optional.ofNullable(parent);
    }

    Optional<TrieNode<E>> get(String pathPart, boolean resolveParameters) {
        Optional<TrieNode<E>> result = Optional.ofNullable(childrenByPath.get(pathPart));
        if (!result.isPresent()) {
            result = Optional.ofNullable(parameterizedNode);
            if (resolveParameters) {
                result = result.map(p -> new ResolvedParameterTrieNode((ParameterizedTrieNode) p, pathPart));
            }
        }
        return result;
    }

    TrieNode<E> child(String pathPart) {
        if (pathPart.startsWith(":")) {
            return childParam(pathPart.substring(1));
        }
        TrieNode<E> child = childrenByPath.get(pathPart);
        if (child == null) {
            child = new TrieNode<>(this);
            childrenByPath.put(pathPart, child);
        }
        return child;
    }

    private TrieNode<E> childParam(String param) {
        if (parameterizedNode == null) {
            parameterizedNode = new ParameterizedTrieNode(this, param);
        } else {
            throw new IllegalStateException("Path already contains a parameterized element");
        }
        return parameterizedNode;
    }

    Optional<E> getElement() {
        return Optional.ofNullable(element);
    }

    void setElement(E element) {
        this.element = element;
    }

    class ParameterizedTrieNode extends TrieNode<E> {
        final String parameterName;

        ParameterizedTrieNode(TrieNode<E> parent, String parameterName) {
            super(parent);
            this.parameterName = parameterName;
        }

        @Override
        Optional<E> getElement() {
            return TrieNode.this.getElement();
        }

        @Override
        void setElement(E element) {
            TrieNode.this.setElement(element);
        }

        @Override
        Optional<TrieNode<E>> get(String pathPart, boolean resolveParameters) {
            return TrieNode.this.get(pathPart, resolveParameters);
        }

        @Override
        TrieNode<E> child(String pathPart) {
            return TrieNode.this.child(pathPart);
        }
    }

    class ResolvedParameterTrieNode extends TrieNode<E> {
        private final ParameterizedTrieNode node;
        private final String parameterValue;

        ResolvedParameterTrieNode(ParameterizedTrieNode node, String parameterValue) {
            super(node.getParent().orElse(null));
            this.node = node;
            this.parameterValue = parameterValue;
        }

        @Override
        public Optional<E> getElement() {
            return node.getElement();
        }

        @Override
        public void setElement(E element) {
            node.setElement(element);
        }

        @Override
        public Optional<TrieNode<E>> get(String pathPart, boolean resolveParameters) {
            return node.get(pathPart, resolveParameters);
        }

        @Override
        public TrieNode<E> child(String pathPart) {
            return node.child(pathPart);
        }

        Map<String, String> parameterMap() {
            Map<String, String> result = new HashMap<>();
            TrieNode<E> currentNode = this;
            while (currentNode != null) {
                if (currentNode instanceof TrieNode.ResolvedParameterTrieNode) {
                    ((ResolvedParameterTrieNode) currentNode).putParameterValueInto(result);
                }
                currentNode = currentNode.parent;
            }
            return result;
        }

        private void putParameterValueInto(Map<String, String> map) {
            map.put(node.parameterName, parameterValue);
        }
    }
}
