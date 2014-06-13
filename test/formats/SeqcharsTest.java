package formats;

import org.junit.Test;

import static org.junit.Assert.*;

public class SeqcharsTest {

    @Test
    public void testGoodSyntax() throws Exception {
        System.out.println("Seqchars.testGoodSytax...");
        String s = "adbbqsdq$$*ncnv,::!:*£1+";

        new Seqchars(s);
    }

    @Test (expected = WrongFormatException.class)
    public void testBadSyntax() throws Exception {
        System.out.println("Seqchars.testBadSyntax...");

        new Seqchars("♥");
    }

    @Test
    public void testToString() throws Exception {
        System.out.println("Seqchars.testToString...");
        String s = "qsdiqsd dqd sq d:q d!qQ: ;d*qsd 12°90£  1£2M/ 1/dqsd qù 2";
        Seqchars seq = new Seqchars(s);

        assertEquals(s.length(), seq.toString().length());
        assertEquals(seq.toString(), s);
    }
}