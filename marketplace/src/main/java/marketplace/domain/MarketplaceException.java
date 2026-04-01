package marketplace.domain;

/**
 * Base exception for all marketplace business rule violations.
 * The message is designed to be directly printed to the user.
 */
public class MarketplaceException extends Exception {

    public MarketplaceException(String message) {
        super(message);
    }
}
