package marketplace.cli;

import marketplace.repository.InMemoryListingRepository;
import marketplace.repository.InMemoryUserRepository;
import marketplace.service.MarketplaceService;

import static marketplace.TestFramework.*;

/**
 * Tests for CommandHandler - integration between CLI parsing and service layer.
 */
public class CommandHandlerTest {

    private CommandHandler createHandler() {
        MarketplaceService svc = new MarketplaceService(
                new InMemoryUserRepository(),
                new InMemoryListingRepository()
        );
        return new CommandHandler(svc);
    }

    // ---- REGISTER ----

    public void testRegister() {
        CommandHandler h = createHandler();
        assertEquals("Success", h.handle("REGISTER user1"));
    }

    public void testRegisterDuplicate() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        assertEquals("Error - user already existing", h.handle("REGISTER user1"));
    }

    // ---- CREATE_LISTING ----

    public void testCreateListing() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");

        assertEquals("100001",
                h.handle("CREATE_LISTING user1 'Phone model 8' 'Black color, brand new' 1000 'Electronics'"));
    }

    public void testCreateListingUnknownUser() {
        CommandHandler h = createHandler();
        assertEquals("Error - unknown user",
                h.handle("CREATE_LISTING unknown 'Phone' 'desc' 100 'Electronics'"));
    }

    // ---- GET_LISTING ----

    public void testGetListing() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        h.handle("CREATE_LISTING user1 'Phone model 8' 'Black color, brand new' 1000 'Electronics'");

        String result = h.handle("GET_LISTING user1 100001");
        assertTrue("should contain title", result.contains("Phone model 8"));
        assertTrue("should contain description", result.contains("Black color, brand new"));
        assertTrue("should contain price", result.contains("1000"));
        assertTrue("should contain category", result.contains("Electronics"));
        assertTrue("should contain username", result.contains("user1"));
    }

    public void testGetListingNotFound() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        assertEquals("Error - not found", h.handle("GET_LISTING user1 999999"));
    }

    public void testGetListingUnknownUser() {
        CommandHandler h = createHandler();
        assertEquals("Error - unknown user", h.handle("GET_LISTING unknown 100001"));
    }

    // ---- DELETE_LISTING ----

    public void testDeleteListing() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        h.handle("CREATE_LISTING user1 'Phone' 'Brand new' 1000 'Electronics'");

        assertEquals("Success", h.handle("DELETE_LISTING user1 100001"));
    }

    public void testDeleteListingOwnerMismatch() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        h.handle("REGISTER user2");
        h.handle("CREATE_LISTING user1 'Phone' 'Brand new' 1000 'Electronics'");

        assertEquals("Error - listing owner mismatch", h.handle("DELETE_LISTING user2 100001"));
    }

    public void testDeleteListingNotFound() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        assertEquals("Error - listing does not exist", h.handle("DELETE_LISTING user1 999999"));
    }

    // ---- GET_CATEGORY ----

    public void testGetCategorySortPriceDsc() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        h.handle("CREATE_LISTING user1 'Shoes' 'Running shoes' 100 'Sports'");
        h.handle("CREATE_LISTING user1 'T-shirt' 'White color' 20 'Sports'");

        String result = h.handle("GET_CATEGORY user1 'Sports' sort_price dsc");
        String[] lines = result.split("\n");
        assertEquals(2, lines.length);
        assertTrue("first line should be Shoes (higher price)", lines[0].startsWith("Shoes|"));
        assertTrue("second line should be T-shirt (lower price)", lines[1].startsWith("T-shirt|"));
    }

    public void testGetCategorySortPriceAsc() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        h.handle("CREATE_LISTING user1 'Shoes' 'Running shoes' 100 'Sports'");
        h.handle("CREATE_LISTING user1 'T-shirt' 'White color' 20 'Sports'");

        String result = h.handle("GET_CATEGORY user1 'Sports' sort_price asc");
        String[] lines = result.split("\n");
        assertTrue("first line should be T-shirt (lower price)", lines[0].startsWith("T-shirt|"));
        assertTrue("second line should be Shoes (higher price)", lines[1].startsWith("Shoes|"));
    }

    public void testGetCategoryNotFound() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        assertEquals("Error - category not found",
                h.handle("GET_CATEGORY user1 'Fashion' sort_time asc"));
    }

    public void testGetCategoryUnknownUser() {
        CommandHandler h = createHandler();
        assertEquals("Error - unknown user",
                h.handle("GET_CATEGORY unknown 'Sports' sort_time asc"));
    }

    // ---- GET_TOP_CATEGORY ----

    public void testGetTopCategory() {
        CommandHandler h = createHandler();
        h.handle("REGISTER user1");
        h.handle("CREATE_LISTING user1 'Phone' 'Brand new' 1000 'Electronics'");
        h.handle("CREATE_LISTING user1 'Shoes' 'Running shoes' 100 'Sports'");
        h.handle("CREATE_LISTING user1 'T-shirt' 'White color' 20 'Sports'");

        assertEquals("Sports", h.handle("GET_TOP_CATEGORY user1"));
    }

    public void testGetTopCategoryUnknownUser() {
        CommandHandler h = createHandler();
        assertEquals("Error - unknown user", h.handle("GET_TOP_CATEGORY unknown"));
    }

    // ---- EDGE CASES ----

    public void testEmptyInput() {
        CommandHandler h = createHandler();
        assertEquals("", h.handle(""));
    }

    public void testUnknownCommand() {
        CommandHandler h = createHandler();
        assertEquals("Error - unknown command", h.handle("INVALID_COMMAND user1"));
    }
}
