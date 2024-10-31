import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RmdirTest {

    private static final String TEST_DIR = "test_dir";
    private Path testDirPath;

    @BeforeEach
    void setUp() throws IOException {
        testDirPath = Paths.get(TEST_DIR);
        Files.createDirectory(testDirPath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testDirPath);
    }

    @Test
    void testRmdir() throws IOException {
        JavaShell shell = new JavaShell();
        assertTrue(Files.exists(testDirPath));

        // Execute command
        shell.rmdir(TEST_DIR);

        // verify
        assertFalse(Files.exists(testDirPath));
    }
}
