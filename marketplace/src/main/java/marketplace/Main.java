package marketplace;

import marketplace.cli.CommandHandler;
import marketplace.cli.Repl;
import marketplace.repository.InMemoryListingRepository;
import marketplace.repository.InMemoryUserRepository;
import marketplace.repository.ListingRepository;
import marketplace.repository.UserRepository;
import marketplace.service.MarketplaceService;

/**
 * Application entry point.
 * <p>
 * Wires up the dependency graph and starts the REPL:
 * <pre>
 *   Main -> Repl -> CommandHandler -> MarketplaceService -> Repositories
 * </pre>
 * <p>
 * Architecture follows a layered approach:
 * <ol>
 *   <li>CLI Layer (cli package)        - Input parsing and output formatting</li>
 *   <li>Service Layer (service package) - Business logic and orchestration</li>
 *   <li>Domain Layer (domain package)   - Pure domain models, errors, and types</li>
 *   <li>Repository Layer (repository)   - Data persistence abstraction</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) {
        // Initialize repositories (can be swapped for DB-backed implementations)
        UserRepository userRepo = new InMemoryUserRepository();
        ListingRepository listingRepo = new InMemoryListingRepository();

        // Initialize service with repository dependencies
        MarketplaceService service = new MarketplaceService(userRepo, listingRepo);

        // Initialize CLI handler and REPL
        CommandHandler handler = new CommandHandler(service);
        Repl repl = new Repl(handler, System.in, System.out);

        // Start the interactive loop
        repl.run();
    }
}
