import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.Example;
import org.os.JavaShell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


public class PipingTest {
    String TEST_FILE = "testing_pip_output.txt";

            @BeforeEach
            void setUp() throws IOException {
                // Create an empty test file
                Files.createFile(Paths.get(TEST_FILE));
            }

            @AfterEach
            void tearDown() throws IOException {
                // Clean up the test file after the test completes
                Files.deleteIfExists(Paths.get(TEST_FILE));
            }

            @Test
            void makeFileEmptyAndListToITAndCheckFileNotEmpty() throws IOException {
                // Command to write content to the test file
                // command : cat source.txt > destination.txt

                String cmd1 = "cat";
                // Command to read from the test file (simulates piping logic)
                String cmd2 = ">>" + TEST_FILE;

                // Call the piping function
                JavaShell shell = new JavaShell();
                shell.handlePiping(cmd1, cmd2);
                // Verify the file is not empty after processing
                Path path = Paths.get(TEST_FILE);
                assertTrue(Files.size(path) > 0, "File should not be empty after piping");
            }
    }
