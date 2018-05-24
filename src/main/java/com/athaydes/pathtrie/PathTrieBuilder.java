package com.athaydes.pathtrie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Builder of {@link PathTrie} instances.
 * <p>
 * To create instances of this builder, use {@link PathTrie#newBuilder()} or {@link PathTrie#newBuilder(Splitter)}.
 *
 * @param <E> type of elements contained in instances of {@link PathTrie}
 */
public class PathTrieBuilder<E> {

    private final MutableTrieNode<E> root = new MutableTrieNode<>();
    private final Splitter splitter;

    PathTrieBuilder() {
        this((path) -> Arrays.asList(path.split("/")));
    }

    PathTrieBuilder(Splitter splitter) {
        this.splitter = splitter;
    }

    /**
     * Put an element under the given path.
     *
     * @param path    to place the element on
     * @param element the element to pu
     * @return this builder
     */
    public PathTrieBuilder<E> put(String path, E element) {
        Iterator<String> pathIterator = splitter.apply(path).iterator();
        if (!pathIterator.hasNext()) {
            throw new IllegalArgumentException("Path cannot be split into one or more parts: '" + path + "'");
        }
        root.put(pathIterator.next(), pathIterator, element);
        return this;
    }

    /**
     * @return an instance of {@link PathTrie} containing the elements added to this builder.
     */
    public PathTrie<E> build() {
        return new ImmutablePathTrie<>(splitter, asImmutable(root));
    }

    private static <E> ImmutablePathTrie.ImmutableTrieNode<E> asImmutable(MutableTrieNode<E> node) {
        if (node instanceof ParameterizedTrieNode) {
            return asImmutableParameterized((ParameterizedTrieNode<E>) node);
        }
        return new ImmutablePathTrie.ImmutableTrieNodeImpl<>(
                node.element,
                asImmutable(node.childrenByPath),
                asImmutableParameterized(node.parameterizedChild));
    }

    private static <E> Map<String, ImmutablePathTrie.ImmutableTrieNode<E>> asImmutable(
            Map<String, MutableTrieNode<E>> childrenByPath) {
        Map<String, ImmutablePathTrie.ImmutableTrieNode<E>> result = new HashMap<>(childrenByPath.size());
        childrenByPath.forEach((path, child) -> result.put(path, asImmutable(child)));
        return result;
    }

    private static <E> ImmutablePathTrie.ParameterizedImmutableTrieNode<E> asImmutableParameterized(
            ParameterizedTrieNode<E> node) {
        if (node == null) {
            return null;
        }
        return new ImmutablePathTrie.ParameterizedImmutableTrieNode<>(
                node.element,
                asImmutable(node.childrenByPath),
                asImmutableParameterized(node.parameterizedChild),
                node.parameterName);
    }

}
