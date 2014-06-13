package Services;

import TI.PrinterDebug;
import TI.TextualInterface;
import data.LoggedUser;
import data.Session;
import formats.Machine;
import formats.Port;
import formats.User;
import formats.WrongFormatException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPService extends Thread {
    private static final String HLO = "HLO";
    private static final String IAM = "IAM";
    private static final String BYE = "BYE";
    private static final String RFH = "RFH";
    private static final String BAN = "BAN";

    public static final int HLO_CODE = 1;
    public static final int IAM_CODE = 2;
    public static final int BYE_CODE = 3;
    public static final int RFH_CODE = 4;
    public static final int BAN_CODE = 5;

    private final DatagramSocket ds;
    private final InetSocketAddress isa;
    private final DatagramPacket dp;

    private String receivedMessage;

    /**
     * Create the service.
     * @param ds the socket
     * @param isa the address
     * @param dp the packet
     */
    public UDPService(DatagramSocket ds,
                      InetSocketAddress isa,
                      DatagramPacket dp) {
        this.ds = ds;
        this.isa = isa;
        this.dp = dp;
        receivedMessage = new String(dp.getData());
    }


    @Override
    public void run() {
        PrinterDebug.printUDP("\tEntry point for service");
        User    u;
        Machine m1, m2;
        Port    p;
        switch (getCode(getMessageType())) {
            case HLO_CODE:

                Session.writeMessageInformation(true,getMessageType(), receivedMessage);

                try {
                    u = new User(getUser());
                } catch(WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~getUser()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    m1 = getHostAddress();
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~DatagramPacket.getAddress()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    m2 = new Machine(getMachine());
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~getMachine()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    p = new Port(getPort());
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~getPort()]",
                            wfe.getMessage());
                    break;
                }

                LoggedUser newUser =
                        new LoggedUser(u, m1, m2, p);

                if (!Session.banList.contains(newUser)) {
                    Session.loggedUsers.addLoggedUser(newUser);
                }

                try {
                    sendMessage(ds, isa, IAM_CODE);
                } catch (IOException e) {
                    PrinterDebug.printUDP("FAIL TO SEND UDP MESSAGE.\n");
                }

                PrinterDebug.printUDP(Session.loggedUsers.toString());

                break;
            case IAM_CODE:

                Session.writeMessageInformation(true,getMessageType(), receivedMessage);

                try {
                    u = new User(getUser());
                } catch(WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~getUser()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    m1 = getHostAddress();
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~DatagramPacket.getAddress()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    m2 = new Machine(getMachine());
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~getMachine()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    p = new Port(getPort());
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [IAM~getPort()]",
                            wfe.getMessage());
                    break;
                }


                LoggedUser newUsr =
                        new LoggedUser(u, m1, m2, p);

                if (!Session.banList.contains(newUsr)) {
                    Session.loggedUsers.addLoggedUser(newUsr);
                } else {
                    /* Notifier la tentative de connexion ?? */
                }
                PrinterDebug.printUDP(Session.loggedUsers.toString());

                break;
            case BYE_CODE:

                Session.writeMessageInformation(true,getMessageType(), receivedMessage);

                try {
                    u = new User(getUser());
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [BYE~getUser()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    m1 = getHostAddress();
                } catch (WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [BYE~DatagramPacket.getAddress()]",
                            wfe.getMessage());
                    break;
                }

                Session.loggedUsers.deleteByUserAndAddress(u, m1);

                break;
            case RFH_CODE:

                Session.writeMessageInformation(true,getMessageType(), receivedMessage);

                try {
                    sendMessage(ds, isa, IAM_CODE);
                } catch (IOException e) {
                    PrinterDebug.printTCP("//!\\\\ ERROR WHILE SENDING MESSAGE.");
                }

                break;
            case BAN_CODE:

                Session.writeMessageInformation(true,getMessageType(), receivedMessage);

                try {
                    u = new User(getUser());
                } catch(WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [BAN~getUser()]",
                            wfe.getMessage());
                    break;
                }

                try {
                    m1 = new Machine(getMachine());
                } catch(WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [BAN~getMachine()]",
                            "The sender version doesn't respect this protocol.");
                    m1 = null;
                    break;
                }

                try {
                    m2 = getHostAddress();
                } catch(WrongFormatException wfe) {
                    Session.writeErrorMessage("UDP Message [BAN~DatagramPacket.getAddress()]",
                            wfe.getMessage());
                    break;
                }

                LoggedUser lu = Session.loggedUsers.getLoggedUserByUserAndAddress(u, m1);
                if (lu == null) {
                    Session.writeErrorMessage("UDP Message [BAN]",
                            "Unknown user to ban");
                    break;
                }

                if (u.toString().equals(Session.localUser.toString())) {
                    System.out.println("Vous avez reçu un ban.");
                    Session.localBanCount++;

                    if (Session.localBanCount >= 2) {
                        TextualInterface.closeSession();
                    }
                } else {
                    System.out.println("Ban reçu pour: " + u);
                    Session.banVote(u.toString(), lu);
                }

                break;
            default:
                /* Comment traite-t-on les message inconnu ? */
                break;
        }

        PrinterDebug.printUDP("\n\n");
    }

    /**
     * Send the message.
     * @param ds socket
     * @param isa address
     * @param msgType message type
     * @param args necessary args
     * @throws IOException
     */
    public static void sendMessage(DatagramSocket ds, InetSocketAddress isa,
                                   int msgType, Object... args) throws IOException {
        DatagramPacket dp;
        byte[] msgArray;
        switch (msgType) {
            case HLO_CODE:
                msgArray = getHLOMessage().getBytes();
                dp = new DatagramPacket(msgArray, msgArray.length, isa);
                break;
            case IAM_CODE:
                msgArray = getIAMMessage().getBytes();
                dp = new DatagramPacket(msgArray, msgArray.length, isa);
                break;
            case RFH_CODE:
                msgArray = getRFHMessage().getBytes();
                dp = new DatagramPacket(msgArray, msgArray.length, isa);
                Session.loggedUsers.clear();
                break;
            case BYE_CODE:
                msgArray = getBYEMessage().getBytes();
                dp = new DatagramPacket(msgArray, msgArray.length, isa);
                break;
            case BAN_CODE:
                if (args.length > 0 && args[0] instanceof LoggedUser) {
                    msgArray = getBANMessage((LoggedUser) args[0]).getBytes();
                    dp = new DatagramPacket(msgArray, msgArray.length, isa);
                } else {
                    System.out.println("...");
                    return;
                }
                break;
            default:
                return;
        }

        Session.writeMessageInformation(false, getType(msgType), new String(msgArray));
        ds.send(dp);
    }

    private static String getHLOMessage() {
        final String HLOmsg =
                "HLO " + Session.localUser
                       + " " + Session.localMachine
                       + " " + Session.localPort;

        return HLOmsg;
    }

    private static String getIAMMessage() {
        final String IAMmsg =
                "IAM " + Session.localUser
                       + " " + Session.localMachine
                       + " " + Session.localPort;

        return IAMmsg;
    }

    private static String getBYEMessage() {
        final String BYEmsg =
                "BYE " + Session.localUser;

        return BYEmsg;
    }

    private static String getRFHMessage() {
        final String RFHmsg =
                "RFH";

        return RFHmsg;
    }

    private static String getBANMessage(LoggedUser lu) {
        final String BANmsg =
                "BAN " + lu.getUser();

        return BANmsg;
    }


    private String getMessageType() {
        PrinterDebug.printUDP("MESSAGE TYPE : " + receivedMessage.substring(0, 3));
        return receivedMessage.substring(0, 3);
    }

    private String getUser() {
        PrinterDebug.printUDP("USER : " + receivedMessage.substring(4, 12));
        return receivedMessage.substring(4, 12);
    }

    private String getMachine() {
        PrinterDebug.printUDP("MACHINE : " + receivedMessage.substring(13, 28));
        return receivedMessage.substring(13, 28);
    }

    private String getPort() {
        PrinterDebug.printUDP("PORT : " + receivedMessage.substring(29, 34));
        return receivedMessage.substring(29, 34);
    }

    private Machine getHostAddress() throws WrongFormatException {
        PrinterDebug.printUDP("HOST ADDRESS : " + dp.getAddress().getHostAddress());
        Machine m = new Machine(
                new String(dp.getAddress().getHostAddress())
        );

        return m;
    }

    /**
     * Give the type code.
     * @param type type
     * @return code
     */
    public static Integer getCode(String type) {
        if (type.equals(HLO)) {
            return HLO_CODE;
        } else if (type.equals(IAM)) {
            return IAM_CODE;
        } else if (type.equals(RFH)) {
            return RFH_CODE;
        } else if (type.equals(BYE)) {
            return BYE_CODE;
        } else if (type.equals(BAN)) {
            return BAN_CODE;
        } else {
            return -1;
        }
    }

    /**
     * Give the code type
     * @param code code
     * @return type
     */
    public static String getType(int code) {
        switch(code) {
            case HLO_CODE:
                return HLO;
            case IAM_CODE:
                return IAM;
            case RFH_CODE:
                return RFH;
            case BYE_CODE:
                return BYE;
            case BAN_CODE:
                return BAN;
            default:
                return "UNK";
        }
    }
}
