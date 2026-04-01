package marketplace.repository;

import marketplace.domain.Listing;

import java.util.List;

/**
 * Contract for listing persistence operations.
 * Implementations can range from in-memory stores to databases.
 * Follows Interface Segregation Principle - only listing-related operations.
 */
public interface ListingRepository {

    /**
     * Stores a new listing and returns its assigned ID.
     *
     * @param listing the listing to store
     * @return the auto-generated listing ID
     */
    int add(Listing listing);

    /**
     * Retrieves a listing by ID.
     *
     * @param id the listing ID
     * @return the Listing, or null if not found
     */
    Listing get(int id);

    /**
     * Removes a listing by ID.
     *
     * @param id the listing ID
     * @return true if the listing was found and removed, false otherwise
     */
    boolean delete(int id);

    /**
     * Returns all listings in the given category.
     *
     * @param category the category name (case-insensitive)
     * @return list of listings, empty list if none found
     */
    List<Listing> getByCategory(String category);

    /**
     * Checks if any listings exist for the given category.
     *
     * @param category the category name (case-insensitive)
     * @return true if at least one listing exists in the category
     */
    boolean categoryExists(String category);

    /**
     * Returns the category name with the most listings.
     * Optimized for read-heavy access (e.g., home page).
     *
     * @return the top category name, or empty string if no listings exist
     */
    String getTopCategory();
}
