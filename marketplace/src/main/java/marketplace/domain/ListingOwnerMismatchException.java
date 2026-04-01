package marketplace.domain;

/**
 * Thrown when a user tries to delete a listing they don't own.
 */
public class ListingOwnerMismatchException extends MarketplaceException {

    public ListingOwnerMismatchException() {
        super("Error - listing owner mismatch");
    }
}
