import org.junit.jupiter.api.Test;
import org.os.Example;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class ExampleTest {

    @Test
    void threePlusOneFour() {
       var example = new Example();

       int result = example.Add(3,1);

       assertEquals(4,result);
    }


    @Test
    void twoPlusTwoFour() {
        var example = new Example();

        int result = example.Add(2,2);

        assertEquals(4,result);
    }


}
