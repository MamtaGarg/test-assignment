package marketplace.repository;

import marketplace.domain.Listing;

import java.time.LocalDateTime;
import java.util.List;

import static marketplace.TestFramework.*;

/**
 * Tests for InMemoryListingRepository.
 */
public class InMemoryListingRepositoryTest {

    private Listing createListing(String title, int price, String username, String category) {
        return new Listing(0, title, "desc", price, username, category, LocalDateTime.now());
    }

    public void testAddReturnsIncrementingIds() {
        InMemoryListingRepository repo = new InMemoryListingRepository();

        int id1 = repo.add(createListing("Phone", 1000, "user1", "Electronics"));
        int id2 = repo.add(createListing("Shoes", 100, "user1", "Sports"));

        assertEquals(100001, id1);
        assertEquals(100002, id2);
    }

    public void testGetExisting() {
        InMemoryListingRepository repo = new InMemoryListingRepository();
        int id = repo.add(createListing("Phone", 1000, "user1", "Electronics"));

        Listing listing = repo.get(id);
        assertNotNull("listing should be found", listing);
        assertEquals("Phone", listing.getTitle());
        assertEquals(id, listing.getId());
    }

    public void testGetNonExisting() {
        InMemoryListingRepository repo = new InMemoryListingRepository();
        assertNull(repo.get(999999));
    }

    public void testDeleteExisting() {
        InMemoryListingRepository repo = new InMemoryListingRepository();
        int id = repo.add(createListing("Phone", 1000, "user1", "Electronics"));

        assertTrue("delete should return true", repo.delete(id));
        assertNull(repo.get(id));
    }

    public void testDeleteNonExisting() {
        InMemoryListingRepository repo = new InMemoryListingRepository();
        assertFalse("delete non-existing should return false", repo.delete(999999));
    }

    public void testGetByCategory() {
        InMemoryListingRepository repo = new InMemoryListingRepository();
        repo.add(createListing("Phone", 1000, "user1", "Electronics"));
        repo.add(createListing("Shoes", 100, "user1", "Sports"));
        repo.add(createListing("T-shirt", 20, "user2", "Sports"));

        List<Listing> electronics = repo.getByCategory("Electronics");
        assertEquals(1, electronics.size());

        List<Listing> sports = repo.getByCategory("Sports");
        assertEquals(2, sports.size());

        // Case-insensitive category lookup
        List<Listing> sportsLower = repo.getByCategory("sports");
        assertEquals(2, sportsLower.size());
    }

    public void testGetByCategoryEmpty() {
        InMemoryListingRepository repo = new InMemoryListingRepository();
        List<Listing> result = repo.getByCategory("Fashion");
        assertTrue("empty category should return empty list", result.isEmpty());
    }

    public void testCategoryExists() {
        InMemoryListingRepository repo = new InMemoryListingRepository();
        repo.add(createListing("Phone", 1000, "user1", "Electronics"));

        assertTrue("Electronics should exist", repo.categoryExists("Electronics"));
        assertTrue("electronics (lowercase) should exist", repo.categoryExists("electronics"));
        assertFalse("Fashion should not exist", repo.categoryExists("Fashion"));
    }

    public void testGetTopCategory() {
        InMemoryListingRepository repo = new InMemoryListingRepository();

        // Empty repo
        assertEquals("", repo.getTopCategory());

        // Add one electronics
        repo.add(createListing("Phone", 1000, "user1", "Electronics"));
        assertEquals("Electronics", repo.getTopCategory());

        // Add two sports (Sports becomes top)
        repo.add(createListing("Shoes", 100, "user1", "Sports"));
        repo.add(createListing("T-shirt", 20, "user2", "Sports"));
        assertEquals("Sports", repo.getTopCategory());
    }

    public void testDeleteUpdatesTopCategory() {
        InMemoryListingRepository repo = new InMemoryListingRepository();

        int id1 = repo.add(createListing("Phone", 1000, "user1", "Electronics"));
        int id2 = repo.add(createListing("Shoes", 100, "user1", "Sports"));
        int id3 = repo.add(createListing("T-shirt", 20, "user2", "Sports"));

        // Sports=2, Electronics=1 -> Sports is top
        assertEquals("Sports", repo.getTopCategory());

        // Delete one sports -> tied at 1 each, Sports retains top
        repo.delete(id3);
        assertEquals("Sports", repo.getTopCategory());

        // Delete last sports -> Electronics is only category
        repo.delete(id2);
        assertEquals("Electronics", repo.getTopCategory());

        // Delete last listing
        repo.delete(id1);
        assertEquals("", repo.getTopCategory());
    }
}
