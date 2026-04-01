package marketplace.domain;

import java.util.Optional;

/**
 * Defines the available sort ordering directions.
 */
public enum SortOrder {

    ASCENDING("asc"),
    DESCENDING("dsc");

    private final String value;

    SortOrder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Parses a string into a SortOrder.
     *
     * @param input the string to parse (e.g., "asc", "dsc")
     * @return Optional containing the SortOrder if valid, empty otherwise
     */
    public static Optional<SortOrder> fromString(String input) {
        for (SortOrder order : values()) {
            if (order.value.equals(input)) {
                return Optional.of(order);
            }
        }
        return Optional.empty();
    }
}
