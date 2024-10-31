import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RmTest {

    private static final String TEST_DIR = "test_dir";
    private static final String TEST_FILE = "test_file.txt";
    private Path testFilePath;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectory(Paths.get(TEST_DIR));
        testFilePath = Paths.get(TEST_DIR, TEST_FILE);
        Files.createFile(testFilePath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
        Files.deleteIfExists(Paths.get(TEST_DIR));
    }

    @Test
    void testRm() throws IOException {
        JavaShell shell = new JavaShell();
        assertTrue(Files.exists(testFilePath));

        // Execute rm command
        shell.rm(TEST_FILE);

        // Verify file is deleted
        assertFalse(Files.exists(testFilePath));
    }
}
