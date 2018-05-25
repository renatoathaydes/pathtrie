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

abstract class Controller {
    abstract String handle(ParameterizedElement<Controller> element);
}

class AllUsersController extends Controller {
    @Override
    String handle(ParameterizedElement<Controller> element) {
        return "users: " + users.values().toString();
    }
}

class UserController extends Controller {
    @Override
    String handle(ParameterizedElement<Controller> element) {
        String id = element.param("id");
        return users.containsKey(id) ? "name: " + users.get(id) : "found: false";
    }
}

PathTrie<Controller> parameterizedTrie = PathTrie.<Controller>newBuilder()
        .put("/users", new AllUsersController())
        .put("/users/:id", new UserController())
        .build();

Function<String, String> router = (path) -> parameterizedTrie.getParameterized(path)
        .map(p -> p.getElement().handle(p))
        .orElse("Path not mapped");

String allUsers = router.apply("/users");
assertEquals("users: [Joe, Mary]", allUsers);

String user123 = router.apply("/users/123");
assertEquals("name: Joe", user123);

String user456 = router.apply("/users/456");
assertEquals("name: Mary", user456);

String user789 = router.apply("/users/789");
assertEquals("found: false", user789);
```
