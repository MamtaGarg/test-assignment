package marketplace.domain;

/**
 * Thrown when attempting to register a user with a username that already exists.
 */
public class UserAlreadyExistsException extends MarketplaceException {

    public UserAlreadyExistsException() {
        super("Error - user already existing");
    }
}
