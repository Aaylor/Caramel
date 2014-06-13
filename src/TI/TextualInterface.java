package TI;

import Services.*;
import data.LoggedUser;
import data.Session;
import formats.*;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class TextualInterface {

    /**
     * Enumere les etats possibles
     */
    public enum Etat {
        Disconnected,
        Connected,
        Conversation
    }

    /**
     * Classe qui stock la file d'attente de fichier
     */
    static class FileWL {
        public Filename filename;
        public FileLength fileLength;
        public Port port;
        public Machine machine;
    }

    /**
     * Le conctructeur lance l'initiation
     */
    public TextualInterface() {
        initTextualInterface();
    }

    public static Etat etat;
    private User user;
    private static User uConv = null;
    private static ServiceTCP serviceTCP;
    private static UDPServer udpServer = null;
    public static LinkedList<FileWL> fileToReceive = new LinkedList<FileWL>();
    public static LinkedList<SendFile> fileToSend = new LinkedList<SendFile>();

    /**
     * Boucle en récupérant les entrées utilisateurs
     */
    private void initTextualInterface() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n *** Bienvenue dans Caramel ! ***\n\n" +
                    " - Entrez /help pour obtenir la liste des commandes.");
            etat = Etat.Disconnected;
            while (true) {
                System.out.print(
                        etat == Etat.Disconnected ? "déconnecté > " :
                                (etat == Etat.Connected ? "connecté > " : "conversation > ")
                );
                readUserInput(scanner.nextLine());
            }
        }catch(NoSuchElementException e) {
            //Reception d'un signal ctrl+c
            System.exit(1);
        }
    }

    /**
     * Lis l'entré standard
     * @param s l'entré standard
     */
    private void readUserInput(String s) {

        //Mode Disconnected
        if (etat.equals(Etat.Disconnected)){
            if(s.equals("/help")){
                System.out.println(" - Listes Commandes : \n" +
                        "/connect username  Connection sous 'username'\n" +
                        "/quit              Ferme Caramel");
            }
            else if (s.startsWith("/connect ")){
                try {
                    user = new User(s.substring(9));
                    etat = Etat.Connected;
                    initSession(user);
                } catch (WrongFormatException e) {
                    System.out.println(e);
                }
            }
            else if(s.equals("/quit")){
                System.out.println("Au revoir !");
                System.exit(0);
            }
            else if(!s.isEmpty()){
                System.out.println(" - Commande non reconnue, /help pour obtenir la liste des commandes.");
            }
        }

        //Mode Connected
        else if(etat.equals(Etat.Connected)){
            if(s.equals("/help")){
                System.out.println(" - Listes Commandes : \n" +
                        "/ban username      Demande le ban de 'username'\n" +
                        "/refresh           Raffraichit la liste des utilisateurs\n" +
                        "/call username     Appel 'username' en conversation privée\n" +
                        "/disconnect        Deconnecte de la session\n" +
                        "/who               Montre les autres utilisateurs connectés");
            }
            else if(s.startsWith("/ban ")){
                try {
                    User uBan = new User(s.substring(5));
                    udpServer.sayBan(uBan);
                } catch (WrongFormatException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(s.startsWith("/refresh")) {
                try {
                    udpServer.sayHeyIWantSomeNews();
                } catch (IOException e) {
                    System.out.println(" - Erreur lors du refresh.");
                }
            }
            else if(s.startsWith("/call ")){
                try {
                    User uCall = new User(s.substring(6));

                    if (uCall.toString().equals(Session.localUser.toString())) {
                        System.out.println(" - Vous ne pouvez pas vous appelez vous même.");
                        return;
                    }

                    LoggedUser loggedUser = null;
                    if((loggedUser = Session.loggedUsers.getLoggedUserByName(uCall)) != null){
                        etat = Etat.Conversation;
                        serviceTCP.connectionToUser(loggedUser.getUserAddress(), loggedUser.getPort());
                        uConv = uCall;
                    }
                    else {
                        System.out.println(" - Le 'username' fournis ne correspond à aucun utilisateur connecté.");
                    }
                } catch (WrongFormatException e) {
                    System.out.println(e);
                }
            }
            else if(s.equals("/disconnect")){
                System.out.println("Vous êtes maintenant déconnecté.");
                closeSession();
            }
            else if(s.equals("/who")){
                System.out.println(Session.loggedUsers.toString());
            }
            else if(!s.isEmpty()){
                System.out.println(" - Commande non reconnue, /help pour obtenir la liste des commandes.");
            }
        }

        // Mode Conversation
        else {
            if(s.equals("/help")){
                System.out.println(" - Listes Commandes: \n" +
                        "/accept            Accepte le premier fichier dans la file d'attente\n" +
                        "/file              Montre les fichiers en attentes\n" +
                        "/quit              Ferme la conversation\n" +
                        "/reject            Refuse le premier fichier dans la file d'attente\n" +
                        "/send filename     Propose l'envoi de 'filename'");
            }

            else if(s.equals("/accept")){
                if(!fileToReceive.isEmpty()){
                    FileWL f = fileToReceive.pop();
                    serviceTCP.sendACK();
                    new ReceiveFile(f.fileLength,f.filename,f.port,f.machine).start();
                }
                else{
                    System.out.println(" - Aucun fichier en attente.");
                }
            }

            else if(s.equals("/file")){
                int n = 1;
                for(FileWL f : fileToReceive){
                    System.out.println(n + " : " + f.fileLength + " " + f.filename + " " + f.port + " " + f.machine);
                    n++;
                }
            }

            else if(s.equals("/quit")){
                etat = Etat.Connected;
                serviceTCP.sendCLO();
            }

            else if(s.equals("/reject")){
                if(!fileToReceive.isEmpty()){
                    fileToReceive.pop();
                    serviceTCP.sendNAK();
                }
                else{
                    System.out.println(" - Aucun fichier en attente.");
                }
            }

            else if(s.startsWith("/send ")){
                try {
                    Filename filename = new Filename(s.substring(6));
                    SendFile sendFile;
                    if ((sendFile = SendFile.init(filename)) != null) {
                        if (fileToSend.isEmpty()){
                            fileToSend.add(sendFile);
                            serviceTCP.sendFIL(sendFile.getFileLength(), filename, sendFile.getPort());
                        }
                        else{
                            fileToSend.add(sendFile);
                        }
                    }
                } catch (WrongFormatException e) {
                    System.out.println(e);
                }
            }
            else if (!s.isEmpty()){
                try {
                    Length l = new Length(s.length());
                    Seqchars seqchars = new Seqchars(s);
                    serviceTCP.sendMSG(l, seqchars);
                } catch (WrongFormatException e) {
                    System.out.println(e);
                }
            }
        }
    }

    /**
     * Crée le serveur TCP et le serveur UDP
     * @param user nom de l'utilisateur
     * @throws WrongFormatException
     */
    private void initSession(User user) throws WrongFormatException {
        Session.localUser = user;
        Port port = null;
        try {
            ServerSocket s = new ServerSocket(0);
            port = new Port(s.getLocalPort());
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Session.localPort = port;
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
                    udpServer = new UDPServer();
                    udpServer.listeningMessage();
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
                serviceTCP = new ServiceTCP(Session.localPort);
                serviceTCP.initService();
            }
        }.start();
    }

    /**
     * Ferme la session
     */
    public static void closeSession(){
        try {
            if (udpServer != null) {
                udpServer.sayGoodBye();
                if (serviceTCP != null) {
                    System.out.println("Service TCP closed.");
                    serviceTCP.close();
                    etat = Etat.Disconnected;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion...");
        }
    }

    private static void fillAddressList() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();

        while(e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while(ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                Session.addressList.add(i.getHostAddress());
            }
        }
    }

    /**
     * affiche le message
     * @param msg le message
     */
    public static void receiveMessage(String msg){
        System.out.println(uConv + " : " + msg);
    }

    /**
     * Envoi le fichier en premier dans la liste
     */
    public static void receiveACK(){
        SendFile s = fileToSend.pop();
        s.start();
        if(!fileToSend.isEmpty()){
            SendFile next = fileToSend.getFirst();
            serviceTCP.sendFIL(next.getFileLength(), next.getFilename(), next.getPort());
        }
    }

    /**
     * Elimine le premier fichier de la file d'attente
     */
    public static void receiveNAK(){
        SendFile s = fileToSend.pop();
        if(!fileToSend.isEmpty()){
            SendFile next = fileToSend.getFirst();
            serviceTCP.sendFIL(next.getFileLength(), next.getFilename(), next.getPort());
        }
        System.out.println("Send of " + s.getFilename() + " has been reject.");

    }

    /**
     * Recois une proposition d'echange de fichier
     * @param fileLength la taille du fichier
     * @param filename le nom du fichier
     * @param port le port ou recuperer le fichier
     * @param machine la machine d'ou provient le fichier
     */
    public static void receiveFIL(FileLength fileLength, Filename filename, Port port, Machine machine ){
        System.out.println("Tentative d'envoi de " + filename + " : " +
                "\n - /accept pour accepter " +
                "\n - /reject pour refuser");
        FileWL file = new FileWL();
        file.fileLength = fileLength;
        file.filename = filename;
        file.port = port;
        file.machine = machine;

        fileToReceive.add(file);
    }

    /**
     * Recois un appel
     * @param machine la machine de celui qui appel
     */
    public static void receiveCall(Machine machine){
        //LoggedUser loggedUser = Session.loggedUsers.getLoggedUserByFromAddress(machine);

        try {
            uConv = new User("Locuteur");
        }catch(Exception e){ e.printStackTrace(); }
        System.out.println(uConv + " à établie une conversation privée avec vous.");

        etat = Etat.Conversation;
    }

    /**
     * Ferme la conversation
     */
    public static void receiveCLO(){
        etat = Etat.Connected;
        System.out.println(uConv + " a fermé la conversation.");
    }

}
