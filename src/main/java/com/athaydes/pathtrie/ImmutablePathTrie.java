package com.athaydes.pathtrie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

final class ImmutablePathTrie<E> implements PathTrie<E> {

    private final Splitter splitter;
    private final ImmutableTrieNode<E> root;

    ImmutablePathTrie(Splitter splitter, ImmutableTrieNode<E> root) {
        this.splitter = splitter;
        this.root = root;
    }

    @Override
    public Optional<E> get(String path) {
        Iterable<String> pathParts = splitter.apply(path);
        Optional<ImmutableTrieNode<E>> node = findNode(pathParts);
        return node.map(n -> n.element);
    }

    @Override
    public Optional<ParameterizedElement<E>> getParameterized(String path) {
        final Iterable<String> pathParts = splitter.apply(path);
        return findParameterizedNode(pathParts);
    }

    @Override
    public Optional<PathTrie<E>> getChild(String path) {
        Iterable<String> pathParts = splitter.apply(path);
        return findNode(pathParts).map(n -> new ImmutablePathTrie<>(splitter, n));
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

    static abstract class ImmutableTrieNode<E> {
        final E element;

        ImmutableTrieNode(E element) {
            this.element = element;
        }

        abstract ImmutableTrieNode<E> get(String pathPart);

        abstract Stream<ImmutableTrieNode<E>> getChildren();
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

        Stream<ImmutableTrieNode<E>> getChildren() {
            return Stream.concat(
                    Stream.of(parameterizedChild).filter(Objects::nonNull),
                    childrenByPath.values().stream());
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
