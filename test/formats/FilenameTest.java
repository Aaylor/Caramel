package formats;

import org.junit.Test;

import static org.junit.Assert.*;

public class FilenameTest {

    @Test
    public void testGoodFormat() throws Exception {
        System.out.println("Filename.testGoodFormat...");
        String s = "";

        for (char c = 'a'; c <= 'z'; c++) {
            s += c;
        }

        s += "...";

        new Filename(s);
    }

    @Test(expected = WrongFormatException.class)
    public void testWrongFormatBadCharacters() throws Exception {
        System.out.println("Filename.testWrongFormatBadCharacters...");

        new Filename("abc.txt-_");
    }

    @Test(expected = WrongFormatException.class)
    public void testWrongFormatWrongSize() throws Exception {
        System.out.println("Filename.testWrongFormatWrongSize...");

        String s = "";
        for (int i = 0; i < 100; i++) {
            s += "a";
        }

        new Filename(s);
    }

    @Test
    public void testEqualsToItself() throws Exception {
        System.out.println("Filename.testEqualsToItself...");
        Filename f1 = new Filename("filename.txt");

        assertEquals(f1, f1);
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("Filename.testEquals...");
        Filename f1 = new Filename("filename.txt");
        Filename f2 = new Filename("filename.txt");

        assertEquals(f1, f2);
        assertEquals(f2, f1);
    }

    @Test
    public void testToString() throws Exception {
        System.out.println("Filename.testToString...")  ;
        Filename f = new Filename("filename.txt");

        String s = "filename.txt";
        for (int i = s.length(); i < 40; i++) s += (char)32;

        assertEquals(f.toString().length(), s.length());
        assertEquals(f.toString(), s);
    }

    @Test
    public void testTrimmedFilename() throws Exception {
        System.out.println("Filename.testTrimmedFilename...");
        Filename f = new Filename("filename.txt");

        assertEquals(f.toString().length(), 40);
        assertEquals(f.trimmedFilename(), "filename.txt");
        assertEquals(f.trimmedFilename().length(), "filename.txt".length());
    }
}
