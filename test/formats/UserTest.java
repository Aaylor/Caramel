package formats;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testGoodSyntax() throws Exception {
        System.out.println("User.testGoodSyntax...");

        new User("aaaa");
        new User("aaaaaaaa");
    }

    @Test (expected = WrongFormatException.class)
    public void testOversizedLength() throws Exception {
        System.out.println("User.testOversizedLength...");

        new User("aaaaaaaaa");
    }

    @Test (expected = WrongFormatException.class)
    public void testTooShortLength() throws Exception {
        System.out.println("User.testTooShortLength...");

        new User("");
    }

    @Test (expected = WrongFormatException.class)
    public void testNonAsciiCharacters() throws Exception {
        System.out.println("User.testNonAsciiCharacters...");

        new User("éééé");
    }

    @Test
    public void testToString() throws Exception {
        System.out.println("User.testToString...");

        User u1   = new User("user");
        String s1 = "user    ";
        assertEquals(u1.toString().length(), s1.length());
        assertEquals(u1.toString(), s1);

        User u2   = new User("useruser");
        String s2 = "useruser";
        assertEquals(u2.toString().length(), s2.length());
        assertEquals(u2.toString(), s2);
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("User.testEquals...");

        User u1 = new User("aaylor");
        User u2 = new User("aaylor  ");

        assertEquals(u1, u2);
    }
}