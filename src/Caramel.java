import Services.UDPServer;
import TI.PrinterDebug;
import TI.TextualInterface;


public class Caramel {

    public static void main(String[] args) {
        for (String s : args) {
            if (s.equals("-t")) {
                if (PrinterDebug.tcpDebugPrint) {
                    System.err.println("Option -t en plusieurs exemplaires.");
                    System.exit(1);
                }

                PrinterDebug.tcpDebugPrint = true;
            } else if (s.equals("-u")) {
                if (PrinterDebug.udpDebugPrint) {
                    System.err.println("Option -u en plusieurs exemplaires.");
                    System.exit(1);
                }

                PrinterDebug.udpDebugPrint = true;
            } else {
                System.err.println("Option " + s + " non reconnue.");
                System.exit(1);
            }
        }


        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                UDPServer uds = UDPServer.getCurrentInstance();
                if (uds != null) {
                    TextualInterface.closeSession();
                }

            }
        });

        new TextualInterface();


        /*
        if (args.length != 2) {
            System.err.println("./skaipeuh username port");
            System.exit(1);
        }

        try {
            Session.localUser = new User(args[0]);
        } catch (WrongFormatException wfe) {
            System.err.println("Le pseudonyme est incorrect: de 1 à 8 caractère(s) ascii");
            System.exit(1);
        }

        try {
            Session.localPort = new Port(args[1]);
        } catch (WrongFormatException wfe) {
            System.err.println("Port incorrect : de 1 à 5 chiffres.");
            System.exit(1);
        }

        Session.initFW();

        try {
            fillAddressList();
        } catch (SocketException se) {
            Session.writeErrorMessage("Address List [Caramel~fillAddressList]",
                    "Can't get address list.");
        }
        System.out.println("address list: " + Session.addressList);

        new Thread() {
            public void run() {
                System.out.println("UDP Server created.");
                try {
                    new UDPServer().listeningMessage();
                } catch (IOException e) {
                    System.err.println("UDPServer.");
                    e.printStackTrace();
                }
            }
        }.start();


        new Thread() {
            public void run() {
                System.out.println("TCP Server created.");
                System.out.println(Session.localMachine + ":" + Session.localPort);
                new ServiceTCP(Session.localPort);
            }
        }.start();*/
    }
}
