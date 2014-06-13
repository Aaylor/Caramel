package formats;

import org.junit.Test;

import static org.junit.Assert.*;

public class PortTest {
    @Test
    public void testGoodSyntax() throws Exception {
        System.out.println("Port.testGoodSyntax...");
        new Port(12345);
        new Port(1);
        new Port("00001");
        new Port("1");
        new Port("11111");
    }

    @Test (expected = WrongFormatException.class)
    public void testBadSyntax() throws Exception {
        System.out.println("Port.testBadSyntax...");
        new Port("a");
    }

    @Test (expected = WrongFormatException.class)
    public void testWrongSizeSyntax() throws Exception {
        System.out.println("Port.testWrongSizeSyntax...");
        new Port(111111);
    }

    @Test
    public void testEqualityCompare() throws Exception {
        System.out.println("Port.testEqualityCompare...");
        Port p1 = new Port("01010");
        Port p2 = new Port("1010");

        assertEquals(p1.compare(p2), 0);
    }

    @Test
    public void testLessCompare() throws Exception {
        System.out.println("Port.testLessCompare...");
        Port p1 = new Port(0);
        Port p2 = new Port("1");

        assertTrue(p1.compare(p2) < 0);
    }

    @Test
    public void testMoreCompare() throws Exception {
        System.out.println("Port.testMoreCompare...");
        Port p1 = new Port(1000);
        Port p2 = new Port("123");

        assertTrue(p1.compare(p2) > 0);
    }

    @Test
    public void testToString() throws Exception {
        System.out.println("Port.testToString...");
        Port p1 = new Port(100);

        assertEquals(p1.toString(), "00100");
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("Port.testEquals...");
        Port p1 = new Port(345);
        Port p2 = new Port("345");

        assertEquals(p1, p2);
    }

    @Test
    public void testNotEquals() throws Exception {
        System.out.println("Port.testEquals...");
        Port p1 = new Port(18934);
        Port p2 = new Port("1223");

        assertNotEquals(p1, p2);
    }
}