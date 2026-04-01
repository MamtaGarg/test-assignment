package marketplace.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses command line input into individual arguments.
 * Handles single-quoted strings as single arguments, allowing spaces within quoted values.
 * <p>
 * Example:
 *   "CREATE_LISTING user1 'Phone model 8' 'Black color, brand new' 1000 'Electronics'"
 *   becomes: ["CREATE_LISTING", "user1", "Phone model 8", "Black color, brand new", "1000", "Electronics"]
 */
public class CommandParser {

    /**
     * Parses a raw command line string into a list of arguments.
     *
     * @param line the raw input line
     * @return list of parsed arguments
     */
    public List<String> parse(String line) {
        List<String> args = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '\'') {
                inQuote = !inQuote;
            } else if (ch == ' ' && !inQuote) {
                if (current.length() > 0) {
                    args.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(ch);
            }
        }

        // Don't forget the last argument
        if (current.length() > 0) {
            args.add(current.toString());
        }

        return args;
    }
}
