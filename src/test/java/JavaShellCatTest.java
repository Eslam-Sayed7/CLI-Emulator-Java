import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaShellCatTest {

    private static final String TEST_FILE = "sample.txt";
    private static final String FILE_CONTENT = "This is a sample file for testing.";

    private Path projectRoot;
    private JavaShell shell;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        projectRoot = Paths.get("").toAbsolutePath();

        // Create the sample file in the current directory
        Files.write(projectRoot.resolve(TEST_FILE), FILE_CONTENT.getBytes());

        // Initialize JavaShell instance
        shell = new JavaShell();

        // Redirect System.out to capture output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete the sample file created in the project root directory
        Files.deleteIfExists(projectRoot.resolve(TEST_FILE));

        // Reset System.out to its original state
        System.setOut(System.out);
    }

    @Test
    void testCatFileExistsInCurrentDirectory() {
        // Run the 'cat' command on the sample file
        shell.Cat(TEST_FILE);

        // Verify that the content matches the expected content
        assertEquals(FILE_CONTENT + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testCatFileDoesNotExistInCurrentDirectory() {
        // Run the 'cat' command on a non-existent file
        String nonExistentFile = "non_existent_file.txt";
        shell.Cat(nonExistentFile);

        // Verify output for a non-existent file
        assertEquals("cat: " + nonExistentFile + ": No such file" + System.lineSeparator(), outputStream.toString());
    }
}
