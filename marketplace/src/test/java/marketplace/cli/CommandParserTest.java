package marketplace.cli;

import java.util.Arrays;
import java.util.List;

import static marketplace.TestFramework.*;

/**
 * Tests for CommandParser.
 */
public class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    public void testSimpleCommand() {
        List<String> result = parser.parse("REGISTER user1");
        assertEquals(Arrays.asList("REGISTER", "user1"), result);
    }

    public void testCommandWithQuotedArgs() {
        List<String> result = parser.parse(
                "CREATE_LISTING user1 'Phone model 8' 'Black color, brand new' 1000 'Electronics'");
        assertEquals(6, result.size());
        assertEquals("CREATE_LISTING", result.get(0));
        assertEquals("user1", result.get(1));
        assertEquals("Phone model 8", result.get(2));
        assertEquals("Black color, brand new", result.get(3));
        assertEquals("1000", result.get(4));
        assertEquals("Electronics", result.get(5));
    }

    public void testCommandNoQuotes() {
        List<String> result = parser.parse("GET_CATEGORY user1 Sports sort_time asc");
        assertEquals(Arrays.asList("GET_CATEGORY", "user1", "Sports", "sort_time", "asc"), result);
    }

    public void testEmptyString() {
        List<String> result = parser.parse("");
        assertTrue("empty input should return empty list", result.isEmpty());
    }

    public void testMultipleSpaces() {
        List<String> result = parser.parse("REGISTER   user1");
        assertEquals(Arrays.asList("REGISTER", "user1"), result);
    }

    public void testSingleArg() {
        List<String> result = parser.parse("GET_TOP_CATEGORY user1");
        assertEquals(Arrays.asList("GET_TOP_CATEGORY", "user1"), result);
    }

    public void testQuotedArgWithSpecialChars() {
        List<String> result = parser.parse(
                "CREATE_LISTING user1 'My Item' 'A nice description here' 500 'Home & Garden'");
        assertEquals(6, result.size());
        assertEquals("Home & Garden", result.get(5));
    }
}
