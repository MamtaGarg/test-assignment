# Marketplace CLI - Installation & Quick Start Guide

## System Requirements

- **JDK 8 or higher** (OpenJDK, Oracle JDK, Amazon Corretto, etc.)
- **Debian 9** (or any Unix-like system with bash)
- **Make** (optional, for using Makefile targets)
- No external dependencies or libraries required

## Verify Java Installation

```bash
# Check Java Compiler
javac -version
# Output: javac 1.8.x or higher

# Check Java Runtime
java -version
# Output: Java version 1.8 or higher
```

## Quick Start (30 seconds)

### Option 1: Using Make

```bash
cd marketplace
make build
make run
```

Then type commands at the prompt:
```
# REGISTER user1
# CREATE_LISTING user1 'Phone' 'Black, brand new' 1000 'Electronics'
# GET_LISTING user1 100001
# ctrl+D to exit
```

### Option 2: Using Scripts

```bash
cd marketplace
./build.sh
./run.sh
```

### Option 3: From Root Directory

```bash
# One-command build and run
java -cp marketplace/build/classes marketplace.Main

# Or with Make
make -C marketplace build
```

## Running Tests

```bash
cd marketplace
make test
```

Expected output:
```
========================================
Results: 69 passed, 0 failed
========================================
```

## Using with Input File

Create `test_input.txt`:
```
REGISTER user1
CREATE_LISTING user1 'Phone' 'Test phone' 1000 'Electronics'
GET_LISTING user1 100001
GET_TOP_CATEGORY user1
```

Run:
```bash
cd marketplace
./run.sh < test_input.txt
```

## Build Targets

```bash
cd marketplace

# Compile sources
make build
# or
./build.sh

# Run with STDIN
make run
# or
./run.sh

# Run all tests
make test

# Show help
make help

# Clean build artifacts
make clean
```

## Troubleshooting

### "java: command not found"

**Solution:** Install JDK 8 or higher
```bash
# macOS
brew install openjdk@11

# Ubuntu/Debian
sudo apt-get install default-jdk

# Or download from oracle.com or adoptopenjdk.net
```

### "javac: command not found"

**Solution:** Ensure JDK (not just JRE) is installed

### NoClassDefFoundError

**Solution:** Clean rebuild
```bash
make clean
make build
make run
```

### Permission denied on run.sh

**Solution:** Make script executable
```bash
chmod +x run.sh build.sh
```

## Example Session

```bash
$ cd marketplace
$ ./run.sh
# REGISTER user1
Success
# REGISTER user2
Success
# CREATE_LISTING user1 'Laptop' 'Dell XPS' 5000 'Electronics'
100001
# CREATE_LISTING user2 'Mouse' 'Wireless' 500 'Electronics'
100002
# GET_CATEGORY user1 'Electronics' sort_price dsc
Laptop|Dell XPS|5000|2026-04-01 12:34:56|Electronics|user1
Mouse|Wireless|500|2026-04-01 12:34:57|Electronics|user2
# GET_TOP_CATEGORY user1
Electronics
# DELETE_LISTING user2 100002
Success
# DELETE_LISTING user1 100001
Success
# GET_TOP_CATEGORY user1
Error - category not found
```

## Architecture Overview

```
Input (STDIN)
    ↓
Command Parser (marketplace.cli.CommandParser)
    ↓
Command Handler (marketplace.cli.CommandHandler)
    ↓
Marketplace Service (marketplace.service.MarketplaceService)
    ↓
Repositories (UserRepository, ListingRepository)
    ↓
Output (STDOUT)
```

## Project Contents

```
marketplace/
├── src/
│   ├── main/java/marketplace/     # Main application code
│   │   ├── Main.java               # Entry point
│   │   ├── cli/                    # Command-line interface
│   │   ├── service/                # Business logic
│   │   ├── repository/             # Data access layer
│   │   └── domain/                 # Domain models
│   └── test/java/marketplace/      # Test suite (69 tests)
├── test/
│   └── input.txt                  # Sample input for testing
├── build.sh                        # Build script
├── run.sh                          # Run script
├── Makefile                        # Build automation
└── README.md                       # Full documentation
```

## Supported Commands

| Command | Example |
|---------|---------|
| Register user | `REGISTER john` |
| Create listing | `CREATE_LISTING john 'iPhone' 'Black, new' 1000 'Electronics'` |
| Get listing | `GET_LISTING john 100001` |
| Get category | `GET_CATEGORY john 'Electronics' sort_price dsc` |
| Get top category | `GET_TOP_CATEGORY john` |
| Delete listing | `DELETE_LISTING john 100001` |

## Important Notes

- **Usernames are case-insensitive**: `User1` and `user1` refer to the same user
- **Arguments with spaces must be quoted**: Use single quotes `'like this'`
- **All output is to STDOUT**: Errors also appear on STDOUT, not STDERR
- **Commands are case-sensitive**: `REGISTER` works, `register` doesn't
- **Each command must be on a separate line** in input files

## Performance Characteristics

- **User lookup**: O(1) per command
- **Listing lookup**: O(1) per ID
- **Category listing**: O(n log n) where n = listings in category
- **Top category**: O(1) optimized with caching
- **Memory**: O(m + n) where m = users, n = listings

## Next Steps

1. ✅ Build: `make build`
2. ✅ Test: `make test` (should see "69 passed")
3. ✅ Run: `./run.sh` (interactive mode)
4. ✅ Integrate: Use in your application or API

## Support

For issues or questions:
1. Check README.md for detailed documentation
2. Review FIX_SUMMARY.md for known issues
3. Run `make test` to ensure everything works
4. Verify Java version with `javac -version`

## License

Internal project - all rights reserved.
