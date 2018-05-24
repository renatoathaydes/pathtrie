package com.athaydes.pathtrie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

final class ImmutablePathTrie<E> implements PathTrie<E> {

    private final PathSplitter pathSplitter;
    private final ImmutableTrieNode<E> root;

    ImmutablePathTrie(PathSplitter pathSplitter, ImmutableTrieNode<E> root) {
        this.pathSplitter = pathSplitter;
        this.root = root;
    }

    @Override
    public Optional<E> get(String path) {
        Iterable<String> pathParts = pathSplitter.apply(path);
        Optional<ImmutableTrieNode<E>> node = findNode(pathParts);
        return node.map(n -> n.element);
    }

    @Override
    public Optional<ParameterizedElement<E>> getParameterized(String path) {
        final Iterable<String> pathParts = pathSplitter.apply(path);
        return findParameterizedNode(pathParts);
    }

    @Override
    public Optional<PathTrie<E>> getChild(String path) {
        Iterable<String> pathParts = pathSplitter.apply(path);
        return findNode(pathParts).map(n -> new ImmutablePathTrie<>(pathSplitter, n));
    }

    private Optional<ImmutableTrieNode<E>> findNode(Iterable<String> pathParts) {
        ImmutableTrieNode<E> current = root;
        for (String pathPart : pathParts) {
            current = current.get(pathPart);
            if (current == null) {
                break;
            }
        }
        return Optional.ofNullable(current == root ? null : current);
    }

    private Optional<ParameterizedElement<E>> findParameterizedNode(Iterable<String> pathParts) {
        Map<String, String> parameterMap = new HashMap<>();
        ImmutableTrieNode<E> current = root;
        for (String pathPart : pathParts) {
            current = current.get(pathPart);
            if (current == null) {
                break;
            } else if (current instanceof ParameterizedImmutableTrieNode) {
                parameterMap.put(((ParameterizedImmutableTrieNode<E>) current).parameterName, pathPart);
            }
        }
        return Optional.ofNullable(current == null || current.element == null
                ? null
                : new DefaultParameterizedElement<>(current.element, parameterMap));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PathTrie {\n");
        buildString(builder, root, "");
        builder.append("}");
        return builder.toString();
    }

    private static void buildString(StringBuilder builder, ImmutableTrieNode<?> node, String indent) {
        node.forEach((pathPart, child) -> {
            builder.append(indent);
            if (child instanceof ParameterizedImmutableTrieNode) {
                builder.append('<').append(pathPart).append('>');
            } else {
                builder.append(pathPart);
            }
            if (child.element != null) {
                builder.append(": ").append(child.element);
            }
            builder.append('\n');
            buildString(builder, child, indent + "  ");
        });
    }

    static abstract class ImmutableTrieNode<E> {
        final E element;

        ImmutableTrieNode(E element) {
            this.element = element;
        }

        abstract ImmutableTrieNode<E> get(String pathPart);

        abstract void forEach(BiConsumer<String, ImmutableTrieNode> action);
    }

    static class ImmutableTrieNodeImpl<E> extends ImmutableTrieNode<E> {

        private final Map<String, ImmutableTrieNode<E>> childrenByPath;
        private final ParameterizedImmutableTrieNode<E> parameterizedChild;

        ImmutableTrieNodeImpl(E element,
                              Map<String, ImmutableTrieNode<E>> childrenByPath,
                              ParameterizedImmutableTrieNode<E> parameterizedChild) {
            super(element);
            this.childrenByPath = Collections.unmodifiableMap(childrenByPath);
            this.parameterizedChild = parameterizedChild;
        }

        @Override
        void forEach(BiConsumer<String, ImmutableTrieNode> action) {
            childrenByPath.forEach(action);
            if (parameterizedChild != null) {
                action.accept(parameterizedChild.parameterName, parameterizedChild);
            }
        }

        @Override
        public ImmutableTrieNode<E> get(String pathPart) {
            ImmutableTrieNode<E> result = childrenByPath.get(pathPart);
            if (result == null) {
                result = parameterizedChild;
            }
            return result;
        }
    }

    static class ParameterizedImmutableTrieNode<E> extends ImmutableTrieNodeImpl<E> {
        final String parameterName;

        ParameterizedImmutableTrieNode(E element,
                                       Map<String, ImmutableTrieNode<E>> childrenByPath,
                                       ParameterizedImmutableTrieNode<E> parameterizedChild,
                                       String parameterName) {
            super(element, childrenByPath, parameterizedChild);
            this.parameterName = parameterName;
        }
    }

}
