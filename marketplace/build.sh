#!/bin/bash
# Build script for the Marketplace CLI application
# Requires: JDK 8+
# This script compiles all Java source files and produces bytecode in build/classes

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src/main/java"
TEST_SRC_DIR="$SCRIPT_DIR/src/test/java"
BUILD_DIR="$SCRIPT_DIR/build"
CLASSES_DIR="$BUILD_DIR/classes"
TEST_CLASSES_DIR="$BUILD_DIR/test-classes"

# Verify JDK is available
if ! command -v javac &> /dev/null; then
    echo "Error: javac not found. Please ensure JDK 8+ is installed." >&2
    exit 1
fi

echo "Building Marketplace CLI..."

# Clean build directory
rm -rf "$BUILD_DIR"
mkdir -p "$CLASSES_DIR" "$TEST_CLASSES_DIR" "$BUILD_DIR"

# Compile main sources
if [ -d "$SRC_DIR" ] && [ ! -z "$(find "$SRC_DIR" -name "*.java" 2>/dev/null)" ]; then
    echo "Compiling main sources..."
    find "$SRC_DIR" -name "*.java" > "$BUILD_DIR/sources.txt"
    if ! javac -d "$CLASSES_DIR" -encoding UTF-8 @"$BUILD_DIR/sources.txt" 2>&1; then
        echo "Error: Compilation failed" >&2
        exit 1
    fi
    MAIN_CLASS_COUNT=$(find "$CLASSES_DIR" -name "*.class" 2>/dev/null | wc -l)
    echo "  Compiled main classes: $MAIN_CLASS_COUNT"
else
    echo "Error: No Java source files found in $SRC_DIR" >&2
    exit 1
fi

# Compile test sources (optional)
if [ -d "$TEST_SRC_DIR" ] && [ ! -z "$(find "$TEST_SRC_DIR" -name "*.java" 2>/dev/null)" ]; then
    echo "Compiling test sources..."
    find "$TEST_SRC_DIR" -name "*.java" > "$BUILD_DIR/test-sources.txt"
    if ! javac -d "$TEST_CLASSES_DIR" -encoding UTF-8 -cp "$CLASSES_DIR" @"$BUILD_DIR/test-sources.txt" 2>&1; then
        echo "Error: Test compilation failed" >&2
        exit 1
    fi
    TEST_CLASS_COUNT=$(find "$TEST_CLASSES_DIR" -name "*.class" 2>/dev/null | wc -l)
    echo "  Compiled test classes: $TEST_CLASS_COUNT"
fi

echo ""
echo "✓ Build successful!"
echo "  Main classes:  $CLASSES_DIR"
echo "  Test classes:  $TEST_CLASSES_DIR"
