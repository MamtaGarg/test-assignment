package marketplace.domain;

import static marketplace.TestFramework.*;

/**
 * Tests for SortOrder enum parsing.
 */
public class SortOrderTest {

    public void testParseAsc() {
        assertTrue("asc should parse", SortOrder.fromString("asc").isPresent());
        assertEquals(SortOrder.ASCENDING, SortOrder.fromString("asc").get());
    }

    public void testParseDsc() {
        assertTrue("dsc should parse", SortOrder.fromString("dsc").isPresent());
        assertEquals(SortOrder.DESCENDING, SortOrder.fromString("dsc").get());
    }

    public void testParseInvalid() {
        assertFalse("invalid should not parse", SortOrder.fromString("invalid").isPresent());
        assertFalse("empty should not parse", SortOrder.fromString("").isPresent());
        assertFalse("desc should not parse (not 'dsc')", SortOrder.fromString("desc").isPresent());
    }
}
