package marketplace.domain;

/**
 * Represents a registered user on the marketplace.
 * Users are identified by a unique, case-insensitive username.
 * Immutable value object.
 */
public class User {

    private final String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equalsIgnoreCase(user.username);
    }

    @Override
    public int hashCode() {
        return username.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "User{username='" + username + "'}";
    }
}
