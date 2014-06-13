package formats;

import java.util.Arrays;

/**
 * Format de la taille d'un Seqchars
 */
public class Length {
    private final char[] length = new char[3];

    /**
     * Create the length with an Integer.
     * @param length the length.
     * @throws WrongFormatException if doesn't correspond to the format.
     */
    public Length(int length) throws WrongFormatException {
        parse(length);
    }

    /**
     * Create the length with a String.
     * @param length the length.
     * @throws WrongFormatException if doesn't correspond to the format.
     */
    public Length(String length) throws WrongFormatException {
        if (!length.matches("[0-9]{1,3}")) {
            throw new WrongFormatException("Bad length format");
        }

        try {
            int l = Integer.parseInt(length);
            parse(l);
        } catch (Exception e) {
            throw new WrongFormatException("Bad length format");
        }
    }

    private void parse(int length) throws  WrongFormatException{
        if (length < 1 || length > 500) {
            throw new WrongFormatException("Bad length format");
        }

        int cpt = this.length.length - 1;

        while (cpt >= 0) {
            this.length[cpt] = (char)(((int)'0') + (length % 10));
            length /= 10;
            --cpt;
        }
    }

    @Override
    public String toString() {
        String res = "";
        res = String.valueOf(this.length);

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Length length1 = (Length) o;

        if (!Arrays.equals(length, length1.length)) return false;

        return true;
    }

    /**
     * Retourne la valeur entière de la taille.
     * @return valeur entière.
     */
    public Integer toInteger() {
        return Integer.parseInt(toString());
    }
}
