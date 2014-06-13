package formats;

import java.util.Arrays;

/**
 * Représente l'addresse d'une machine dans le format du système.
 */
public class Machine {
    private final char[][] machine;

    /**
     * Création de l'addresse à partir d'une chaîne de caractère.
     * @param addresse chaîne
     * @throws WrongFormatException si la chaîne ne correspond à une adresse IPV4.
     */
    public Machine(String addresse) throws WrongFormatException {
        machine = new char[4][3];
        fillMachine(addresse);
    }

    private void fillMachine(String address) throws WrongFormatException {

        if (!address.matches("[0-9]{1,3}(\\.[0-9]{1,3}){3}")) {
            throw new WrongFormatException("Bad address format (" + address + ")");
        }

        String[] adr = address.split("\\.");
        int i = 0;
        for (String s : adr) {
            int value = Integer.parseInt(s);
            if (value < 0 || value > 255) throw new WrongFormatException("Bad number (" + value + ")");

            char[] tmp = s.toCharArray();
            int j = 0;

            while (j < (3 - tmp.length)) {
                this.machine[i][j++] = '0';
            }

            for (Character c : tmp) {
                this.machine[i][j++] = c;
            }

            ++i;
        }
    }

    /**
     * Compare deux machine.
     *
     * Il renvoit un nombre négatif si l'objet courant est plus petit.
     * Il renvoit 0 si les deux sont égaux.
     * Il renvoit un nombre positif si l'objet est courant.
     *
     * Par exemple :
     *
     * 192.168.000.001 < 192.168.000.002
     * 191.154.010.255 < 192.168.000.000
     *
     * 192.168.000.100 = 192.168.000.100
     *
     * 099.000.100.001 > 090.255.255.255
     * 154.200.200.200 > 154.200.200.199
     *
     * @param m la seconde machine
     * @return la valeur de comparaison
     */
    public int compare(Machine m) {
        int[] thisMachine = toArray();
        int[] mMachine    = m.toArray();

        for (int i = 0; i < 4; i++) {
            int res = thisMachine[i] - mMachine[i];
            if (res != 0) {
                return res;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        String res = "";

        for (int i = 0; i < 3; i++) {
            res += String.valueOf(machine[i]) + ".";
        }

        res += String.valueOf(machine[machine.length - 1]);

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Machine m = (Machine) o;

        if (machine.length != m.machine.length) return false;

        for (int i = 0; i < machine.length; i++) {
            if (!Arrays.equals(machine[i], m.machine[i])) return false;
        }

        return true;
    }


    private int[] toArray() {
        int[] res = new int[4];

        int i = 0;
        for (char[] c : machine) {
            res[i] = Integer.parseInt(String.valueOf(c));
            ++i;
        }

        return res;
    }
}
