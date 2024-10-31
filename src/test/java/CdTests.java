import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CdTests {

    private static final String TEST_DIR = "testing_subdirectory_change";
    private Path testFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        // Create the test directory if it doesn't exist
        testFilePath = Paths.get(TEST_DIR);
        File directory = testFilePath.toFile();
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the test directory after each test
        File directory = testFilePath.toFile();
        if (directory.exists()) {
            directory.delete();
        }
    }

    @Test
    public void testCdParentDirectoryExists() throws IOException {
        JavaShell shell = new JavaShell();
        String initialDirectory = shell.PWD();
        File initialDirFile = new File(initialDirectory);

        String parentPath = initialDirFile.getParent();
        if (parentPath == null) {
            fail("No parent directory exists for the current directory.");
        }

        shell.CD("..");
        String currentDirectory = shell.PWD();
        assertEquals(parentPath, currentDirectory, "The current directory should be the parent directory.");
    }

    @Test
    public void testChangeToValidSubdirectory() throws IOException {
        JavaShell shell = new JavaShell();
        File resultDir = shell.changeDirectory(TEST_DIR); // changeDirectory should return File, not Path

        assertEquals(testFilePath.toFile().getAbsolutePath(), resultDir.getAbsolutePath(),
                "Current directory should change to TEST_DIR");
    }

    @Test
    public void testInvalidDirectory() throws IOException {
        JavaShell shell = new JavaShell();
        String nonExistentDir = "wqlkwoqk";
        File test = shell.changeDirectory(nonExistentDir);

        // Assuming PWD() returns the current directory as a string
        assertEquals(shell.PWD(), test.toString(), "The directory should not exist.");
    }
}
