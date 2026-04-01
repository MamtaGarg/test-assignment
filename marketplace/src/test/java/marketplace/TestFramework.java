package marketplace;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight test framework using only JDK standard library.
 * Discovers and runs all public methods starting with "test" in registered test classes.
 */
public class TestFramework {

    private int passed = 0;
    private int failed = 0;
    private final List<String> failures = new ArrayList<>();

    public void runTests(Class<?>... testClasses) {
        for (Class<?> testClass : testClasses) {
            System.out.println("\n=== " + testClass.getSimpleName() + " ===");
            try {
                Object instance = testClass.getDeclaredConstructor().newInstance();

                for (Method method : testClass.getDeclaredMethods()) {
                    if (method.getName().startsWith("test") && method.getParameterCount() == 0) {
                        try {
                            method.invoke(instance);
                            System.out.println("  PASS: " + method.getName());
                            passed++;
                        } catch (Exception e) {
                            Throwable cause = e.getCause() != null ? e.getCause() : e;
                            String msg = testClass.getSimpleName() + "." + method.getName()
                                    + ": " + cause.getMessage();
                            System.out.println("  FAIL: " + method.getName() + " - " + cause.getMessage());
                            failures.add(msg);
                            failed++;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("  ERROR: Could not instantiate " + testClass.getSimpleName());
                failures.add("Could not instantiate " + testClass.getSimpleName());
                failed++;
            }
        }

        System.out.println("\n========================================");
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
        if (!failures.isEmpty()) {
            System.out.println("\nFailures:");
            for (String f : failures) {
                System.out.println("  - " + f);
            }
        }
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---- Assertion Helpers ----

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError("Expected: <" + expected + "> but was: <" + actual + ">");
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(message + " - Expected: <" + expected + "> but was: <" + actual + ">");
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true");
        }
    }

    public static void assertFalse(String message, boolean condition) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertNull(Object obj) {
        if (obj != null) {
            throw new AssertionError("Expected null but was: <" + obj + ">");
        }
    }

    public static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected non-null but was null");
        }
    }

    public static void assertNotNull(String message, Object obj) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }

    /**
     * Custom assertion error that works without JUnit.
     */
    public static class AssertionError extends RuntimeException {
        public AssertionError(String message) {
            super(message);
        }
    }
}
