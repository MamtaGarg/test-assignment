.PHONY: build test run clean help

SRC_DIR = src/main/java
TEST_SRC_DIR = src/test/java
BUILD_DIR = build
CLASSES_DIR = $(BUILD_DIR)/classes
TEST_CLASSES_DIR = $(BUILD_DIR)/test-classes

help:
	@echo "Marketplace CLI - Build targets:"
	@echo "  make build    - Compile all Java sources"
	@echo "  make run      - Build and run the application (reads from STDIN)"
	@echo "  make test     - Build and run all tests"
	@echo "  make clean    - Remove build artifacts"

# Build the application
build: clean
	@mkdir -p $(CLASSES_DIR) $(TEST_CLASSES_DIR) $(BUILD_DIR)
	@echo "Compiling main sources..."
	@find $(SRC_DIR) -name "*.java" > $(BUILD_DIR)/sources.txt
	@if [ ! -s $(BUILD_DIR)/sources.txt ]; then \
		echo "Error: No Java source files found"; \
		exit 1; \
	fi
	@javac -d $(CLASSES_DIR) -encoding UTF-8 @$(BUILD_DIR)/sources.txt
	@echo "Compiling test sources..."
	@find $(TEST_SRC_DIR) -name "*.java" > $(BUILD_DIR)/test-sources.txt
	@if [ -s $(BUILD_DIR)/test-sources.txt ]; then \
		javac -d $(TEST_CLASSES_DIR) -encoding UTF-8 -cp $(CLASSES_DIR) @$(BUILD_DIR)/test-sources.txt; \
	fi
	@echo "✓ Build successful."
	@echo "  Classes compiled to: $(CLASSES_DIR)"

# Run the application
run: build
	@echo "Running Marketplace CLI..."
	@java -cp $(CLASSES_DIR) marketplace.Main

# Run all tests
test: build
	@echo "Running tests..."
	@java -cp $(CLASSES_DIR):$(TEST_CLASSES_DIR) marketplace.TestRunner

# Clean build artifacts
clean:
	@rm -rf $(BUILD_DIR)
	@echo "Cleaned."
