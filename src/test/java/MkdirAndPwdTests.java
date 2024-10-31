import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.os.JavaShell;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MkdirAndPwdTests {

    private JavaShell shell;
    private File testDirectory;

    @BeforeEach
    public void setUp() throws IOException {
        shell = new JavaShell();
        testDirectory = new File(shell.PWD());
    }

    @AfterEach
    public void tearDown() {
        // Delete any created directories to clean up after each test
        File[] directories = new File[]{new File(testDirectory, "Hello"),
                new File(testDirectory, "Bye"),
                new File(testDirectory, "SeeYouLater")};
        for (File dir : directories) {
            if (dir.exists()) {
                dir.delete();
            }
        }
    }

    @Test
    public void mkdirTest() throws IOException {
        shell.mkdirCommand(new String[]{"mkdir", "Hello"});

        File helloDir = new File(testDirectory, "Hello");
        assertTrue(helloDir.exists(), "Directory should be created.");
    }

    @Test
    public void mkdirMakeMultipleDirectories() throws IOException {
        String[] directories = {"mkdir", "Hello", "Bye", "SeeYouLater"};
        shell.mkdirCommand(directories);

        assertTrue(new File(testDirectory, "Hello").exists(), "Directory 'Hello' should be created.");
        assertTrue(new File(testDirectory, "Bye").exists(), "Directory 'Bye' should be created.");
        assertTrue(new File(testDirectory, "SeeYouLater").exists(), "Directory 'SeeYouLater' should be created.");
    }

    @Test
    public void currentDirectoryPwd() throws IOException {
        assertEquals(new File(System.getProperty("user.dir")).getAbsolutePath(),
                testDirectory.getAbsolutePath(),
                "Initial directory should match the user's current directory.");
    }

    @Test
    public void ensureParentDirectory() throws IOException {
        File initialDirectory = new File(shell.PWD());
        String parentPath = initialDirectory.getParent();
        assertEquals(parentPath, initialDirectory.getParent(), "Parent directory should match.");
    }
}
