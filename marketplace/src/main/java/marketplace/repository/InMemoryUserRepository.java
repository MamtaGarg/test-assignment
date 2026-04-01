package marketplace.repository;

import marketplace.domain.User;
import marketplace.domain.UserAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of UserRepository.
 * Usernames are stored and compared in lowercase to ensure case-insensitivity.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public void add(User user) throws UserAlreadyExistsException {
        String key = user.getUsername().toLowerCase();
        if (users.containsKey(key)) {
            throw new UserAlreadyExistsException();
        }
        users.put(key, user);
    }

    @Override
    public boolean exists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    @Override
    public User get(String username) {
        return users.get(username.toLowerCase());
    }
}
