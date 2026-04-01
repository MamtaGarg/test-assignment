package marketplace.cli;

import marketplace.domain.*;
import marketplace.service.MarketplaceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Processes parsed CLI commands and delegates to the MarketplaceService.
 * <p>
 * Follows the Single Responsibility Principle: its only concern is translating
 * CLI input/output to/from service calls. All business logic resides in the service layer.
 */
public class CommandHandler {

    private final MarketplaceService service;
    private final CommandParser parser;

    public CommandHandler(MarketplaceService service) {
        this.service = service;
        this.parser = new CommandParser();
    }

    /**
     * Processes a single command line and returns the output string.
     *
     * @param line the raw command line input
     * @return the output to display to the user
     */
    public String handle(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        List<String> args = parser.parse(trimmed);
        if (args.isEmpty()) {
            return "";
        }

        String command = args.get(0).toUpperCase();

        switch (command) {
            case "REGISTER":
                return handleRegister(args);
            case "CREATE_LISTING":
                return handleCreateListing(args);
            case "DELETE_LISTING":
                return handleDeleteListing(args);
            case "GET_LISTING":
                return handleGetListing(args);
            case "GET_CATEGORY":
                return handleGetCategory(args);
            case "GET_TOP_CATEGORY":
                return handleGetTopCategory(args);
            default:
                return "Error - unknown command";
        }
    }

    private String handleRegister(List<String> args) {
        if (args.size() < 2) {
            return "Error - invalid arguments";
        }

        try {
            service.registerUser(args.get(1));
            return "Success";
        } catch (MarketplaceException e) {
            return e.getMessage();
        }
    }

    private String handleCreateListing(List<String> args) {
        if (args.size() < 6) {
            return "Error - invalid arguments";
        }

        String username = args.get(1);
        String title = args.get(2);
        String description = args.get(3);
        int price;
        try {
            price = Integer.parseInt(args.get(4));
        } catch (NumberFormatException e) {
            return "Error - invalid price";
        }
        String category = args.get(5);

        try {
            int id = service.createListing(username, title, description, price, category);
            return String.valueOf(id);
        } catch (MarketplaceException e) {
            return e.getMessage();
        }
    }

    private String handleDeleteListing(List<String> args) {
        if (args.size() < 3) {
            return "Error - invalid arguments";
        }

        String username = args.get(1);
        int listingId;
        try {
            listingId = Integer.parseInt(args.get(2));
        } catch (NumberFormatException e) {
            return "Error - invalid listing id";
        }

        try {
            service.deleteListing(username, listingId);
            return "Success";
        } catch (MarketplaceException e) {
            return e.getMessage();
        }
    }

    private String handleGetListing(List<String> args) {
        if (args.size() < 3) {
            return "Error - invalid arguments";
        }

        String username = args.get(1);
        int listingId;
        try {
            listingId = Integer.parseInt(args.get(2));
        } catch (NumberFormatException e) {
            return "Error - invalid listing id";
        }

        try {
            Listing listing = service.getListing(username, listingId);
            return listing.formatFull();
        } catch (MarketplaceException e) {
            return e.getMessage();
        }
    }

    private String handleGetCategory(List<String> args) {
        if (args.size() < 5) {
            return "Error - invalid arguments";
        }

        String username = args.get(1);
        String category = args.get(2);
        String sortFieldStr = args.get(3);
        String sortOrderStr = args.get(4);

        Optional<SortField> sortField = SortField.fromString(sortFieldStr);
        if (!sortField.isPresent()) {
            return "Error - invalid sort field";
        }

        Optional<SortOrder> sortOrder = SortOrder.fromString(sortOrderStr);
        if (!sortOrder.isPresent()) {
            return "Error - invalid sort order";
        }

        try {
            List<Listing> listings = service.getCategory(
                    username, category, sortField.get(), sortOrder.get());

            return listings.stream()
                    .map(Listing::formatCategory)
                    .collect(Collectors.joining("\n"));
        } catch (MarketplaceException e) {
            return e.getMessage();
        }
    }

    private String handleGetTopCategory(List<String> args) {
        if (args.size() < 2) {
            return "Error - invalid arguments";
        }

        try {
            return service.getTopCategory(args.get(1));
        } catch (MarketplaceException e) {
            return e.getMessage();
        }
    }
}
