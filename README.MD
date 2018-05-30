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

assertEquals(users, parameterizedTrie.get("/users").orElse(emptyMap()));
assertEquals("Joe", parameterizedTrie.get("/users/123").orElse("NOT FOUND"));
assertEquals("Mary", parameterizedTrie.get("/users/456").orElse("NOT FOUND"));

assertFalse(parameterizedTrie.get("/others").isPresent());
assertFalse(parameterizedTrie.get("/users/789").isPresent());
```

Up to 4 parameters can be used (if more is needed, you can implement `com.athaydes.pathtrie.functions.Fun` providing
an adapter that can take more parameters):

```java
PathTrie<Object> parameterizedTrie = PathTrie.newBuilder()
    .putFun("/:a/:b/:c/:d", (a, b, c, d) -> "a=" + a + ",b=" + b + ",c=" + c + ",d=" + d)
    .build();

assertEquals("a=A,b=B,c=C,d=D", parameterizedTrie.get("/A/B/C/D").orElse("NOT FOUND"));
```

Notice that the lambda arguments are positional (i.e. their names don't matter, only their positions relative to the
path).
