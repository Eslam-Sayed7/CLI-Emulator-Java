import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.stream.Collectors;

public class JavaShellLSTest {

    private static final String TEST_FILE1 = "file1.txt";
    private static final String TEST_FILE2 = "file2.txt";
    private static final String HIDDEN_FILE = ".hiddenfile"; // Adjust this if needed
    private Path projectRoot;
    private JavaShell shell;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        projectRoot = Paths.get("").toAbsolutePath();

        // Create sample files
        Files.createFile(projectRoot.resolve(TEST_FILE1));
        Files.createFile(projectRoot.resolve(TEST_FILE2));
        Files.createFile(projectRoot.resolve(HIDDEN_FILE));

        // Ensure the hidden file is recognized as hidden on Windows
        setHiddenAttribute(projectRoot.resolve(HIDDEN_FILE));

        // Initialize JavaShell instance
        shell = new JavaShell();

        // Redirect System.out to capture output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete the sample files created in the project root directory
        Files.deleteIfExists(projectRoot.resolve(TEST_FILE1));
        Files.deleteIfExists(projectRoot.resolve(TEST_FILE2));
        Files.deleteIfExists(projectRoot.resolve(HIDDEN_FILE));

        // Reset System.out to its original state
        System.setOut(System.out);
    }

    private void setHiddenAttribute(Path file) {
        // Set the file attribute to hidden if on Windows
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                Process process = new ProcessBuilder("attrib", "+h", file.toString()).start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testLSWithoutShowAllAndRegularOrder() {
        // Run 'ls' (showAll = false, reverseOrder = false)
        shell.STDprintFunctionOutput(() -> shell.LS(false, 0));

        // Capture actual output and verify expected files are present in regular order
        List<String> actualOutput = outputStream.toString().lines().collect(Collectors.toList());

        System.out.print(actualOutput);

        assertTrue(actualOutput.contains(TEST_FILE1), "Output should contain file1.txt");
        assertTrue(actualOutput.contains(TEST_FILE2), "Output should contain file2.txt");
        assertTrue(!actualOutput.contains(HIDDEN_FILE), "Output should not contain hidden files when showAll is false");
    }

    @Test
    void testLSWithShowAll() {
        // Run 'ls -a' (showAll = true, reverseOrder = false)
        shell.STDprintFunctionOutput(() -> shell.LS(true, 0));

        // Capture actual output and verify expected files are present in order
        List<String> actualOutput = outputStream.toString().lines().collect(Collectors.toList());

        assertTrue(actualOutput.contains(HIDDEN_FILE), "Output should contain hidden file");
        assertTrue(actualOutput.contains(TEST_FILE1), "Output should contain file1.txt");
        assertTrue(actualOutput.contains(TEST_FILE2), "Output should contain file2.txt");
    }

    @Test
    void testLSWithReverseOrder() {
        // Run 'ls -r' (showAll = false, reverseOrder = true)
        shell.STDprintFunctionOutput(() -> shell.LS(false, 1));

        // Capture actual output and verify expected files are present in reverse order
        List<String> actualOutput = outputStream.toString().lines().collect(Collectors.toList());

        // Verify the expected order (only non-hidden files)
        int index1 = actualOutput.indexOf(TEST_FILE2);
        int index2 = actualOutput.indexOf(TEST_FILE1);
        assertTrue(index1 < index2, "file2.txt should appear before file1.txt in reverse order");
    }

    @Test
    void testLSWithShowAllAndReverseOrder() {
        // Run 'ls -a -r' (showAll = true, reverseOrder = true)
        shell.STDprintFunctionOutput(() -> shell.LS(true, 1));

        // Capture actual output and verify expected files are present in reverse order
        List<String> actualOutput = outputStream.toString().lines().collect(Collectors.toList());

        // Verify the expected order (all files including hidden)
        int index1 = actualOutput.indexOf(TEST_FILE2);
        int index2 = actualOutput.indexOf(TEST_FILE1);
        int indexHidden = actualOutput.indexOf(HIDDEN_FILE);
        assertTrue(index1 < index2 && index2 < indexHidden, "Files should be in reverse order with hidden file last");
    }
}
