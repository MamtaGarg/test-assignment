package marketplace.service;

import marketplace.domain.*;
import marketplace.repository.InMemoryListingRepository;
import marketplace.repository.InMemoryUserRepository;

import java.util.List;

import static marketplace.TestFramework.*;

/**
 * Tests for MarketplaceService business logic.
 */
public class MarketplaceServiceTest {

    private MarketplaceService createService() {
        return new MarketplaceService(
                new InMemoryUserRepository(),
                new InMemoryListingRepository()
        );
    }

    // ---- REGISTER ----

    public void testRegisterUser() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1"); // should not throw
    }

    public void testRegisterDuplicateUser() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");

        try {
            svc.registerUser("user1");
            fail("Expected UserAlreadyExistsException");
        } catch (UserAlreadyExistsException e) {
            assertEquals("Error - user already existing", e.getMessage());
        }
    }

    public void testRegisterCaseInsensitiveDuplicate() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");

        try {
            svc.registerUser("USER1");
            fail("Expected UserAlreadyExistsException for case-insensitive duplicate");
        } catch (UserAlreadyExistsException e) {
            // expected
        }
    }

    // ---- CREATE_LISTING ----

    public void testCreateListing() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");

        int id = svc.createListing("user1", "Phone", "Brand new", 1000, "Electronics");
        assertEquals(100001, id);
    }

    public void testCreateListingUnknownUser() {
        MarketplaceService svc = createService();

        try {
            svc.createListing("unknown", "Phone", "Brand new", 1000, "Electronics");
            fail("Expected UnknownUserException");
        } catch (MarketplaceException e) {
            assertEquals("Error - unknown user", e.getMessage());
        }
    }

    // ---- DELETE_LISTING ----

    public void testDeleteListing() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        int id = svc.createListing("user1", "Phone", "Brand new", 1000, "Electronics");

        svc.deleteListing("user1", id); // should not throw
    }

    public void testDeleteListingOwnerMismatch() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        svc.registerUser("user2");
        int id = svc.createListing("user1", "Phone", "Brand new", 1000, "Electronics");

        try {
            svc.deleteListing("user2", id);
            fail("Expected ListingOwnerMismatchException");
        } catch (ListingOwnerMismatchException e) {
            assertEquals("Error - listing owner mismatch", e.getMessage());
        }
    }

    public void testDeleteListingNotFound() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");

        try {
            svc.deleteListing("user1", 999999);
            fail("Expected ListingNotFoundException");
        } catch (ListingNotFoundException e) {
            assertEquals("Error - listing does not exist", e.getMessage());
        }
    }

    public void testDeleteListingUnknownUser() {
        MarketplaceService svc = createService();

        try {
            svc.deleteListing("unknown", 100001);
            fail("Expected UnknownUserException");
        } catch (MarketplaceException e) {
            assertEquals("Error - unknown user", e.getMessage());
        }
    }

    // ---- GET_LISTING ----

    public void testGetListing() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        int id = svc.createListing("user1", "Phone", "Brand new", 1000, "Electronics");

        Listing listing = svc.getListing("user1", id);
        assertEquals("Phone", listing.getTitle());
        assertEquals("Brand new", listing.getDescription());
        assertEquals(1000, listing.getPrice());
    }

    public void testGetListingCrossUser() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        svc.registerUser("user2");
        int id = svc.createListing("user1", "Phone", "Brand new", 1000, "Electronics");

        // user2 should be able to get user1's listing
        Listing listing = svc.getListing("user2", id);
        assertEquals("Phone", listing.getTitle());
    }

    public void testGetListingNotFound() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");

        try {
            svc.getListing("user1", 999999);
            fail("Expected ListingNotFoundException");
        } catch (ListingNotFoundException e) {
            assertEquals("Error - not found", e.getMessage());
        }
    }

    public void testGetListingUnknownUser() {
        MarketplaceService svc = createService();

        try {
            svc.getListing("unknown", 100001);
            fail("Expected UnknownUserException");
        } catch (MarketplaceException e) {
            assertEquals("Error - unknown user", e.getMessage());
        }
    }

    // ---- GET_CATEGORY ----

    public void testGetCategorySortPriceAsc() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        svc.createListing("user1", "Shoes", "Running shoes", 100, "Sports");
        svc.createListing("user1", "T-shirt", "White color", 20, "Sports");

        List<Listing> listings = svc.getCategory("user1", "Sports",
                SortField.PRICE, SortOrder.ASCENDING);

        assertEquals(2, listings.size());
        assertEquals(20, listings.get(0).getPrice());   // cheaper first
        assertEquals(100, listings.get(1).getPrice());
    }

    public void testGetCategorySortPriceDsc() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        svc.createListing("user1", "Shoes", "Running shoes", 100, "Sports");
        svc.createListing("user1", "T-shirt", "White color", 20, "Sports");

        List<Listing> listings = svc.getCategory("user1", "Sports",
                SortField.PRICE, SortOrder.DESCENDING);

        assertEquals(2, listings.size());
        assertEquals(100, listings.get(0).getPrice());  // expensive first
        assertEquals(20, listings.get(1).getPrice());
    }

    public void testGetCategorySortTimeDsc() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        svc.createListing("user1", "Shoes", "Running shoes", 100, "Sports");    // created first (ID 100001)
        svc.createListing("user1", "T-shirt", "White color", 20, "Sports");     // created second (ID 100002)

        List<Listing> listings = svc.getCategory("user1", "Sports",
                SortField.TIME, SortOrder.DESCENDING);

        assertEquals(2, listings.size());
        // Descending by time: newest first. With same timestamp, higher ID first
        assertEquals("T-shirt", listings.get(0).getTitle());
        assertEquals("Shoes", listings.get(1).getTitle());
    }

    public void testGetCategoryNotFound() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");

        try {
            svc.getCategory("user1", "Fashion", SortField.TIME, SortOrder.ASCENDING);
            fail("Expected CategoryNotFoundException");
        } catch (CategoryNotFoundException e) {
            assertEquals("Error - category not found", e.getMessage());
        }
    }

    public void testGetCategoryUnknownUser() {
        MarketplaceService svc = createService();

        try {
            svc.getCategory("unknown", "Sports", SortField.TIME, SortOrder.ASCENDING);
            fail("Expected UnknownUserException");
        } catch (MarketplaceException e) {
            assertEquals("Error - unknown user", e.getMessage());
        }
    }

    // ---- GET_TOP_CATEGORY ----

    public void testGetTopCategory() throws MarketplaceException {
        MarketplaceService svc = createService();
        svc.registerUser("user1");
        svc.registerUser("user2");

        svc.createListing("user1", "Phone", "Brand new", 1000, "Electronics");
        svc.createListing("user1", "Shoes", "Running shoes", 100, "Sports");
        svc.createListing("user2", "T-shirt", "White color", 20, "Sports");

        assertEquals("Sports", svc.getTopCategory("user1"));
    }

    public void testGetTopCategoryUnknownUser() {
        MarketplaceService svc = createService();

        try {
            svc.getTopCategory("unknown");
            fail("Expected UnknownUserException");
        } catch (MarketplaceException e) {
            assertEquals("Error - unknown user", e.getMessage());
        }
    }
}
