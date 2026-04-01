# Marketplace CLI Application

A command-line marketplace application that allows users to register, create/delete/view listings, browse categories, and find the top category.

## Prerequisites

- **JDK 8** (tested with OpenJDK 1.8 on Debian 9)
- No external libraries or build managers required (uses only JDK standard library)
- No Maven, Gradle, or Ant needed — plain `javac` and `java` only

## Build

```bash
# Using the build script:
./build.sh

# Using Make:
make build
```

## Run

```bash
# Using the run script (builds automatically if needed):
./run.sh

# Using Make:
make run

# With piped input:
echo "REGISTER user1" | ./run.sh
./run.sh < test/input.txt
```

## Test

```bash
# Using Make:
make test

# Or manually (after building):
java -cp build/classes:build/test-classes marketplace.TestRunner
```

Tests use a lightweight built-in test framework (no JUnit dependency) that discovers and runs all `test*` methods via reflection.

## Project Structure

```
marketplace/
├── src/
│   ├── main/java/marketplace/
│   │   ├── Main.java                              # Entry point - wires dependencies, starts REPL
│   │   ├── domain/                                 # Pure domain layer (no external dependencies)
│   │   │   ├── User.java                           # User entity (immutable)
│   │   │   ├── Listing.java                        # Listing entity (immutable) + formatters
│   │   │   ├── SortField.java                      # Sort field enum with parser
│   │   │   ├── SortOrder.java                      # Sort order enum with parser
│   │   │   ├── MarketplaceException.java           # Base exception
│   │   │   ├── UserAlreadyExistsException.java     # REGISTER duplicate
│   │   │   ├── UnknownUserException.java           # Authentication failure
│   │   │   ├── ListingNotFoundException.java       # GET/DELETE not found
│   │   │   ├── ListingOwnerMismatchException.java  # DELETE wrong owner
│   │   │   └── CategoryNotFoundException.java      # GET_CATEGORY miss
│   │   ├── repository/                             # Data access layer (interfaces + implementations)
│   │   │   ├── UserRepository.java                 # User persistence contract
│   │   │   ├── ListingRepository.java              # Listing persistence contract
│   │   │   ├── InMemoryUserRepository.java         # In-memory user store
│   │   │   └── InMemoryListingRepository.java      # In-memory listing store with category index
│   │   ├── service/                                # Business logic layer
│   │   │   └── MarketplaceService.java             # Core marketplace operations
│   │   └── cli/                                    # Presentation layer
│   │       ├── CommandParser.java                  # Quoted-argument parser
│   │       ├── CommandHandler.java                 # Command routing to service methods
│   │       └── Repl.java                           # Read-Eval-Print Loop
│   └── test/java/marketplace/
│       ├── TestFramework.java                      # Lightweight test framework (no JUnit)
│       ├── TestRunner.java                         # Test runner entry point
│       ├── domain/
│       │   ├── SortFieldTest.java
│       │   └── SortOrderTest.java
│       ├── repository/
│       │   ├── InMemoryUserRepositoryTest.java
│       │   └── InMemoryListingRepositoryTest.java
│       ├── service/
│       │   └── MarketplaceServiceTest.java
│       └── cli/
│           ├── CommandParserTest.java
│           ├── CommandHandlerTest.java
│           └── ReplIntegrationTest.java
├── test/
│   └── input.txt                                   # Sample input for manual testing
├── build.sh                                        # Build script
├── run.sh                                          # Run script
├── Makefile                                        # Build automation
└── README.md                                       # This file
```

## Architecture

The application follows a **layered architecture** with clear separation of concerns:

```
Main → Repl → CommandHandler → MarketplaceService → Repositories
```

### Layers

| Layer | Package | Responsibility |
|-------|---------|----------------|
| **Domain** | `marketplace.domain` | Pure domain models, typed exceptions, enums. Zero dependencies on other layers. |
| **Repository** | `marketplace.repository` | Data access contracts (interfaces) + in-memory implementations. |
| **Service** | `marketplace.service` | Business logic and orchestration. Depends on repository interfaces only. |
| **CLI** | `marketplace.cli` | Input parsing, command routing, output formatting. |

### SOLID Principles Applied

| Principle | Implementation |
|-----------|----------------|
| **Single Responsibility** | Each class has one clear purpose (e.g., `CommandParser` only parses, `CommandHandler` only routes) |
| **Open/Closed** | New commands, sort options, or storage backends can be added without modifying existing code. Typed exception hierarchy is extensible. |
| **Liskov Substitution** | `UserRepository` and `ListingRepository` interfaces can be satisfied by any implementation (in-memory, database, etc.) |
| **Interface Segregation** | Separate `UserRepository` and `ListingRepository` interfaces — no client is forced to depend on methods it doesn't use |
| **Dependency Inversion** | `MarketplaceService` depends on abstractions (interfaces), not concrete implementations |

### Additional Design Decisions

- **Immutable domain objects**: `User` and `Listing` are immutable once created, preventing unintended state mutations
- **Typed exceptions**: Each business error has its own exception class with the exact error message for the CLI, enabling clean `try/catch` in the handler
- **Factory methods**: `ListingNotFoundException.forDelete()` and `forGet()` produce the correct error message for each context
- **I/O abstraction**: `Repl` accepts `InputStream`/`OutputStream` for easy testing without real stdin/stdout

### Performance Optimization

- **`GET_TOP_CATEGORY` is O(1)** — cached top-category tracker updated on writes, recalculated only when the top category's count changes
- **Category lookups** use a `HashMap` index for O(1) access
- **Listing lookups by ID** are O(1) via `HashMap`
- **Tie-breaking in sorts** uses listing ID (creation order) for deterministic results even when timestamps/prices are equal
- **Tie-breaking in top category** retains the previous leader when counts are equal, ensuring deterministic behavior

## Supported Commands

| Command | Description |
|---------|-------------|
| `REGISTER <username>` | Register a new user |
| `CREATE_LISTING <username> <title> <description> <price> <category>` | Create a new listing |
| `DELETE_LISTING <username> <listing_id>` | Delete a listing (owner only) |
| `GET_LISTING <username> <listing_id>` | Get listing details |
| `GET_CATEGORY <username> <category> <sort_field> <sort_order>` | List all items in a category |
| `GET_TOP_CATEGORY <username>` | Get the category with the most listings |

**Note:** Arguments containing spaces should be enclosed in single quotes (e.g., `'Phone model 8'`).

## Troubleshooting

### Error: `java.lang.NoClassDefFoundError: marketplace/service/MarketplaceService$1`

This error occurs when inner classes (anonymous/lambda classes) are not properly compiled or the classpath is incomplete.

**Solutions:**

1. **Clean rebuild (most reliable)**:
   ```bash
   cd marketplace
   make clean
   make build
   ./run.sh < input.txt
   ```

2. **Verify the inner class file exists**:
   ```bash
   ls -la build/classes/marketplace/service/MarketplaceService\$1.class
   ```
   If the file doesn't exist, rebuild using the commands above.

3. **Use explicit classpath when running manually**:
   ```bash
   # From the project root
   java -cp marketplace/build/classes marketplace.Main < input.txt

   # Or from the marketplace directory
   cd marketplace && java -cp build/classes marketplace.Main < input.txt
   ```

4. **Ensure correct directory**:
   - The classpath must point to the `build/classes` directory where compiled classes are located
   - If running from a different directory, use absolute paths or `$(pwd)` expansion

5. **Check Java version compatibility**:
   ```bash
   javac -version
   java -version
   ```
   The build supports Java 8+. Modern Java (11+) is also tested and works.

### Error: `Error - unknown user` when user exists

This usually indicates case-sensitivity issues. Usernames are case-insensitive in the design.

- Ensure you're using the exact same case as registered (or different cases, as they should work)
- Example: `REGISTER User1` then `CREATE_LISTING user1 ...` should work

### Build fails with "No Java source files found"

- Verify the project structure:
  ```bash
  ls -la src/main/java/marketplace/
  ```
- Ensure source files are in the correct location before running build
