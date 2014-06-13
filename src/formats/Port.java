package formats;

import java.util.Arrays;

public class Port {
    public final char[] port = new char[5];

    public Port(String port) throws WrongFormatException {
        formatPort(port);
    }

    public Port(int port) throws WrongFormatException {
        this(new String(String.valueOf(port)));
    }

    private void formatPort(String port) throws WrongFormatException {
        char[] portArray = port.toCharArray();
        int length = portArray.length;
        int cpt = 0;

        if (length > 5 || length <= 0) {
            throw new WrongFormatException("Bad port size");
        }

        while (cpt < (5 - length)) {
            this.port[cpt++] = (char)'0';
        }
        
        for (Character c : portArray) {
            if ((int)c < (int)'0' || (int)c > (int)'9') {
                throw new WrongFormatException("Bad port format");
            }

            this.port[cpt++] = c;
        }
    }


    public int compare(Port p) {
        return Integer.parseInt(String.valueOf(port).trim())
                - Integer.parseInt(String.valueOf(p.port).trim());
    }

    @Override
    public String toString() {
        String res = String.valueOf(port);

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Port port1 = (Port) o;

        if (!Arrays.equals(port, port1.port)) return false;

        return true;
    }

    /**
     * Renvoie la valeur entière du port.
     * @return valeur entière.
     */
    public Integer parseToInteger() {
        return Integer.parseInt(String.valueOf(port));
    }

}
