package com.athaydes.pathtrie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

class MutableTrieNode<E> {

    final Map<String, MutableTrieNode<E>> childrenByPath = new HashMap<>(2);
    ParameterizedTrieNode<E> parameterizedChild;
    E element;

    public void put(String pathPart, Iterator<String> nextPaths, E element) {
        MutableTrieNode<E> node = child(pathPart);
        if (nextPaths.hasNext()) {
            String childPath = nextPaths.next();
            node.put(childPath, nextPaths, element);
        } else {
            node.element = element;
        }
    }

    Stream<MutableTrieNode<E>> getChildren() {
        return Stream.concat(
                Stream.of(parameterizedChild).filter(Objects::nonNull),
                childrenByPath.values().stream());
    }

    private MutableTrieNode<E> child(String pathPart) {
        MutableTrieNode<E> child = childrenByPath.get(pathPart);
        if (child == null) {
            if (pathPart.startsWith(":")) {
                parameterizedChild = new ParameterizedTrieNode<>(pathPart.substring(1));
                child = parameterizedChild;
            } else {
                MutableTrieNode<E> newChild = new MutableTrieNode<>();
                childrenByPath.put(pathPart, newChild);
                child = newChild;
            }
        }
        return child;
    }

}

class ParameterizedTrieNode<E> extends MutableTrieNode<E> {
    final String parameterName;

    ParameterizedTrieNode(String parameterName) {
        this.parameterName = parameterName;
    }

}