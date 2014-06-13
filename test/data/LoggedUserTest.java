package data;

import formats.Machine;
import formats.Port;
import formats.User;
import formats.WrongFormatException;
import org.junit.Test;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class LoggedUserTest {

    @Test
    public void testEquals() throws Exception {
        System.out.println("LoggedUser.testEquals...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("210.134.1.13"), new Port(234)
        );

        LoggedUser lu2 = new LoggedUser(
                new User("test"), new Machine("192.168.000.01"),
                new Machine("210.134.001.013"), new Port("234")
        );

        LoggedUser lu3 = new LoggedUser(
                new User("test2"), new Machine("100.168.0.1"),
                new Machine("220.114.1.13"), new Port(10)
        );

        assertEquals(lu1, lu2);
        assertNotEquals(lu1, lu3);
        assertNotEquals(lu2, lu3);
    }

    @Test
    public void testCompareTo() throws Exception {
        System.out.println("LoggedUser.testCompareTo...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("210.134.1.13"), new Port(234)
        );

        LoggedUser lu2 = new LoggedUser(
                new User("test"), new Machine("192.168.000.01"),
                new Machine("210.134.001.013"), new Port("234")
        );


        LoggedUser lu4 = new LoggedUser(
                new User("uuu"), new Machine("100.168.0.1"),
                new Machine("220.114.1.13"), new Port(10)
        );

        LoggedUser lu5 = new LoggedUser(
                new User("zzz"), new Machine("100.168.0.1"),
                new Machine("220.114.1.13"), new Port(10)
        );

        assertEquals(lu1.compareTo(lu2), 0);
        assertTrue(lu1.compareTo(lu4) < 0);
        assertTrue(lu5.compareTo(lu4) > 0);
    }

    @Test
    public void testCompareToByUserAddress() throws Exception {
        System.out.println("LoggedUser.testCompareToByUserAddress...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("100.1.1.13"), new Port(234)
        );

        LoggedUser lu2 = new LoggedUser(
                new User("test"), new Machine("192.168.000.01"),
                new Machine("11.23.001.013"), new Port("234")
        );


        LoggedUser lu4 = new LoggedUser(
                new User("uuu"), new Machine("100.168.0.1"),
                new Machine("210.134.1.13"), new Port(10)
        );

        LoggedUser lu5 = new LoggedUser(
                new User("uuu"), new Machine("100.168.0.1"),
                new Machine("210.134.1.13"), new Port(10)
        );

        assertTrue(lu1.compareToByUserAddress(lu2) > 0);
        assertTrue(lu1.compareToByUserAddress(lu4) < 0);
        assertEquals(lu5.compareToByUserAddress(lu4), 0);
    }

    @Test
    public void testCompareToByPort() throws Exception {
        System.out.println("LoggedUser.testCompareToByPort...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("100.1.1.13"), new Port(234)
        );

        LoggedUser lu2 = new LoggedUser(
                new User("test"), new Machine("192.168.000.01"),
                new Machine("100.1.1.013"), new Port("234")
        );


        LoggedUser lu4 = new LoggedUser(
                new User("uuu"), new Machine("100.168.0.1"),
                new Machine("210.134.1.13"), new Port(10)
        );

        LoggedUser lu5 = new LoggedUser(
                new User("uuu"), new Machine("100.168.0.1"),
                new Machine("210.134.1.13"), new Port(1234)
        );

        assertEquals(lu1.compareToByPort(lu2), 0);
        assertTrue(lu1.compareToByPort(lu4) > 0);
        assertTrue(lu4.compareToByPort(lu5) < 0);
    }

    @Test
    public void testIsSender() throws Exception {
        System.out.println("LoggedUser.testIsSender...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("100.1.1.13"), new Port(234)
        );

        assertTrue(lu1.isSender(new Machine("192.168.0.1")));
        assertFalse(lu1.isSender(new Machine("1.1.1.1")));
    }

    @Test
    public void testIsSender1() throws Exception {
        System.out.println("LoggedUser.testIsSender1...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("100.1.1.13"), new Port(234)
        );

        assertTrue(lu1.isSender(new User("test"), new Machine("192.168.0.1")));
        assertFalse(lu1.isSender(new User("user"), new Machine("192.168.0.1")));
        assertFalse(lu1.isSender(new User("test"), new Machine("192.100.0.0")));
        assertFalse(lu1.isSender(new User("user"), new Machine("192.100.0.0")));

    }

    @Test
    public void testIsSender2() throws Exception {
        System.out.println("LoggedUser.testIsSender2...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("100.1.1.13"), new Port(234)
        );

        assertTrue(lu1.isSender(
                new LoggedUser(
                    new User("test"), new Machine("192.168.0.1"),
                    new Machine("1.1.1.1"), new Port(100))));
        assertFalse(lu1.isSender(
                new LoggedUser(
                    new User("user"), new Machine("192.168.0.1"),
                    new Machine("1.1.1.1"), new Port(100))));
        assertFalse(lu1.isSender(
                new LoggedUser(
                    new User("test"), new Machine("192.100.0.0"),
                    new Machine("1.1.1.1"), new Port(100))));
        assertFalse(lu1.isSender(
                new LoggedUser(
                    new User("user"), new Machine("192.100.0.0"),
                    new Machine("1.1.1.1"), new Port(100))));
    }

    @Test
    public void testTestUserByUserAndAddress() throws Exception {
        System.out.println("LoggedUser.testTestUserByUserAndAddress...");

        LoggedUser lu1 = new LoggedUser(
                new User("test"), new Machine("192.168.0.1"),
                new Machine("100.1.1.13"), new Port(234)
        );

        assertTrue(lu1.testUserByUserAndAddress(
                new User("test"), new Machine("100.001.001.013")
        ));
        assertFalse(lu1.testUserByUserAndAddress(
                new User("user"), new Machine("100.001.001.013")
        ));
        assertFalse(lu1.testUserByUserAndAddress(
                new User("test"), new Machine("1.1.1.1")
        ));
        assertFalse(lu1.testUserByUserAndAddress(
                new User("user"), new Machine("1.1.1.1")
        ));
    }
}