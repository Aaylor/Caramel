package formats;

import java.util.Arrays;

/**
 * Représente le nom d'un utilisateur dans le système.
 */
public class User {
    private final char[] username = new char[8];

    /**
     * Créer l'objet à partir d'une chaîne de caractère.
     * @param username Chaîne contenant le nom de l'utilisateur.
     * @throws WrongFormatException Si ne correspond pas au format.
     */
    public User(String username) throws WrongFormatException {
        formatName(username);
    }

    private void formatName(String username) throws WrongFormatException {
        char[] usernameArray = username.toCharArray();
        int length = usernameArray.length;
        int cpt = 0;

        if (!username.matches("^\\p{ASCII}{1,8}$"))
            throw new WrongFormatException("Bad username format.");

        while (cpt < length) {
            this.username[cpt] = usernameArray[cpt];
            ++cpt;
        }

        while (cpt < 8) {
            this.username[cpt++] = (char)32;
        }
    }

    @Override
    public String toString() {
        String res = String.valueOf(username);

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Arrays.equals(username, user.username)) return false;

        return true;
    }
}
