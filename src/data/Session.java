package data;

import formats.Machine;
import formats.Port;
import formats.User;
import formats.WrongFormatException;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Session {
    private static FileWriter fw = null;
    private static FileWriter no = null;

    public static User localUser;
    public static Machine localMachine;
    public static Port localPort;

    public static void initFW() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

        try {
            fw = new FileWriter("log/err_" + sdf.format(new java.util.Date()) + ".log", true);
        } catch (IOException e) {
            System.err.println("No error log this session.");
        }

        try {
            no = new FileWriter("log/inf_" + localUser  + ".log");
        } catch (IOException e) {
            System.err.println("No msg_informations.log");
        }
    }




    static {
        try {
            localMachine = new Machine(InetAddress.getLocalHost().getHostAddress());
        } catch (WrongFormatException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static final LinkedList<String> addressList = new LinkedList<String>();
    public static final LoggedUserList loggedUsers = new LoggedUserList();

    public static final ArrayList<LoggedUser> banList = new ArrayList<LoggedUser>();

    public static final void writeErrorMessage(String title, String body) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("kk'h':mm'm':ss's'");

        if (fw == null) return;

        try {
            fw.write(
                    "[" + sdf.format(new java.util.Date()) + "]\n"
                  + "\t" + title + ":\n"
                  + "\t" + body + "\n\n"
            );

            fw.flush();
        } catch (IOException e) {
            System.err.println("Error while writing on error log. Yeah. Fuck it.");
        }
    }

    public static final void writeMessageInformation(boolean received, String msgType, String body) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("dd/MM/yy '~' kk'h':mm'm':ss's'");

        if (no == null) return;

        try {
            no.write(
                    "[" + sdf.format(new java.util.Date()) + "]\n"
                            + "\t" + msgType + "(" + (received ? "reçu" : "envoye") + ") :\n"
                            + "\t" + body + "\n\n"
            );

            no.flush();
        } catch (IOException e) {
            System.err.println("Error while writing on error log. Yeah. Fuck it.");
        }
    }



    public static int localBanCount = 0;

    public static ArrayList<User> bannedMessageSendToUsers = new ArrayList<User>();



    public static HashMap<String, Integer> banCount = new HashMap<String, Integer>();
    public synchronized static void banVote(String user, LoggedUser lu) {
        int next = 1;

        if (Session.banCount.containsKey(user)) {
            next = Session.banCount.get(user) + 1;
        }

        Session.banCount.put(user, next);

        if (next >= 2) {
            Session.banList.add(lu);
            Session.loggedUsers.deleteLoggedUser(lu);
        }
    }
}
