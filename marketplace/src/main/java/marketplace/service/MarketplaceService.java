package marketplace.service;

import marketplace.domain.*;
import marketplace.repository.ListingRepository;
import marketplace.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Core business logic for the marketplace.
 * <p>
 * Depends on repository interfaces (not concrete implementations),
 * following the Dependency Inversion Principle.
 * This allows easy swapping of storage backends and independent unit testing.
 */
public class MarketplaceService {

    private final UserRepository userRepo;
    private final ListingRepository listingRepo;

    public MarketplaceService(UserRepository userRepo, ListingRepository listingRepo) {
        this.userRepo = userRepo;
        this.listingRepo = listingRepo;
    }

    /**
     * Registers a new user on the marketplace.
     *
     * @param username the username to register
     * @throws UserAlreadyExistsException if the username is already taken (case-insensitive)
     */
    public void registerUser(String username) throws UserAlreadyExistsException {
        User user = new User(username);
        userRepo.add(user);
    }

    /**
     * Creates a new listing for the given user.
     *
     * @return the assigned listing ID
     * @throws UnknownUserException if the user doesn't exist
     */
    public int createListing(String username, String title, String description,
                             int price, String category) throws UnknownUserException {
        validateUserExists(username);

        Listing listing = new Listing(0, title, description, price, username, category, LocalDateTime.now());
        return listingRepo.add(listing);
    }

    /**
     * Deletes a listing by ID. Only the listing owner can delete it.
     *
     * @param username  the user requesting deletion
     * @param listingId the listing ID to delete
     * @throws UnknownUserException          if the user doesn't exist
     * @throws ListingNotFoundException      if the listing doesn't exist
     * @throws ListingOwnerMismatchException if the user is not the listing owner
     */
    public void deleteListing(String username, int listingId)
            throws UnknownUserException, ListingNotFoundException, ListingOwnerMismatchException {
        validateUserExists(username);

        Listing listing = listingRepo.get(listingId);
        if (listing == null) {
            throw ListingNotFoundException.forDelete();
        }

        if (!listing.getUsername().equals(username)) {
            throw new ListingOwnerMismatchException();
        }

        listingRepo.delete(listingId);
    }

    /**
     * Retrieves a listing by ID. Username is used for authentication only;
     * any listing can be retrieved regardless of owner.
     *
     * @param username  the authenticated user
     * @param listingId the listing ID to retrieve
     * @return the Listing
     * @throws UnknownUserException     if the user doesn't exist
     * @throws ListingNotFoundException if the listing doesn't exist
     */
    public Listing getListing(String username, int listingId)
            throws UnknownUserException, ListingNotFoundException {
        validateUserExists(username);

        Listing listing = listingRepo.get(listingId);
        if (listing == null) {
            throw ListingNotFoundException.forGet();
        }

        return listing;
    }

    /**
     * Retrieves all listings in a category, sorted by the specified field and order.
     * Uses listing ID as a stable tiebreaker (reflects creation order).
     *
     * @param username  the authenticated user
     * @param category  the category to query
     * @param sortField the field to sort by
     * @param sortOrder the sort direction
     * @return sorted list of listings
     * @throws UnknownUserException      if the user doesn't exist
     * @throws CategoryNotFoundException if the category has no listings
     */
    public List<Listing> getCategory(String username, String category,
                                     SortField sortField, SortOrder sortOrder)
            throws UnknownUserException, CategoryNotFoundException {
        validateUserExists(username);

        if (!listingRepo.categoryExists(category)) {
            throw new CategoryNotFoundException();
        }

        List<Listing> listings = listingRepo.getByCategory(category);
        Comparator<Listing> comparator = buildComparator(sortField, sortOrder);
        Collections.sort(listings, comparator);

        return listings;
    }

    /**
     * Returns the category with the most listings.
     * Optimized for read-heavy usage (O(1) in the repository layer).
     *
     * @param username the authenticated user
     * @return the top category name
     * @throws UnknownUserException if the user doesn't exist
     */
    public String getTopCategory(String username) throws UnknownUserException {
        validateUserExists(username);
        return listingRepo.getTopCategory();
    }

    /**
     * Validates that a user exists, throwing UnknownUserException if not.
     */
    private void validateUserExists(String username) throws UnknownUserException {
        if (!userRepo.exists(username)) {
            throw new UnknownUserException();
        }
    }

    /**
     * Builds a comparator for sorting listings based on the given field and order.
     * Uses listing ID as a stable tiebreaker to ensure deterministic ordering
     * when the primary sort field has equal values.
     */
    private Comparator<Listing> buildComparator(SortField sortField, SortOrder sortOrder) {
        Comparator<Listing> primary;

        switch (sortField) {
            case PRICE:
                primary = Comparator.comparingInt(Listing::getPrice);
                break;
            case TIME:
                primary = Comparator.comparing(Listing::getCreatedAt);
                break;
            default:
                primary = Comparator.comparingInt(Listing::getId);
                break;
        }

        // Tiebreaker by ID (creation order)
        Comparator<Listing> withTiebreaker = primary.thenComparingInt(Listing::getId);

        if (sortOrder == SortOrder.DESCENDING) {
            withTiebreaker = withTiebreaker.reversed();
        }

        return withTiebreaker;
    }
}
