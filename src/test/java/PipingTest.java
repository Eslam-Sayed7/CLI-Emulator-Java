    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.os.JavaShell;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.Arrays;

    import static org.junit.jupiter.api.Assertions.*;


    public class PipingTest {

        String[] TEST_FILES = {"1.txt" , "2.txt" , "AB.txt" , "AC.txt","a.txt" , "b.txt"};
        String TEST_DIR  = "testing_pip_output";
        private Path testFilePath;
        int NumberoftestFiles = 6;
        JavaShell shell;


        @BeforeEach
        void setUp() throws IOException {
            Files.createDirectory(Paths.get(TEST_DIR));
            for(int i = 0; i< NumberoftestFiles; i++){
                testFilePath = Paths.get(TEST_DIR, TEST_FILES[i]);
                Files.createFile(testFilePath);
            }
        }

        @AfterEach
        void tearDown() throws IOException {
            for(int i = 0; i< NumberoftestFiles; i++){
                testFilePath = Paths.get(TEST_DIR, TEST_FILES[i]);
                Files.deleteIfExists(testFilePath);
            }
            Files.deleteIfExists(Paths.get(TEST_DIR));
        }

        @Test
        void PipCurrentFilesAndReloadAndCheckIfSortedAsc() throws IOException, InterruptedException {
            // Command: ls | sort   would sort them in ascending order
            String cmd = "ls | sort";
            shell = new JavaShell();
            shell.CD(TEST_DIR);
            String output = shell.handlePiping(cmd);
            String[] sortedFiles = output.split("\n");
            Arrays.sort(TEST_FILES);
            assertArrayEquals(TEST_FILES, sortedFiles);
        }

    }
