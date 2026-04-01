package marketplace.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an item put up for sale on the marketplace.
 * Each listing is associated with exactly one user and one category.
 * Immutable once created (only Create, Read, Delete - no Update).
 */
public class Listing {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int id;
    private final String title;
    private final String description;
    private final int price;
    private final String username;
    private final String category;
    private final LocalDateTime createdAt;

    public Listing(int id, String title, String description, int price,
                   String username, String category, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.username = username;
        this.category = category;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getUsername() {
        return username;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the full listing representation for GET_LISTING.
     * Format: title|description|price|created_at|category|username
     */
    public String formatFull() {
        return String.format("%s|%s|%d|%s|%s|%s",
                title, description, price, createdAt.format(FORMATTER), category, username);
    }

    /**
     * Returns the listing representation for GET_CATEGORY views.
     * Format: title|description|price|created_at|category|username
     */
    public String formatCategory() {
        return String.format("%s|%s|%d|%s|%s|%s",
                title, description, price, createdAt.format(FORMATTER), category, username);
    }

    @Override
    public String toString() {
        return "Listing{id=" + id + ", title='" + title + "'}";
    }
}
