package formats;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;



public class FileLengthTest {
    FileLength fl1 = null;
    FileLength fl2 = null;
    FileLength fl3 = null;
    FileLength fl4 = null;
    FileLength fl5 = null;
    FileLength fl6 = null;

    @Before
    public void setUp() throws Exception {
        fl1 = new FileLength(100);
        fl2 = new FileLength("100");
        fl3 = new FileLength(254);
        fl4 = new FileLength("254");
        fl5 = new FileLength(1234);
        fl6 = new FileLength(99999999);
    }

    @Test(expected = WrongFormatException.class)
    public void testFalse() throws Exception {
        System.out.println("FilenameLength.testFalse...");
        new FileLength(-1);
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("FilenameLength.testEquals...");
        assertEquals(fl1, fl2);
        assertEquals(fl3, fl4);
        assertNotEquals(fl1, fl3);
        assertNotEquals(fl3, fl5);
        assertNotEquals(fl1, fl6);
    }

    @Test
    public void testToInteger() throws Exception {
        System.out.println("FilenameLength.testToInteger...");
        assertEquals(fl1.toInteger(), 100);
        assertEquals(fl2.toInteger(), 100);
        assertEquals(fl6.toInteger(), 99999999);
        assertEquals(fl5.toInteger(), 1234);
    }
}