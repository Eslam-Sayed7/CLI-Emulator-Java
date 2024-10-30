import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MvTest {

    private static final String TEST_DIR = "test_dir";
    private static final String TEST_FILE = "test_file.txt";
    private static final String NEW_FILE = "new_file.txt";
    private Path testFilePath;
    private Path newFilePath;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectory(Paths.get(TEST_DIR));
        testFilePath = Paths.get(TEST_DIR, TEST_FILE);
        newFilePath = Paths.get(TEST_DIR, NEW_FILE);
        Files.createFile(testFilePath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
        Files.deleteIfExists(newFilePath);
        Files.deleteIfExists(Paths.get(TEST_DIR));
    }

    @Test
    void testMv() throws IOException {
        JavaShell shell = new JavaShell();
        assertTrue(Files.exists(testFilePath));
        assertFalse(Files.exists(newFilePath));

        // Execute the command
        shell.mv(TEST_FILE, NEW_FILE);

        // verify it was renamed
        assertFalse(Files.exists(testFilePath));
        assertTrue(Files.exists(newFilePath));
    }
}
