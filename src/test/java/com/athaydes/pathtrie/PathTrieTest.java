package com.athaydes.pathtrie;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathTrieTest {

    @Test
    public void canRetrieveSingleSimplePath() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello", 10)
                .build();
        assertElementHasValue(trie, "hello", 10);
        assertFalse("Does not contain element not added", trie.get("other").isPresent());
    }

    @Test
    public void canRetrieveSingleComplexPath() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello/joe/welcome/to/the/jungle", 10)
                .build();
        assertElementHasValue(trie, "hello/joe/welcome/to/the/jungle", 10);
        assertFalse("Does not contain element not added", trie.get("hello/joe/welcome/to/the").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello/joe/").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello").isPresent());
    }

    @Test
    public void canRetrieveMultiplePaths() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello", 10)
                .put("ho", 15)
                .put("bye/there", 20)
                .put("boo/foo/moo/few", 30)
                .put("boo/foo/boo", 40)
                .build();
        assertElementHasValue(trie, "hello", 10);
        assertElementHasValue(trie, "ho", 15);
        assertElementHasValue(trie, "bye/there", 20);
        assertElementHasValue(trie, "boo/foo/moo/few", 30);
        assertElementHasValue(trie, "boo/foo/boo", 40);
        assertFalse("Does not contain element not added", trie.get("other").isPresent());
        assertFalse("Does not contain element not added", trie.get("bye").isPresent());
        assertFalse("Does not contain element not added", trie.get("bye/there/you").isPresent());
        assertFalse("Does not contain element not added", trie.get("boo").isPresent());
        assertFalse("Does not contain element not added", trie.get("boo/foo").isPresent());
        assertFalse("Does not contain element not added", trie.get("boo/foo/moo").isPresent());
    }

    @Test
    public void canRetrieveChildTrie() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello", 10)
                .put("ho", 15)
                .put("bye/there", 20)
                .put("boo/foo/moo/few", 30)
                .put("boo/foo/boo", 40)
                .build();

        // check empty child
        Optional<PathTrie<Integer>> emptyChild = trie.getChild("hello");
        assertTrue(emptyChild.isPresent());
        assertFalse("Does not contain anything", emptyChild.get().get("other").isPresent());
        assertFalse("Does not contain anything", emptyChild.get().get("ho").isPresent());
        assertFalse("Does not contain anything", emptyChild.get().get("bye/there").isPresent());
        assertFalse("Does not contain anything", emptyChild.get().get("there").isPresent());

        // check child containing a single element
        Optional<PathTrie<Integer>> byeTrie = trie.getChild("bye");
        assertTrue(byeTrie.isPresent());
        assertElementHasValue(byeTrie.get(), "there", 20);
        assertFalse("Does not contain element outside sub-trie", byeTrie.get().get("hello").isPresent());
        assertFalse("Does not contain element outside sub-trie", byeTrie.get().get("foo").isPresent());

        // check child containing 2 elements
        Optional<PathTrie<Integer>> booTrie = trie.getChild("boo");
        assertTrue(booTrie.isPresent());
        assertElementHasValue(booTrie.get(), "foo/moo/few", 30);
        assertElementHasValue(booTrie.get(), "foo/boo", 40);
        assertFalse("Does not contain element outside sub-trie", booTrie.get().get("foo").isPresent());
        assertFalse("Does not contain element outside sub-trie", booTrie.get().get("ho").isPresent());
        assertFalse("Does not contain element outside sub-trie", booTrie.get().get("foo/boo/boo").isPresent());
    }

    @Test
    public void canResolveParameterizedPath() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put(":person", 10)
                .put("hello/name", 20)
                .put("hello/:name", 30)
                .build();

        assertParameterHasValue(trie, "mary", "person", "mary", 10);
        assertParameterHasValue(trie, "ana", "person", "ana", 10);
        assertParameterHasValue(trie, "hello/joe", "name", "joe", 30);
        assertParameterHasValue(trie, "hello/mary", "name", "mary", 30);
        assertParameterHasValue(trie, "hello/ana", "name", "ana", 30);
        assertElementHasValue(trie, "hello/name", 20);
        assertElementHasValue(trie, "hello/mary", 30);
        assertElementHasValue(trie, "hello/ana", 30);
        assertFalse("Does not contain element not added", trie.get("other/hello").isPresent());
        assertFalse("Does not contain element not added", trie.get("path/other/hello").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello/name/other").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello/mary/other/path").isPresent());
    }

    @Test
    public void canResolveParameterizedPathWithCustomParameterPrefix() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder(
                PathSplitter.newBuilder()
                        .splitOn("\\")
                        .withParameterPrefix("?")
                        .build())
                .put("?person", 10)
                .put("hello\\name", 20)
                .put("hello\\?name", 30)
                .build();

        assertParameterHasValue(trie, "mary", "person", "mary", 10);
        assertParameterHasValue(trie, "ana", "person", "ana", 10);
        assertParameterHasValue(trie, "hello/joe", "person", "hello/joe", 10);
        assertParameterHasValue(trie, "hello\\joe", "name", "joe", 30);
        assertParameterHasValue(trie, "hello\\mary", "name", "mary", 30);
        assertParameterHasValue(trie, "hello\\ana", "name", "ana", 30);
        assertElementHasValue(trie, "hello/name", 10);
        assertElementHasValue(trie, "some/name", 10);
        assertElementHasValue(trie, "hello\\name", 20);
        assertElementHasValue(trie, "hello\\mary", 30);
        assertElementHasValue(trie, "hello\\ana", 30);
        assertFalse("Does not contain element not added", trie.get("other\\hello").isPresent());
        assertFalse("Does not contain element not added", trie.get("path\\other\\hello").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello\\name\\other").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello\\mary\\other\\path").isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void cannotResolveParameterThatDoesNotExist() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put(":person", 10)
                .build();

        trie.getParameterized("joe").ifPresent(p -> p.param("wrong"));
    }

    @Test(expected = NoSuchElementException.class)
    public void cannotResolveParameterThatDoesNotExistInSubPath() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello/:person", 10)
                .build();

        trie.getParameterized("hello/joe").ifPresent(p -> p.param("wrong"));
    }

    private void assertParameterHasValue(PathTrie<Integer> trie, String key, String parameterName,
                                         String parameterValue, Integer value) {
        Optional<ParameterizedElement<Integer>> element = trie.getParameterized(key);
        assertTrue("Element is present: " + key, element.isPresent());
        assertEquals("Element has correct value", element.get().getElement(), value);
        assertEquals("Element has correct value", element.get().param(parameterName), parameterValue);
    }

    private static void assertElementHasValue(PathTrie<Integer> trie, String key, Integer value) {
        assertTrue("Element is present: " + key, trie.get(key).isPresent());
        assertEquals("Element has correct value", trie.get(key).get(), value);
    }

}
