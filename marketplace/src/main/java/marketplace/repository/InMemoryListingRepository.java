package marketplace.repository;

import marketplace.domain.Listing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of ListingRepository.
 * <p>
 * Maintains the following data structures for efficient operations:
 * <ul>
 *   <li>listings: Map of ID -> Listing for O(1) lookup by ID</li>
 *   <li>categoryIndex: Map of lowercase(category) -> list of listing IDs for efficient category queries</li>
 *   <li>categoryCounts: Map of lowercase(category) -> count for O(1) top category computation</li>
 *   <li>topCategory/topCount: Cached top category for O(1) read-heavy GET_TOP_CATEGORY</li>
 * </ul>
 * <p>
 * Listing IDs start from 100001 as specified in the problem statement.
 */
public class InMemoryListingRepository implements ListingRepository {

    private final Map<Integer, Listing> listings = new HashMap<>();
    private final Map<String, List<Integer>> categoryIndex = new HashMap<>();
    private final Map<String, Integer> categoryCounts = new HashMap<>();

    private String topCategory = "";
    private int topCount = 0;
    private int nextId = 100001;

    @Override
    public int add(Listing listing) {
        int id = nextId++;

        // Create the listing with the assigned ID (Listing is immutable, so we create a new one)
        Listing stored = new Listing(id, listing.getTitle(), listing.getDescription(),
                listing.getPrice(), listing.getUsername(), listing.getCategory(), listing.getCreatedAt());
        listings.put(id, stored);

        String catKey = listing.getCategory().toLowerCase();

        // Update category index
        categoryIndex.computeIfAbsent(catKey, k -> new ArrayList<>()).add(id);

        // Update category count
        int newCount = categoryCounts.merge(catKey, 1, Integer::sum);

        // Update top category cache (strict > so first category to reach a count holds position)
        if (newCount > topCount) {
            topCategory = listing.getCategory();
            topCount = newCount;
        }

        return id;
    }

    @Override
    public Listing get(int id) {
        return listings.get(id);
    }

    @Override
    public boolean delete(int id) {
        Listing listing = listings.remove(id);
        if (listing == null) {
            return false;
        }

        String catKey = listing.getCategory().toLowerCase();

        // Remove from category index
        List<Integer> ids = categoryIndex.get(catKey);
        if (ids != null) {
            Iterator<Integer> it = ids.iterator();
            while (it.hasNext()) {
                if (it.next() == id) {
                    it.remove();
                    break;
                }
            }
            if (ids.isEmpty()) {
                categoryIndex.remove(catKey);
            }
        }

        // Update category count
        Integer count = categoryCounts.get(catKey);
        if (count != null) {
            if (count <= 1) {
                categoryCounts.remove(catKey);
            } else {
                categoryCounts.put(catKey, count - 1);
            }
        }

        // Recalculate top category if the deleted listing's category was the top
        if (topCategory.equalsIgnoreCase(listing.getCategory())) {
            recalculateTopCategory();
        }

        return true;
    }

    @Override
    public List<Listing> getByCategory(String category) {
        String catKey = category.toLowerCase();
        List<Integer> ids = categoryIndex.get(catKey);
        if (ids == null) {
            return new ArrayList<>();
        }

        List<Listing> result = new ArrayList<>(ids.size());
        for (int id : ids) {
            Listing listing = listings.get(id);
            if (listing != null) {
                result.add(listing);
            }
        }
        return result;
    }

    @Override
    public boolean categoryExists(String category) {
        String catKey = category.toLowerCase();
        Integer count = categoryCounts.get(catKey);
        return count != null && count > 0;
    }

    @Override
    public String getTopCategory() {
        return topCategory;
    }

    /**
     * Recalculates the top category by scanning all category counts.
     * In case of a tie, the previously leading category retains its position
     * for deterministic behavior.
     */
    private void recalculateTopCategory() {
        String prevTop = topCategory.toLowerCase();
        topCategory = "";
        topCount = 0;

        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            String catKey = entry.getKey();
            int count = entry.getValue();

            if (count > topCount || (count == topCount && catKey.equals(prevTop))) {
                topCount = count;
                // Retrieve original (case-preserved) category name from a listing
                List<Integer> ids = categoryIndex.get(catKey);
                if (ids != null && !ids.isEmpty()) {
                    Listing sample = listings.get(ids.get(0));
                    if (sample != null) {
                        topCategory = sample.getCategory();
                    }
                }
            }
        }
    }
}
