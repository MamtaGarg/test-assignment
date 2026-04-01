package marketplace.domain;

/**
 * Thrown when a category lookup finds no listings for the given category.
 */
public class CategoryNotFoundException extends MarketplaceException {

    public CategoryNotFoundException() {
        super("Error - category not found");
    }
}
