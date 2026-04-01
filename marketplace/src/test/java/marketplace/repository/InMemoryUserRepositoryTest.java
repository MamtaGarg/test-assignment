package marketplace.repository;

import marketplace.domain.User;
import marketplace.domain.UserAlreadyExistsException;

import static marketplace.TestFramework.*;

/**
 * Tests for InMemoryUserRepository.
 */
public class InMemoryUserRepositoryTest {

    public void testAddAndGet() throws UserAlreadyExistsException {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.add(new User("user1"));

        User user = repo.get("user1");
        assertNotNull("user should be found", user);
        assertEquals("user1", user.getUsername());
    }

    public void testAddDuplicateThrows() throws UserAlreadyExistsException {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.add(new User("user1"));

        try {
            repo.add(new User("user1"));
            fail("Expected UserAlreadyExistsException");
        } catch (UserAlreadyExistsException e) {
            assertEquals("Error - user already existing", e.getMessage());
        }
    }

    public void testCaseInsensitiveDuplicate() throws UserAlreadyExistsException {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.add(new User("user1"));

        try {
            repo.add(new User("USER1"));
            fail("Expected UserAlreadyExistsException for case-insensitive match");
        } catch (UserAlreadyExistsException e) {
            // expected
        }
    }

    public void testExists() throws UserAlreadyExistsException {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.add(new User("testuser"));

        assertTrue("exact match should exist", repo.exists("testuser"));
        assertTrue("uppercase should exist", repo.exists("TESTUSER"));
        assertTrue("mixed case should exist", repo.exists("TestUser"));
        assertFalse("unknown should not exist", repo.exists("unknown"));
    }

    public void testGetNonExisting() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        assertNull(repo.get("nonexistent"));
    }

    public void testGetCaseInsensitive() throws UserAlreadyExistsException {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.add(new User("User1"));

        assertNotNull("lowercase get should work", repo.get("user1"));
        assertNotNull("uppercase get should work", repo.get("USER1"));
    }
}
