package com.athaydes.pathtrie;

import com.athaydes.pathtrie.functions.Fun0;
import com.athaydes.pathtrie.functions.Fun1;
import com.athaydes.pathtrie.functions.Fun2;
import com.athaydes.pathtrie.functions.Fun3;
import com.athaydes.pathtrie.functions.Fun4;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builder of {@link PathTrie} instances.
 * <p>
 * To create instances of this builder, use {@link PathTrie#newBuilder()} or {@link PathTrie#newBuilder(PathSplitter)}.
 *
 * @param <E> type of elements contained in instances of {@link PathTrie}
 */
public class PathTrieBuilder<E> {

    private final MutableTrieNode<E> root = new MutableTrieNode<>();
    private final PathSplitter pathSplitter;

    PathTrieBuilder(PathSplitter pathSplitter) {
        this.pathSplitter = pathSplitter;
    }

    /**
     * Put an element under the given path.
     *
     * @param path    to place the element on
     * @param element the element to pu
     * @return this builder
     */
    public PathTrieBuilder<E> put(String path, E element) {
        return putBox(path, new Box.SimpleBox<>(element));
    }

    public PathTrieBuilder<E> putFun(String path, Fun0<E> fun) {
        return putBox(path, new Box.FunBox<>(fun));
    }

    public PathTrieBuilder<E> putFun(String path, Fun1<E> fun) {
        return putBox(path, new Box.FunBox<>(fun));
    }

    public PathTrieBuilder<E> putFun(String path, Fun2<E> fun) {
        return putBox(path, new Box.FunBox<>(fun));
    }

    public PathTrieBuilder<E> putFun(String path, Fun3<E> fun) {
        return putBox(path, new Box.FunBox<>(fun));
    }

    public PathTrieBuilder<E> putFun(String path, Fun4<E> fun) {
        return putBox(path, new Box.FunBox<>(fun));
    }

    private PathTrieBuilder<E> putBox(String path, Box<E> box) {
        Iterator<String> pathIterator = pathSplitter.apply(path).iterator();
        if (!pathIterator.hasNext()) {
            throw new IllegalArgumentException("Path cannot be split into one or more parts: '" + path + "'");
        }
        root.put(pathIterator.next(), pathIterator, pathSplitter.parameterizedParameterPrefix(), box);
        return this;
    }

    /**
     * @return an instance of {@link PathTrie} containing the elements added to this builder.
     */
    public PathTrie<E> build() {
        verifyParameterNamesAndCount(root, new LinkedHashSet<>(4), new ArrayList<>(6));
        return new ImmutablePathTrie<>(pathSplitter, asImmutable(root));
    }

    private static void verifyParameterNamesAndCount(MutableTrieNode<?> node,
                                                     Set<String> visitedParameters,
                                                     List<String> pathParts) {
        if (node instanceof ParameterizedTrieNode) {
            ParameterizedTrieNode<?> parameterizedTrieNode = (ParameterizedTrieNode<?>) node;
            String name = parameterizedTrieNode.parameterName;
            boolean seenBefore = !visitedParameters.add(name);
            if (seenBefore) {
                throw new IllegalArgumentException("Parameter name appears more than once on same hierarchy: " + name);
            }
            verifyParameterCount(visitedParameters, pathParts, parameterizedTrieNode);
        } else if (node.element != null) {
            verifyParameterCount(visitedParameters, pathParts, node);
        }
        Set<String> visitedInBranch = new HashSet<>(visitedParameters);
        node.childrenByPath.forEach((path, mutableTrieNode) -> {
            pathParts.add(path);
            verifyParameterNamesAndCount(mutableTrieNode, visitedInBranch, pathParts);
        });
        if (node.parameterizedChild != null) {
            pathParts.add(":" + node.parameterizedChild.parameterName);
            verifyParameterNamesAndCount(node.parameterizedChild, visitedInBranch, pathParts);
        }
    }

    private static void verifyParameterCount(Set<String> visitedParameters,
                                             List<String> pathParts,
                                             MutableTrieNode<?> node) {
        int pathParameterCount = visitedParameters.size();
        int nodeParameterCount = node.element == null
                ? pathParameterCount
                : node.element.use(
                b -> pathParameterCount, f -> f.fun.parameterCount());
        if (nodeParameterCount != pathParameterCount) {
            String path = String.join("/", pathParts);
            StringBuilder builder = new StringBuilder();
            builder.append("Path '").append(path).append("' contains ")
                    .append(pathParameterCount).append(" parameter");
            if (pathParameterCount != 1) {
                builder.append('s');
            }
            builder.append(" but Fun").append(nodeParameterCount).append(" expects ").append(nodeParameterCount);
            throw new IllegalArgumentException(builder.toString());
        }
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
        Map<String, ImmutablePathTrie.ImmutableTrieNode<E>> result = new LinkedHashMap<>(childrenByPath.size());
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
