package com.athaydes.pathtrie;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Examples {

    @Test
    public void basicExample() {
        PathTrie<Integer> trie = PathTrie.<Integer>newBuilder()
                .put("/hello/world", 1)
                .put("/other/path", 2)
                .build();

        Optional<Integer> value = trie.get("/hello/world");

        assertTrue(value.isPresent());
        assertEquals(Integer.valueOf(1), value.get());

        Optional<PathTrie<Integer>> helloTrie = trie.getChild("/hello");

        assertTrue(helloTrie.isPresent());
        assertEquals(Integer.valueOf(1), helloTrie.get().get("world").get());
    }

    @Test
    public void parameterizedTrieExample() {
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
    }

    @Test
    public void customizedPathTrie() {
        PathTrie<Integer> customTrie = PathTrie.<Integer>newBuilder(
                PathSplitter.newBuilder()
                        .splitOn("\\")
                        .withParameterPrefix("?")
                        .build())
                .putFun("?name\\age", (name) -> name.equals("joe") ? 32 : -1)
                .build();

        Optional<ParameterizedElement<Integer>> param = customTrie.getParameterized("joe\\age");

        assertTrue(param.isPresent());
        assertEquals(Integer.valueOf(32), param.get().getElement());
    }

}
