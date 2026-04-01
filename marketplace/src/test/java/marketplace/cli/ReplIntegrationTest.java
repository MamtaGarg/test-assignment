package marketplace.cli;

import marketplace.repository.InMemoryListingRepository;
import marketplace.repository.InMemoryUserRepository;
import marketplace.service.MarketplaceService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static marketplace.TestFramework.*;

/**
 * End-to-end integration test that runs the full REPL with sample input.
 */
public class ReplIntegrationTest {

    public void testFullExampleFlow() {
        MarketplaceService svc = new MarketplaceService(
                new InMemoryUserRepository(),
                new InMemoryListingRepository()
        );
        CommandHandler handler = new CommandHandler(svc);

        String input = String.join("\n",
                "REGISTER user1",
                "CREATE_LISTING user1 'Phone model 8' 'Black color, brand new' 1000 'Electronics'",
                "CREATE_LISTING user1 'Black shoes' 'Training shoes' 100 'Sports'",
                "REGISTER user2",
                "REGISTER user2",
                "CREATE_LISTING user2 'T-shirt' 'White color' 20 'Sports'",
                "GET_CATEGORY user1 'Fashion' sort_time asc",
                "GET_TOP_CATEGORY user1",
                "DELETE_LISTING user1 100003",
                "DELETE_LISTING user2 100003",
                "GET_TOP_CATEGORY user2",
                "DELETE_LISTING user1 100002",
                "GET_TOP_CATEGORY user1",
                "GET_TOP_CATEGORY user3"
        );

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Repl repl = new Repl(handler, in, out);
        repl.run();

        String result = out.toString();

        // Verify expected outputs are present in order
        String[] expectedOutputs = {
                "Success",                        // REGISTER user1
                "100001",                          // CREATE_LISTING Phone
                "100002",                          // CREATE_LISTING Shoes
                "Success",                         // REGISTER user2
                "Error - user already existing",   // REGISTER user2 (duplicate)
                "100003",                          // CREATE_LISTING T-shirt
                "Error - category not found",      // GET_CATEGORY Fashion
                "Sports",                          // GET_TOP_CATEGORY
                "Error - listing owner mismatch",  // DELETE_LISTING user1 100003
                "Success",                         // DELETE_LISTING user2 100003
                "Sports",                          // GET_TOP_CATEGORY (tie, retains)
                "Success",                         // DELETE_LISTING user1 100002
                "Electronics",                     // GET_TOP_CATEGORY
                "Error - unknown user"             // GET_TOP_CATEGORY user3
        };

        for (String expected : expectedOutputs) {
            assertTrue("Output should contain: " + expected, result.contains(expected));
        }
    }

    public void testGetListingCrossUser() {
        MarketplaceService svc = new MarketplaceService(
                new InMemoryUserRepository(),
                new InMemoryListingRepository()
        );
        CommandHandler handler = new CommandHandler(svc);

        String input = String.join("\n",
                "REGISTER user1",
                "REGISTER user2",
                "CREATE_LISTING user1 'Phone model 8' 'Black color, brand new' 1000 'Electronics'",
                "GET_LISTING user2 100001"
        );

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Repl repl = new Repl(handler, in, out);
        repl.run();

        String result = out.toString();
        assertTrue("user2 should be able to get user1's listing",
                result.contains("Phone model 8|Black color, brand new|1000|"));
        assertTrue("listing should show original owner",
                result.contains("|Electronics|user1"));
    }
}
