package loa;

import ucb.junit.textui;
import org.junit.Test;
import static loa.BoardTest.*;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the loa package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(BoardTest.class, UnitTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
    }

}


