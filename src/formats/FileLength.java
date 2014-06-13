package formats;

import java.util.Arrays;

/**
 *  Enregistre un format de type FileLength correspondant au format du systeme.
 */
public class FileLength {
    private char[] fileLength;

    /**
     * @param fl Entier correspondant à une taille de fichier
     * @throws WrongFormatException
     */
    public FileLength(int fl) throws WrongFormatException {
        parse(String.valueOf(fl));
    }

    /**
     * @param fl Chaîne de caractère correspondant à une taille de fichier.
     * @throws WrongFormatException Si la chaîne possède un caractère n'étant un entier.
     */
    public FileLength(String fl) throws WrongFormatException {
        parse(fl);
    }


    private void parse(String fl) throws WrongFormatException {
        if (!fl.matches("[0-9]+")) {
            throw new WrongFormatException("Badly File Length Format");
        }

        char [] tmp = String.valueOf(Integer.parseInt(fl)).toCharArray();
        fileLength = Arrays.copyOf(tmp, tmp.length);
    }

    @Override
    public String toString() {
        return String.valueOf(fileLength);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileLength that = (FileLength) o;

        if (!Arrays.equals(fileLength, that.fileLength)) return false;

        return true;
    }

    /**
     * Renvoie la taille sous forme d'entier.
     * @return valeur de la taille.
     */
    public int toInteger() {
        return Integer.parseInt(toString());
    }
}
