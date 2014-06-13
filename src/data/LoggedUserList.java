package data;

import formats.Machine;
import formats.Port;
import formats.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoggedUserList {
    private CopyOnWriteArrayList<LoggedUser> loggedUsers;

    /**
     * Create the LoggedUserList.
     */
    public LoggedUserList() {
        loggedUsers = new CopyOnWriteArrayList<LoggedUser>();
    }

    /**
     * Return the array size.
     * @return size
     */
    public int size() {
        return loggedUsers.size();
    }

    /**
     * Add the logged user in the list with the correct particule.
     * @param lu logged user
     */
    public void addLoggedUser(LoggedUser lu) {
        for (LoggedUser u : loggedUsers) {
            if (u.isSender(lu)) {
                return;
            }
        }

        lu.setParticuleToId();
        loggedUsers.add(lu);
    }

    /**
     * Find a logged user by his name.
     * @param u user
     * @return the logged user if it exists.
     */
    public LoggedUser getLoggedUserByName(User u) {
        for (LoggedUser lu : loggedUsers) {
            if (lu.hasSameName(u)) {
                return lu;
            }
        }

        return null;
    }

    /**
     * Return logged user by its address
     * @param m machine
     * @return logged user
     */
    public LoggedUser getLoggedUserByFromAddress(Machine m) {
        for (LoggedUser lu : loggedUsers) {
            if (lu.isSender(m)) {
                return lu;
            }
        }

        return null;
    }

    /**
     * Return logged user by its address and its port.
     * @param m machine
     * @param p port
     * @return logged user
     */
    public LoggedUser getLoggedUserByFromAddress(Machine m, Port p) {
        for (LoggedUser lu : loggedUsers) {
            if (lu.isSender(m, p)) {
                return lu;
            }
        }

        return null;
    }

    /**
     * Return logged user by the username and the machine
     * @param u user
     * @param m machine
     * @return logged user
     */
    public LoggedUser getLoggedUserByUserAndAddress(User u, Machine m) {
        for (LoggedUser lu : loggedUsers) {
            if (lu.testUserByUserAndAddress(u, m)) {
                return lu;
            }
        }

        return null;
    }

    /**
     * Return logged user by the username, the machine and the port
     * @param u user
     * @param m machine
     * @param p port
     * @return logged user
     */
    public LoggedUser getLoggedUserByUserAndAddress(User u, Machine m, Port p) {
        for (LoggedUser lu : loggedUsers) {
            if (lu.testUserByUserAndAddress(u, m, p)) {
                return lu;
            }
        }

        return null;
    }

    /**
     * Delete the logged user if he exists.
     * @param lu logged user
     */
    public void deleteLoggedUser(LoggedUser lu) {
        loggedUsers.remove(lu);
    }

    /**
     * Delete the logged user by its username and its address.
     * @param u user
     * @param m machine
     */
    public void deleteByUserAndAddress(User u, Machine m) {
        for (LoggedUser lu : loggedUsers) {
            if (lu.isSender(u, m)) {
                loggedUsers.remove(lu);
            }
        }
    }

    /**
     * Return the sorted list by name of the logged user list.
     * @return sorted list
     */
    public ArrayList<LoggedUser> getSortedLoggedUserListByName() {
        ArrayList<LoggedUser> tmp =
                new ArrayList<LoggedUser>((java.util.Collection<? extends LoggedUser>) loggedUsers.clone());
        Collections.sort(tmp);
        return tmp;
    }

    /**
     * Return the sorted list by name and user address.
     * @return sorted list
     */
    public ArrayList<LoggedUser> getSortedLoggedUserListByUserAddress() {
        ArrayList<LoggedUser> tmp =
                new ArrayList<LoggedUser>((java.util.Collection<? extends LoggedUser>) loggedUsers.clone());
        Collections.sort(tmp, new Comparator<LoggedUser>() {
            @Override
            public int compare(LoggedUser loggedUser, LoggedUser loggedUser2) {
                return loggedUser.compareToByUserAddress(loggedUser2);
            }
        });
        return tmp;
    }

    /**
     * Return the sorted list by port
     * @return sorted list
     */
    public ArrayList<LoggedUser> getSortedLoggedUserListByPort() {
        ArrayList<LoggedUser> tmp =
                new ArrayList<LoggedUser>((java.util.Collection<? extends LoggedUser>) loggedUsers.clone());
        Collections.sort(tmp, new Comparator<LoggedUser>() {
            @Override
            public int compare(LoggedUser loggedUser, LoggedUser loggedUser2) {
                return loggedUser.compareToByPort(loggedUser2);
            }
        });
        return tmp;
    }

    /**
     * Clear the logged list
     */
    public void clear() {
        loggedUsers.clear();
        LoggedUser.resetId();
    }

    @Override
    public String toString() {
        String s = "";
        for (LoggedUser l : loggedUsers){
            s += l.toString() + "\n";
        }
        return s;
    }
}
