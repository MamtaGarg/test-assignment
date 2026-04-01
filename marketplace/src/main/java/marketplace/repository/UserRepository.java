package marketplace.repository;

import marketplace.domain.User;
import marketplace.domain.UserAlreadyExistsException;

/**
 * Contract for user persistence operations.
 * Implementations can range from in-memory stores to databases.
 * Follows Interface Segregation Principle - only user-related operations.
 */
public interface UserRepository {

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @throws UserAlreadyExistsException if the username is already taken (case-insensitive)
     */
    void add(User user) throws UserAlreadyExistsException;

    /**
     * Checks if a user with the given username exists (case-insensitive).
     *
     * @param username the username to check
     * @return true if the user exists
     */
    boolean exists(String username);

    /**
     * Retrieves a user by username (case-insensitive).
     *
     * @param username the username to look up
     * @return the User, or null if not found
     */
    User get(String username);
}
