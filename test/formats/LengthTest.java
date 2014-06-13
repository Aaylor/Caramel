package formats;

import org.junit.Test;

import static org.junit.Assert.*;

public class LengthTest {
    @Test
    public void testGoodSyntax() throws Exception {
        System.out.println("Length.testGoodSyntax...");
        new Length("123");
        new Length("001");
        new Length(1);
        new Length(499);
    }

    @Test (expected = WrongFormatException.class)
    public void testBadStringSyntax() throws Exception {
        System.out.println("Length.testBadStringSyntax");
        new Length("a");
    }

    @Test (expected = WrongFormatException.class)
    public void testBadSizeSyntax() throws Exception {
        System.out.println("Length.testBadSizeSyntax...");

        new Length(1111);
    }

    @Test
    public void testToString() throws Exception {
        System.out.println("Length.testToString...");
        Length l = new Length(11);

        assertEquals(l.toString().length(), 3);
        assertEquals(l.toString(), "011");
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("Length.testEquals...");
        Length l1 = new Length("1");
        Length l2 = new Length(1);

        assertEquals(l1, l2);
        assertEquals(l2, l1);
    }

    @Test
    public void testNotEquals() throws Exception {
        System.out.println("Length.testNotEquals...");
        Length l1 = new Length(11);
        Length l2 = new Length("456");

        assertNotEquals(l1, l2);
        assertNotEquals(l2, l1);
    }
}