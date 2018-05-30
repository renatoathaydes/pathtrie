# PathTrie

A [Trie](https://en.wikipedia.org/wiki/Trie) implementation designed specifically for using paths as keys.

The main use of this data structure is to locate information stored under hierarchical paths, such as files and URLs,
very fast.

## Simple Usage

The main type of this library is `com.athaydes.pathtrie.PathTrie`.

To create a new `PathTrie`, a builder is used:

```java
var trie = PathTrie.<Integer>newBuilder()
    .put("/hello/world", 1)
    .put("/other/path", 2)
    .build();
```

Retrieving a value from a `PathTrie`:

```java
Optional<Integer> value = trie.get("/hello/world");

assertTrue(value.isPresent());
assertEquals(Integer.valueOf(1), value.get());
```

You can also retrieve a sub-trie:

```java
Optional<PathTrie<Integer>> helloTrie = trie.getChild("/hello");

assertTrue(helloTrie.isPresent());
assertEquals(Integer.valueOf(1), helloTrie.get().get("world").get());
```

## Parameterized Usage

`PathTrie` also supports parameterized mappings:

```java
final Map<String, String> users = new LinkedHashMap<>();
users.put("123", "Joe");
users.put("456", "Mary");

PathTrie<Object> parameterizedTrie = PathTrie.newBuilder()
        .putFun("/users", () -> users)
        .putFun("/users/:id", id -> users.get(id))
        .build();

assertTrue(parameterizedTrie.get("/users").isPresent());
assertEquals(users, parameterizedTrie.get("/users").get());

assertTrue(parameterizedTrie.get("/users/123").isPresent());
assertEquals("Joe", parameterizedTrie.get("/users/123").get());

assertTrue(parameterizedTrie.get("/users/456").isPresent());
assertEquals("Mary", parameterizedTrie.get("/users/456").get());

assertFalse(parameterizedTrie.get("/others").isPresent());
assertFalse(parameterizedTrie.get("/users/789").isPresent());
```
