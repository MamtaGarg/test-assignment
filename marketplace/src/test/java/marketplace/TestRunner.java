package marketplace;

import marketplace.cli.CommandHandlerTest;
import marketplace.cli.CommandParserTest;
import marketplace.cli.ReplIntegrationTest;
import marketplace.domain.SortFieldTest;
import marketplace.domain.SortOrderTest;
import marketplace.repository.InMemoryListingRepositoryTest;
import marketplace.repository.InMemoryUserRepositoryTest;
import marketplace.service.MarketplaceServiceTest;

/**
 * Runs all test suites.
 * Uses the lightweight TestFramework (no JUnit dependency required).
 */
public class TestRunner {

    public static void main(String[] args) {
        TestFramework framework = new TestFramework();

        framework.runTests(
                // Domain tests
                SortFieldTest.class,
                SortOrderTest.class,

                // Repository tests
                InMemoryUserRepositoryTest.class,
                InMemoryListingRepositoryTest.class,

                // Service tests
                MarketplaceServiceTest.class,

                // CLI tests
                CommandParserTest.class,
                CommandHandlerTest.class,

                // Integration tests
                ReplIntegrationTest.class
        );
    }
}
