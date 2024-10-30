import org.junit.jupiter.api.Test;
import org.os.JavaShell;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class mkdirAndPwdTests {

    @Test
    public void mkdirTest() throws IOException {
        JavaShell shell=new JavaShell();
        File testDirectory= new File(shell.PWD());
        if(testDirectory.exists()){
            testDirectory.delete();
        }
        shell.mkdirCommand(new String[]{"mkdir","Hello"});

        assertTrue("Directory Should Be Created",testDirectory.exists());
    }
    @Test
    public void mkdirMakeMultipleDirectories() throws IOException {
        JavaShell shell=new JavaShell();
        File testDirectory= new File(shell.PWD());
        if(testDirectory.exists()){
            testDirectory.delete();
        }
        String[]tmp={"mkdir","Hello","Bye","TabanMenElkolya"};
        shell.mkdirCommand(tmp);

        assertTrue("Directory Should Be Created",testDirectory.exists());
    }
    @Test
    public void currentDirectoryPwd() throws IOException {
        JavaShell shell=new JavaShell();
        File testDirectory= new File(shell.PWD());
        assertEquals("Initial directory should match", testDirectory.toString(), new File(System.getProperty("user.dir")).getAbsolutePath());

    }
    @Test
    public void EnsureParentDirectory() throws IOException {
        JavaShell shell=new JavaShell();
        File intialDirectory= new File(shell.PWD());
        String parent=intialDirectory.getParent();
        assertEquals("The Two Should be Equals",parent,intialDirectory.getParent());
    }
}
