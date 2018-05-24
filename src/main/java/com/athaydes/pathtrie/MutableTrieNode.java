package com.athaydes.pathtrie;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class MutableTrieNode<E> {

    final Map<String, MutableTrieNode<E>> childrenByPath = new LinkedHashMap<>(2);
    ParameterizedTrieNode<E> parameterizedChild;
    E element;

    public void put(String pathPart, Iterator<String> nextPaths, String parameterPrefix, E element) {
        MutableTrieNode<E> node = child(pathPart, parameterPrefix);
        if (nextPaths.hasNext()) {
            String childPath = nextPaths.next();
            node.put(childPath, nextPaths, parameterPrefix, element);
        } else {
            node.element = element;
        }
    }

    private MutableTrieNode<E> child(String pathPart, String parameterPrefix) {
        MutableTrieNode<E> child = childrenByPath.get(pathPart);
        if (child == null) {
            if (pathPart.startsWith(parameterPrefix)) {
                parameterizedChild = new ParameterizedTrieNode<>(pathPart.substring(parameterPrefix.length()));
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