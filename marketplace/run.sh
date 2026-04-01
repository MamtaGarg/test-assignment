#!/bin/bash
# Run script for the Marketplace CLI application
# Usage: ./run.sh
# The application reads commands from STDIN and outputs to STDOUT.
#
# Examples:
#   ./run.sh                           # Interactive mode
#   echo "REGISTER user1" | ./run.sh   # Pipe input
#   ./run.sh < input.txt               # File input

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CLASSES_DIR="$SCRIPT_DIR/build/classes"

# Verify Java is available
if ! command -v java &> /dev/null; then
    echo "Error: java not found. Please ensure JDK 8+ is installed." >&2
    exit 1
fi

# Build if classes don't exist or if sources are newer
if [ ! -d "$CLASSES_DIR" ] || [ ! -f "$CLASSES_DIR/marketplace/Main.class" ]; then
    echo "Building project..." >&2
    "$SCRIPT_DIR/build.sh" >&2
fi

# Verify that the main class exists
if [ ! -f "$CLASSES_DIR/marketplace/Main.class" ]; then
    echo "Error: Main.class not found after build. Build may have failed." >&2
    exit 1
fi

# Verify all required marketplace classes are present
if [ ! -f "$CLASSES_DIR/marketplace/service/MarketplaceService.class" ]; then
    echo "Error: MarketplaceService.class not found. Build may be incomplete." >&2
    exit 1
fi

# Verify the inner Comparator class is present (fixes the NoClassDefFoundError)
if [ ! -f "$CLASSES_DIR/marketplace/service/MarketplaceService\$1.class" ]; then
    echo "Warning: Inner class MarketplaceService\$1.class not found. Rebuilding..." >&2
    "$SCRIPT_DIR/build.sh" >&2
fi

# Run the application with full classpath
exec java -cp "$CLASSES_DIR" marketplace.Main
