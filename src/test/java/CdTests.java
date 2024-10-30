import org.junit.jupiter.api.Test;
import org.os.JavaShell;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class CdTests {
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
        String testDir="C:\\Users\\Hanaa\\Desktop";
        String tmp=shell.changeDirectory(testDir).toString();

        assertEquals(tmp, testDir,"Current directory should change to testDir");
    }
    @Test
    public void testInvalidDirectory() throws IOException {
        JavaShell shell=new JavaShell();
        File test=shell.changeDirectory("wqlkwoqk");
        assertEquals(test.toString(),shell.PWD(),"The Directory Exists");
    }
}
