package Services;

import TI.PrinterDebug;
import data.LoggedUser;
import data.Session;
import formats.User;

import java.io.IOException;
import java.net.*;

public class UDPServer {
    private static UDPServer udps = null;

    private final Integer PORT      = 9876;
    private final String  GROUP_IP  = "224.5.6.7";
    private final InetSocketAddress ISA = new InetSocketAddress(GROUP_IP, PORT);
    private final InetAddress IA = InetAddress.getByName(GROUP_IP);

    private Thread refreshThread;
    private MulticastSocket server;

    private Boolean connected = false;

    /**
     * Create the UDP Server
     * @throws IOException
     */
    public UDPServer() throws IOException {
        if (udps == null) {
            Session.localBanCount = 0;
            Session.banList.clear();
            Session.bannedMessageSendToUsers.clear();

            server = new MulticastSocket(PORT); /* Voir comment enlever le port */
            udps = this;

            server.joinGroup(IA);
            connected = true;

            try {
                sayHello();
            } catch(Exception e) {
                /* FATAL ERROR ! */
                return;
            }

            refreshThread = new Thread() {
                private int fail = 0;

                public void run() {
                    try {
                        while (true) {
                            if (fail == 3) {
                                System.exit(1);
                            }

                            Thread.sleep(30000);

                            try {
                                sayHeyIWantSomeNews();
                            } catch (IOException e) {
                                ++fail;
                            }
                        }
                    }
                    catch (Exception e){
                        /* Leaving Thread */
                    }
                }
            };
            refreshThread.start();
        }
    }


    /**
     * Return the current instance of the udp server.
     * @return udp server
     */
    public static UDPServer getCurrentInstance() {
        return udps;
    }

    /**
     * Send HLO message.
     * @return true if it success.
     * @throws IOException
     */
    public boolean sayHello() throws IOException {
        if (udps != null) {
            UDPService.sendMessage(udps.server, udps.ISA, UDPService.HLO_CODE);
            return true;
        }

        return false;
    }

    /**
     * Send RFH message.
     * @return true if it success.
     * @throws IOException
     */
    public boolean sayHeyIWantSomeNews() throws IOException {
        if (udps != null) {
            UDPService.sendMessage(udps.server, udps.ISA, UDPService.RFH_CODE);
            return true;
        }

        return false;
    }

    /**
     * Send BAN message.
     * @param u
     * @return true if it succeed
     * @throws IOException
     */
    public boolean sayBan(User u) throws IOException {
        if (udps != null) {
            LoggedUser lu = Session.loggedUsers.getLoggedUserByName(u);

            if (lu == null) {
                return false;
            }

            if (Session.bannedMessageSendToUsers.contains(u)) {
                return false;
            }

            Session.bannedMessageSendToUsers.add(u);
            Session.banVote(u.toString(), new LoggedUser(u));
            UDPService.sendMessage(udps.server, udps.ISA, UDPService.BAN_CODE, lu);
            return true;
        }

        return false;
    }

    /**
     * Send BYE message.
     * @return true if it succeed
     * @throws IOException
     */
    public boolean sayGoodBye() throws IOException {
        if (udps != null) {
            UDPService.sendMessage(udps.server, udps.ISA, UDPService.BYE_CODE);
            refreshThread.interrupt();
            connected = false;
            server.leaveGroup(IA);
            server.close();

            Session.loggedUsers.clear();

            udps = null;
            return true;
        }

        return false;
    }

    /**
     * Listening function.
     */
    public void listeningMessage() {
        PrinterDebug.printUDP("Start to process listeningMessage");
        byte[] receiveData = new byte[64];
        while (connected) {
            PrinterDebug.printUDP("Waiting packet...");
            DatagramPacket receiver = new DatagramPacket(receiveData, receiveData.length);

            try {
                server.receive(receiver);
                PrinterDebug.printUDP("Packet received");
            } catch (IOException e) {
                PrinterDebug.printUDP("Un message n'a pu être lu.\n\n");
                continue;
            }

            /*
            if (Session.addressList.contains(receiver.getAddress().getHostAddress())) {
                PrinterDebug.printUDP("Home Message");
                continue;
            }
            */

            PrinterDebug.printUDP("Running service");
            new UDPService(server, ISA, receiver).run();
        }
    }
}
