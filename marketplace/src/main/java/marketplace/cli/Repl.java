package marketplace.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Read-Eval-Print Loop (REPL) for the marketplace CLI.
 * <p>
 * Reads commands from an InputStream (typically stdin), processes them through
 * the CommandHandler, and writes output to a PrintStream (typically stdout).
 * Using InputStream/PrintStream allows easy testing without actual stdin/stdout.
 */
public class Repl {

    private final CommandHandler handler;
    private final BufferedReader reader;
    private final PrintStream writer;

    public Repl(CommandHandler handler, InputStream input, OutputStream output) {
        this.handler = handler;
        this.reader = new BufferedReader(new InputStreamReader(input));
        this.writer = (output instanceof PrintStream)
                ? (PrintStream) output
                : new PrintStream(output);
    }

    /**
     * Starts the REPL loop. Displays a "# " prompt, reads input, processes the
     * command, and prints the result. Continues until EOF or I/O error.
     */
    public void run() {
        try {
            while (true) {
                writer.print("# ");
                writer.flush();

                String line = reader.readLine();
                if (line == null) {
                    break; // EOF
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String result = handler.handle(line);
                if (!result.isEmpty()) {
                    writer.println(result);
                }
            }
        } catch (IOException e) {
            // Stream closed or I/O error - exit gracefully
        }
    }
}
