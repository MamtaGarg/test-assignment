package marketplace.domain;

import static marketplace.TestFramework.*;

/**
 * Tests for SortField enum parsing.
 */
public class SortFieldTest {

    public void testParseSortPrice() {
        assertTrue("sort_price should parse", SortField.fromString("sort_price").isPresent());
        assertEquals(SortField.PRICE, SortField.fromString("sort_price").get());
    }

    public void testParseSortTime() {
        assertTrue("sort_time should parse", SortField.fromString("sort_time").isPresent());
        assertEquals(SortField.TIME, SortField.fromString("sort_time").get());
    }

    public void testParseInvalid() {
        assertFalse("invalid should not parse", SortField.fromString("invalid").isPresent());
        assertFalse("empty should not parse", SortField.fromString("").isPresent());
    }
}
