package marketplace.domain;

/**
 * Thrown when a listing cannot be found by ID.
 * Uses different messages for DELETE vs GET operations as per the spec.
 */
public class ListingNotFoundException extends MarketplaceException {

    public ListingNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates an exception for DELETE_LISTING when the listing doesn't exist.
     */
    public static ListingNotFoundException forDelete() {
        return new ListingNotFoundException("Error - listing does not exist");
    }

    /**
     * Creates an exception for GET_LISTING when the listing doesn't exist.
     */
    public static ListingNotFoundException forGet() {
        return new ListingNotFoundException("Error - not found");
    }
}
