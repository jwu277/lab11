package formula;

import static org.junit.Assert.*;
import org.junit.Test;

public class FormulaFactoryTest {

    @Test
    public void testLiterals() {
        assertTrue(FormulaFactory.parse("T").evaluate());
        assertFalse(FormulaFactory.parse("   F    ").evaluate());
    }

    @Test
    public void testAnd() {
        assertTrue(FormulaFactory.parse("T &T").evaluate());
        assertFalse(FormulaFactory.parse("T & T & F").evaluate());
    }

    @Test
    public void testOtherLiterals() {
        assertFalse(FormulaFactory.parse("T & true & false & true").evaluate());
    }

    @Test
    public void testNot() {
        assertFalse(FormulaFactory.parse("!T").evaluate());
        assertTrue(FormulaFactory.parse("!F").evaluate());
    }

}
