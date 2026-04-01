package marketplace.domain;

/**
 * Thrown when an operation references a user that does not exist.
 */
public class UnknownUserException extends MarketplaceException {

    public UnknownUserException() {
        super("Error - unknown user");
    }
}
