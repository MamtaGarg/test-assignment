package marketplace.domain;

import java.util.Optional;

/**
 * Defines the available fields for sorting listings.
 * Extensible: add new enum values to support sorting by additional fields.
 */
public enum SortField {

    PRICE("sort_price"),
    TIME("sort_time");

    private final String value;

    SortField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Parses a string into a SortField.
     *
     * @param input the string to parse (e.g., "sort_price", "sort_time")
     * @return Optional containing the SortField if valid, empty otherwise
     */
    public static Optional<SortField> fromString(String input) {
        for (SortField field : values()) {
            if (field.value.equals(input)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }
}
