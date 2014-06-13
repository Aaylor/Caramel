package data;

import formats.Machine;
import formats.Port;
import formats.User;

import java.util.ArrayList;

public class LoggedUser implements Comparable {
    private static int addedId = 0;

    private Machine addressFrom;

    private User user;
    private Integer particule;
    private Machine userAddress;
    private Port port;

    /**
     * Create a LoggedUser by the username.
     * @param user username
     */
    public LoggedUser(User user) {
        this.user = user;
        this.addressFrom = null;
        this.port = null;

        particule = 0;
    }

    /**
     * Create a complete LoggedUser.
     * @param user username
     * @param addressFrom address from the user come
     * @param userAddress user address
     * @param port user port
     */
    public LoggedUser(User user, Machine addressFrom, Machine userAddress, Port port) {
        this.user = user;
        this.addressFrom = addressFrom;
        this.userAddress = userAddress;
        this.port = port;

        particule = 0;
    }

    /**
     * Reset the Id.
     */
    public static void resetId() {
        addedId = 0;
    }

    /**
     * Return the user.
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * Return the machine.
     * @return machine
     */
    public Machine getUserAddress() { return userAddress; }

    /**
     * Return the port.
     * @return port
     */
    public Port getPort() { return port; }

    /**
     * Add a particule for the user.
     */
    public synchronized void setParticuleToId() {
        this.particule = addedId++;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LoggedUser) {
            LoggedUser lu = (LoggedUser) o;

            return user.equals(lu.user)
                    && particule.equals(lu.particule);
        }

        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof LoggedUser) {
            int res = user.toString()
                    .compareToIgnoreCase(((LoggedUser) o).user.toString());

            if (res == 0) {
                return particule.compareTo(((LoggedUser) o).particule);
            } else {
                return res;
            }
        }

        return 0;
    }

    /**
     * Compare two loggedusers by addresses.
     * @param o the other logged user
     * @return integer
     */
    public int compareToByUserAddress(Object o) {
        if (o instanceof LoggedUser) {
            int res = userAddress.compare(((LoggedUser) o).userAddress);

            if (res == 0) {
                return compareTo(o);
            } else {
                return res;
            }
        }

        return 0;
    }

    /**
     * Compare two logged users by port
     * @param o the other logged user
     * @return integer
     */
    public int compareToByPort(Object o) {
        if (o instanceof LoggedUser) {
            int res = port.compare(((LoggedUser) o).port);
            if (res == 0) {
                return compareTo(o);
            } else {
                return res;
            }
        }

        return 0;
    }

    /**
     * Return if the given machine is the sender.
     * @param m machine
     * @return true if it's the sender
     */
    public boolean isSender(Machine m) {
        return addressFrom.equals(m);
    }

    /**
     * Return if the tuple user machine is the sender.
     * @param u user
     * @param m machine
     * @return true if it's the sender
     */
    public boolean isSender(User u, Machine m) {
        return user.equals(u) && addressFrom.equals(m);
    }

    /**
     * Return if the logged user is the sender.
     * @param lu the logged user
     * @return true if it's the sender
     */
    public boolean isSender(LoggedUser lu) {
        return lu.user.equals(user) && lu.addressFrom.equals(addressFrom);
    }

    /**
     * Return if the given machine & port is the sender.
     * @param m machine
     * @param p port
     * @return true if it's the sender
     */
    public boolean isSender(Machine m, Port p) {
        return addressFrom.equals(m) && port.equals(p);
    }

    /**
     * Test by the user and machine if it's the current logged user
     * @param u user
     * @param m machine
     * @return true if it(s the current logged user
     */
    public boolean testUserByUserAndAddress(User u, Machine m) {
        return user.equals(u) && (m == null || userAddress.equals(m));
    }

    /**
     * Test, by user, machine and port if it's the current logged user
     * @param u user
     * @param m machine
     * @param p port
     * @return true if it's the current user
     */
    public boolean testUserByUserAndAddress(User u, Machine m, Port p) {
        return user.equals(u) && userAddress.equals(m) && port.equals(p);
    }

    /**
     * Test if the logged user has the same name
     * @param u user
     * @return true if it's the current
     */
    public boolean hasSameName(User u) {
        return u.equals(user);
    }

    @Override
    public String toString() {
        return user + " (" + String.format("%4d", particule) + ") "
                + " [" + userAddress + ":" + port + "]";
    }
}
