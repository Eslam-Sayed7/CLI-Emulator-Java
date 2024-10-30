import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedirectingTest {

    private static final String TEST_DIR = "test_dir";  // Directory inside the project root
    private static final String OVERRIDE_FILE = "override_output.txt";
    private static final String APPEND_FILE = "append_output.txt";

    private Path projectRoot;

    @BeforeEach
    void setUp() throws IOException {
        // Get the project root directory (current working directory)
        projectRoot = Paths.get("").toAbsolutePath();

        // Create the test directory inside the project root
        Files.createDirectory(projectRoot.resolve(TEST_DIR));

        // Create sample files in the test directory
        Files.createFile(projectRoot.resolve(TEST_DIR).resolve("sample1.txt"));
        Files.createFile(projectRoot.resolve(TEST_DIR).resolve("sample2.txt"));

        // Ensure output files do not exist before tests
        Files.deleteIfExists(projectRoot.resolve(OVERRIDE_FILE));
        Files.deleteIfExists(projectRoot.resolve(APPEND_FILE));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete all files inside the test directory
        Files.walk(projectRoot.resolve(TEST_DIR))
                .map(Path::toFile)
                .forEach(File::delete);

        // Delete the test directory itself and output files
        Files.deleteIfExists(projectRoot.resolve(TEST_DIR));
        Files.deleteIfExists(projectRoot.resolve(OVERRIDE_FILE));
        Files.deleteIfExists(projectRoot.resolve(APPEND_FILE));
    }

    @Test
    void testRedirectingWithOverriding() throws IOException {
        // Run the method with ls command targeting the test directory
        String cmd = "ls " + projectRoot.resolve(TEST_DIR);
//        String cmd = "ls";
        JavaShell shell = new JavaShell();
        shell.RedirectingWithOverriding(cmd, projectRoot.resolve(OVERRIDE_FILE).toString());
//        RedirectingWithOverriding(cmd, projectRoot.resolve(OVERRIDE_FILE).toString());

        // Read the contents of the overridden file
        List<String> output = Files.readAllLines(projectRoot.resolve(OVERRIDE_FILE));

        // Verify that the file contains the correct output
        assertEquals(2, output.size());
        assertTrue(output.contains("sample1.txt"));
        assertTrue(output.contains("sample2.txt"));
    }

    @Test
    void testRedirectingWithAppending() throws IOException {
        // Run the method twice to append the output to the same file
        String cmd = "ls " + projectRoot.resolve(TEST_DIR);
//        String cmd = "ls";
        JavaShell shell = new JavaShell();
        shell.RedirectingWithAppending(cmd, projectRoot.resolve(APPEND_FILE).toString());
        shell.RedirectingWithAppending(cmd, projectRoot.resolve(APPEND_FILE).toString());

//        RedirectingWithAppending(cmd, projectRoot.resolve(APPEND_FILE).toString());
//        RedirectingWithAppending(cmd, projectRoot.resolve(APPEND_FILE).toString());

        // Read the contents of the appended file
        List<String> output = Files.readAllLines(projectRoot.resolve(APPEND_FILE));

        // Verify that the file contains the correct output twice
        assertEquals(4, output.size());
        assertTrue(output.contains("sample1.txt"));
        assertTrue(output.contains("sample2.txt"));
    }

}
