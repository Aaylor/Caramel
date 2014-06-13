package formats;

import org.junit.Test;

import static org.junit.Assert.*;

public class MachineTest {
    @Test
    public void testGoodSyntax() throws Exception {
        System.out.println("Machine.testGoodSyntax...");
        new Machine("192.06.0.100");
    }

    @Test (expected = WrongFormatException.class)
    public void testBadNumberSyntax() throws Exception {
        System.out.println("Machine.testBadNumberSyntax...");
        new Machine("256.00.00.0");
    }

    @Test (expected = WrongFormatException.class)
    public void testBadFormat() throws Exception {
        System.out.println("Machine.testBadFormat...");
        new Machine("1111.00.000.111");
    }

    @Test
    public void testCompare() throws Exception {
        System.out.println("Machine.testCompare...");

        Machine m1  = new Machine("192.168.0.001");
        Machine m2  = new Machine("192.168.000.002");
        assertTrue(m1.compare(m2)  < 0);

        Machine m3  = new Machine("191.154.010.255");
        Machine m4  = new Machine("192.168.000.000");
        assertTrue(m3.compare(m4)  < 0);

        Machine m5  = new Machine("192.168.000.100");
        Machine m6  = new Machine("192.168.0.100");
        assertEquals(m5.compare(m6), 0);

        Machine m7  = new Machine("099.000.100.001");
        Machine m8  = new Machine("090.255.255.255");
        assertTrue(m7.compare(m8)  > 0);

        Machine m9  = new Machine("154.200.200.200");
        Machine m10 = new Machine("154.200.200.199");
        assertTrue(m9.compare(m10) > 0);
    }

    @Test
    public void testToString() throws Exception {
        System.out.println("Machine.testToString...");

        Machine m1 = new Machine("192.68.0.1");

        assertEquals(m1.toString().length(), "192.068.000.001".length());
        assertEquals(m1.toString(), "192.068.000.001");
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("Machine.testEquals...");

        Machine m1 = new Machine("9.17.100.0");
        Machine m2 = new Machine("009.017.100.000");

        assertEquals(m1, m2);
    }
}