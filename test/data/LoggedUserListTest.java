package data;

import formats.Machine;
import formats.Port;
import formats.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class LoggedUserListTest {

    @Test
    public void testAddLoggedUser() throws Exception {
        System.out.println("LoggedUserList.testAddLoggedUser...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
            new LoggedUser(
                new User("user"), new Machine("1.1.1.1"),
                new Machine("2.2.2.2"), new Port("1111"))
        );

        assertEquals(l.size(), 1);

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.2"), new Port("1111"))
        );

        assertEquals(l.size(), 1);

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("33.33.33.33"),
                        new Machine("2.2.2.2"), new Port("1111"))
        );

        assertEquals(l.size(), 2);
    }

    @Test
    public void testGetLoggedUserByFromAddress() throws Exception {
        System.out.println("LoggedUserList.testGetLoggedUserByFromAddress...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.2"), new Port("1111"))
        );

        assertNotNull(l.getLoggedUserByFromAddress(new Machine("1.1.1.1")));
        assertNull(l.getLoggedUserByFromAddress(new Machine("2.2.2.2")));
    }

    @Test
    public void testGetLoggedUserByUserAndAddress() throws Exception {
        System.out.println("LoggedUserList.testGetLoggedUserByUserAndAddress...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.2"), new Port("1111"))
        );

        assertNotNull(l.getLoggedUserByUserAndAddress(
                new User("user"), new Machine("2.2.2.2")
        ));
        assertNull(l.getLoggedUserByUserAndAddress(
                new User("test"), new Machine("2.2.2.2")
        ));
        assertNull(l.getLoggedUserByUserAndAddress(
                new User("user"), new Machine("3.3.3.3")
        ));
        assertNull(l.getLoggedUserByUserAndAddress(
                new User("test"), new Machine("3.3.3.3")
        ));
    }

    @Test
    public void testDeleteLoggedUser() throws Exception {
        System.out.println("LoggedUserList.testDeleteLoggedUser...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.2"), new Port("1111"))
        );

        l.deleteLoggedUser(new LoggedUser(
                new User("test"), new Machine("1.1.1.1"),
                new Machine("2.2.2.2"), new Port(1111)
        ));

        assertEquals(l.size(), 1);

        l.deleteLoggedUser(new LoggedUser(
                new User("user"), new Machine("1.1.1.1"),
                new Machine("2.2.2.2"), new Port(1111)
        ));

        assertEquals(l.size(), 0);
    }

    @Test
    public void testDeleteByUserAndAddress() throws Exception {
        System.out.println("LoggedUserList.testDeleteByUserAndAddress...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.2"), new Port("1111"))
        );

        l.deleteByUserAndAddress(new User("test"), new Machine("0.0.0.0"));
        assertEquals(l.size(), 1);

        l.deleteByUserAndAddress(new User("user"), new Machine("0.0.0.0"));
        assertEquals(l.size(), 1);

        l.deleteByUserAndAddress(new User("test"), new Machine("1.1.1.1"));
        assertEquals(l.size(), 1);

        l.deleteByUserAndAddress(new User("user"), new Machine("1.1.1.1"));
        assertEquals(l.size(), 0);
    }

    @Test
    public void testGetSortedLoggedUserListByName() throws Exception {
        System.out.println("LoggedUserList.testGetSortedLoggedUserListByName...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.4"),
                        new Machine("2.2.2.7"), new Port("1"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("aaa"), new Machine("1.1.1.2"),
                        new Machine("2.2.2.1"), new Port("11"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("zzz"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.30"), new Port("111"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("bbb"), new Machine("1.1.1.7"),
                        new Machine("2.2.2.1"), new Port("1111"))
        );

        ArrayList<LoggedUser> sortedList = l.getSortedLoggedUserListByName();
        Iterator<LoggedUser> i = sortedList.iterator();

        assertEquals(i.next().getUser(), new User("aaa"));
        assertEquals(i.next().getUser(), new User("bbb"));
        assertEquals(i.next().getUser(), new User("user"));
        assertEquals(i.next().getUser(), new User("zzz"));
    }

    @Test
    public void testGetSortedLoggedUserListByUserAddress() throws Exception {
        System.out.println("LoggedUserList.testGetSortedLoggedUserListByUserAddress...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.4"),
                        new Machine("2.2.2.1"), new Port("1"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("aaa"), new Machine("1.1.1.2"),
                        new Machine("2.2.2.20"), new Port("11"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("qqq"), new Machine("1.1.1.2"),
                        new Machine("2.2.2.3"), new Port("20"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("zzz"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.1"), new Port("111"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("bbb"), new Machine("1.1.1.7"),
                        new Machine("2.2.2.30"), new Port("1111"))
        );

        ArrayList<LoggedUser> sortedList = l.getSortedLoggedUserListByUserAddress();
        Iterator<LoggedUser> i = sortedList.iterator();

        assertEquals(i.next().getUser(), new User("user"));
        assertEquals(i.next().getUser(), new User("zzz"));
        assertEquals(i.next().getUser(), new User("qqq"));
        assertEquals(i.next().getUser(), new User("aaa"));
        assertEquals(i.next().getUser(), new User("bbb"));
    }

    @Test
    public void testGetSortedLoggedUserListByPort() throws Exception {
        System.out.println("LoggedUserList.testGetSortedLoggedUserListByPort...");
        LoggedUserList l = new LoggedUserList();

        l.addLoggedUser(
                new LoggedUser(
                        new User("user"), new Machine("1.1.1.4"),
                        new Machine("2.2.2.1"), new Port("1"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("aaa"), new Machine("1.1.1.2"),
                        new Machine("2.2.2.20"), new Port("123"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("qqq"), new Machine("1.1.1.2"),
                        new Machine("2.2.2.3"), new Port(1000))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("zzz"), new Machine("1.1.1.1"),
                        new Machine("2.2.2.1"), new Port("100"))
        );

        l.addLoggedUser(
                new LoggedUser(
                        new User("bbb"), new Machine("1.1.1.7"),
                        new Machine("2.2.2.30"), new Port("999"))
        );

        ArrayList<LoggedUser> sortedList = l.getSortedLoggedUserListByPort();
        Iterator<LoggedUser> i = sortedList.iterator();

        assertEquals(i.next().getUser(), new User("user"));
        assertEquals(i.next().getUser(), new User("zzz"));
        assertEquals(i.next().getUser(), new User("aaa"));
        assertEquals(i.next().getUser(), new User("bbb"));
        assertEquals(i.next().getUser(), new User("qqq"));
    }
}