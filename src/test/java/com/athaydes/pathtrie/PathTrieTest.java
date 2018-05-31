package com.athaydes.pathtrie;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathTrieTest {

    @Test
    public void emptyTreeContainsNothing() {
        PathTrie<Integer> trie = PathTrie.empty();
        assertFalse("Does not contain any element", trie.get("").isPresent());
        assertFalse("Does not contain any element", trie.get("other").isPresent());
        assertFalse("Does not contain any element", trie.get(":other").isPresent());
        assertFalse("Does not contain any element", trie.get(":").isPresent());
        assertFalse("Does not contain any element", trie.get("some/path").isPresent());
        assertFalse("Does not contain any element", trie.get("other/something/:other/path").isPresent());
    }

    @Test
    public void canRetrieveSingleSimplePath() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello", 10)
                .build();
        assertElementHasValue(trie, "hello", 10);
        assertFalse("Does not contain element not added", trie.get("").isPresent());
        assertFalse("Does not contain element not added", trie.get("other").isPresent());
        assertFalse("Does not contain element not added", trie.get(":other").isPresent());
    }

    @Test
    public void canRetrieveSingleEmptyPath() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("", 10)
                .build();
        assertElementHasValue(trie, "", 10);
        assertFalse("Does not contain element not added", trie.get("other").isPresent());
        assertFalse("Does not contain element not added", trie.get(":other").isPresent());
    }

    @Test
    public void canRetrieveEmptyParameterizedPath() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put(":", 10)
                .put("hello/:", 20)
                .build();
        assertParameterHasValue(trie, "", "", "", 10);
        assertParameterHasValue(trie, "mary", "", "mary", 10);
        assertParameterHasValue(trie, "hello/bob", "", "bob", 20);
        assertFalse("Does not contain element not added", trie.get("mary/jane").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello/john/smith").isPresent());
    }

    @Test
    public void trailingSeparatorIsIgnored() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello", 10)
                .put("hello/world", 20)
                .build();
        assertElementHasValue(trie, "hello", 10);
        assertElementHasValue(trie, "hello/", 10);
        assertElementHasValue(trie, "hello/world", 20);
        assertElementHasValue(trie, "hello/world/", 20);
        assertFalse("Does not contain element not added", trie.get("").isPresent());
        assertFalse("Does not contain element not added", trie.get("other").isPresent());
        assertFalse("Does not contain element not added", trie.get(":other").isPresent());
    }

    @Test
    public void leadingSeparatorTest() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("/hello", 10)
                .put("/hello/world", 20)
                .build();
        assertElementHasValue(trie, "/hello", 10);
        assertElementHasValue(trie, "/hello/", 10);
        assertElementHasValue(trie, "/hello/world", 20);
        assertElementHasValue(trie, "/hello/world/", 20);
        assertFalse("Does not contain element not added", trie.get("").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello").isPresent());
        assertFalse("Does not contain element not added", trie.get("hello/world").isPresent());
        assertFalse("Does not contain element not added", trie.get("other").isPresent());
        assertFalse("Does not contain element not added", trie.get(":other").isPresent());
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

    @Test
    public void cannotPutMoreThanOneParameterOnSameLevel() {
        Throwable error = shouldThrow(() -> PathTrie.<Integer>newBuilder()
                .put("hello/:person", 10)
                .put("hello/:animal", 11)
                .put("hello/:thing", 12)
                .put("hello/:name", 20)
                .build());

        assertTrue("Error is of expected type :" + error, error instanceof IllegalArgumentException);
        assertEquals("Parameters with different names clash at the same level: 'animal' and 'person'",
                error.getMessage());
    }

    @Test
    public void cannotHaveMoreThanOneParameterWithSameNameOnHierarchy() {
        Exception error = shouldThrow(() -> PathTrie.<Integer>newBuilder()
                .put("hello/:name/other/:name/:something", 10)
                .build());
        assertTrue("Error is of expected type :" + error, error instanceof IllegalArgumentException);
    }

    @Test
    public void canHaveMoreThanOneParameterWithSameNameOnDifferentHierarchy() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello/one/:name/:something", 10)
                .put("hello/other/:name/:something", 20)
                .put("hello/:name", 30)
                .put(":something/:name", 40)
                .build();

        assertParameterHasValue(trie, "hello/one/bob/a", "name", "bob", 10);
        assertParameterHasValue(trie, "hello/one/bob/a", "something", "a", 10);
        assertParameterHasValue(trie, "hello/other/bob/a", "name", "bob", 20);
        assertParameterHasValue(trie, "hello/other/bob/a", "something", "a", 20);
        assertParameterHasValue(trie, "hello/bob", "name", "bob", 30);
        assertParameterHasValue(trie, "a/bob", "something", "a", 40);
        assertParameterHasValue(trie, "a/bob", "name", "bob", 40);
    }

    @Test
    public void toStringTest() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("hello", 10)
                .put("hello/:there", 20)
                .put("boo", 30)
                .put("boo/foo/bar", 61)
                .build();

        assertEquals("PathTrie {\n" +
                "hello: 10\n" +
                "  <there>: 20\n" +
                "boo: 30\n" +
                "  foo\n" +
                "    bar: 61\n" +
                "}", trie.toString());
    }

    @Test
    public void toStringTest2() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put(":name", 10)
                .put("name", 20)
                .put("abc", 15)
                .put("something/other/path/foo/bar", 30)
                .put("boo/foo/bar", 61)
                .build();

        assertEquals("PathTrie {\n" +
                "name: 20\n" +
                "abc: 15\n" +
                "something\n" +
                "  other\n" +
                "    path\n" +
                "      foo\n" +
                "        bar: 30\n" +
                "boo\n" +
                "  foo\n" +
                "    bar: 61\n" +
                "<name>: 10\n" +
                "}", trie.toString());
    }

    @Test
    public void canPutFunInTrie() {
        PathTrie<String> trie = PathTrie.<String>newBuilder()
                .putFun("hello/:person", person -> "Person is " + person)
                .putFun("ola/:name", (name) -> "Ola " + name)
                .putFun("ola/:name/:age", (name, age) -> "" + name + " is " + age + " years old")
                .build();

        assertParameterHasValue(trie, "hello/bob", "person", "bob", "Person is bob");
        assertParameterHasValue(trie, "hello/mary", "person", "mary", "Person is mary");
        assertParameterHasValue(trie, "hello/mary", "person", "mary", "Person is mary");
        assertElementHasValue(trie, "hello/mary", "Person is mary");
        assertElementHasValue(trie, "ola/mary", "Ola mary");
        assertElementHasValue(trie, "ola/mary/29", "mary is 29 years old");
        assertElementHasValue(trie, "ola/bob", "Ola bob");
        assertElementHasValue(trie, "ola/bob/57", "bob is 57 years old");
        assertParameterHasValue(trie, "ola/mary", "name", "mary", "Ola mary");
        assertParameterHasValue(trie, "ola/mary/29", "name", "mary", "mary is 29 years old");
        assertParameterHasValue(trie, "ola/mary/29", "age", "29", "mary is 29 years old");
        assertElementHasValue(trie, "hello/bob", "Person is bob");
        assertFalse("Does not contain element not added", trie.get("other").isPresent());
        assertFalse("Does not contain element not added", trie.get("other/path").isPresent());
    }

    @Test
    public void validatesFunParametersCount() {
        Throwable error = shouldThrow(() -> PathTrie.<String>newBuilder()
                .putFun("hello/:person", (a, b) -> "")
                .build());

        assertTrue("Error is of expected type :" + error, error instanceof IllegalArgumentException);
        assertEquals("Path 'hello/:person' contains 1 parameter but Fun2 expects 2", error.getMessage());
    }

    @Test
    public void validatesFunParametersCount2() {
        Throwable error = shouldThrow(() -> PathTrie.<String>newBuilder()
                .putFun(":a/:b/:c/:d", (a) -> "")
                .build());

        assertTrue("Error is of expected type :" + error, error instanceof IllegalArgumentException);
        assertEquals("Path ':a/:b/:c/:d' contains 4 parameters but Fun1 expects 1", error.getMessage());
    }

    @Test
    public void validatesFunParametersCount3() {
        Throwable error = shouldThrow(() -> PathTrie.<String>newBuilder()
                .putFun("hello", (a) -> "")
                .build());

        assertTrue("Error is of expected type :" + error, error instanceof IllegalArgumentException);
        assertEquals("Path 'hello' contains 0 parameters but Fun1 expects 1", error.getMessage());
    }

    @Test
    public void validatesFunParametersCount4() {
        Throwable error = shouldThrow(() -> PathTrie.<String>newBuilder()
                .putFun("hello/:name", () -> "")
                .build());

        assertTrue("Error is of expected type :" + error, error instanceof IllegalArgumentException);
        assertEquals("Path 'hello/:name' contains 1 parameter but Fun0 expects 0", error.getMessage());
    }

    private <V> void assertParameterHasValue(PathTrie<V> trie, String key, String parameterName,
                                             String parameterValue, V value) {
        Optional<ParameterizedElement<V>> element = trie.getParameterized(key);
        assertTrue("Element is present: " + key, element.isPresent());
        assertEquals("Element has correct value", element.get().getElement(), value);
        assertEquals("Element has correct value", element.get().param(parameterName), parameterValue);
    }

    private static <V> void assertElementHasValue(PathTrie<V> trie, String key, V value) {
        assertTrue("Element is present: " + key, trie.get(key).isPresent());
        assertEquals("Element has correct value", trie.get(key).get(), value);
    }

    private static Exception shouldThrow(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            return e;
        }
        throw new AssertionError("Expected Exception to be thrown but nothing happened");
    }

}
