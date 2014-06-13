package Services;

import TI.TextualInterface;
import formats.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceTCP {
    private ServerSocket serverSocket;
    private Socket socketConversation;
    private int port;
    private boolean isInConversation = false;

    /** Instancie, initialise et met en marche le service de messagerie TCP */
    public ServiceTCP(int port){
        socketConversation = null;
        this.port = port;
    }
    public ServiceTCP(Port p){
        this(Integer.parseInt(p.toString()));
    }

    public void initService() {
        initServerSocket();
    }

    /** Initialise la serveur socket et attend une connection entrante */
    private synchronized void initServerSocket(){
        if(!isInConversation) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    //Creation de la socket
                    try {
                        serverSocket = new ServerSocket(port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Debut de l'attente de connexion, qui peut etre interrompue lorsque l'on demande
                    //au serveur de debuter une conversation
                    try {
                        socketConversation = serverSocket.accept();
                        //Connexion d'un client
                        Machine aClient = new Machine(socketConversation.getInetAddress().getHostAddress());
                        TextualInterface.receiveCall(aClient);

                        beginConversation();
                    } catch (Exception e) {
                        System.out.println("Mise en pause de l'attente de connexion entrante");
                    }

                }
            });
            t.start();
        }
    }

    public void beginConversation () throws IOException{
        serverSocket.close(); //On stoppe l'attente de connexion
        isInConversation = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isInConversation){
                    try {
                        BufferedReader bf = new BufferedReader(new InputStreamReader(socketConversation.getInputStream()));
                        if(!readMessage(bf)){
                            break;
                        }
                    }catch(Exception e) { e.printStackTrace(); }
                }
            }
        });
        t.start();
    }

    /** Met fin à l'attente Connecte le service au service de messagerie TCP identifié par "machine" et "port" **/
    public void connectionToUser(String machine, int port){
        if(!isInConversation) {
            try {
                socketConversation = new Socket(machine, port);
                beginConversation();
            } catch (Exception e) {
                TextualInterface.etat = TextualInterface.Etat.Connected;
                System.out.println("L'utilisateur est occupé.");
            }
        }else{
            System.err.println("Une conversation est déjà en cours");
        }
    }

    public void connectionToUser(Machine machine, Port port){
        connectionToUser(machine.toString(), Integer.parseInt(port.toString()));
    }

    /** Ferme la conversation sans envoyer de message CLO et relance l'attente de connexion,
     *  à utiliser dans le cas de la reception d'un message CLO **/
    public void closeConversation(){
        if(socketConversation != null) {
            try {
                socketConversation.getInputStream().close();
                socketConversation.close();
                socketConversation = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isInConversation = false;
        initServerSocket();
    }

    /** Ferme proprement le service TCP **/
    public synchronized void close(){
        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
            if (socketConversation != null && !socketConversation.isClosed())
                socketConversation.close();
        }catch(Exception e){ e.printStackTrace(); }
    }

    /** Retourne true si le service est en conversation **/
    public boolean isInConversation() {
        return isInConversation;
    }

    private boolean readMessage(BufferedReader bf){
        String entete = readNChar(bf, 3);

        if(entete.equals("MSG")){
            readNChar(bf, 1);//Sauter le char d'espacement

            int l = readLength(bf);
            if(l != 0) {
                readNChar(bf, 1);//Sauter le char d'espacement

                String msg = readNChar(bf, l);

                //System.out.println("MSG detecté ; Longueur : \" + l ; Message : " + msg);
                TextualInterface.receiveMessage(msg);
            }
        }
        else if (entete.equals("FIL")){
            //System.out.println("FIL");
            readNChar(bf, 1);//Sauter le char d'espacement

            //int filelength = readFileLength(bf);//Cette fonction saute deja le caractere d'espacement

            try {
                FileLength fileLength = new FileLength(readFileLength(bf));
                String fn = readNChar(bf, 40);
                Filename filename = new Filename(fn);
                readNChar(bf, 1);//Sauter le char d'espacement
                Machine machine = new Machine(socketConversation.getInetAddress().getHostAddress());

                try{
                    Port p = new Port(readNChar(bf, 5));
                    TextualInterface.receiveFIL(fileLength, filename, p, machine);
                    //System.out.println("FIL detecté ; fl=" + filelength + " ; filename = " + filename + " ; port = " + p);
                }catch(Exception e){ System.err.println("Port reçu au mauvais format."); }
            }catch(WrongFormatException e){ System.err.println("Filelength reçu au mauvais format."); }
        }
        else if (entete.equals("ACK")){
            TextualInterface.receiveACK();
        }
        else if (entete.equals("NAK")){
            TextualInterface.receiveNAK();
        }
        else if (entete.equals("CLO")){
            //System.out.println("Reception d'un message CLO : Fermeture de la conversation");
            TextualInterface.receiveCLO();
            this.closeConversation();
        }
        else{
            //Si on est en conversation et que la Socket recoit des messages non coherent, on arrete la conversation
            if(isInConversation) {
                System.out.println("Reception d'un messages vide ou invalide. Fermeture de la conversation.");
                TextualInterface.receiveCLO();
                this.closeConversation();
                return false;
            }
            //Si la socket recoit des message non coherent sans être en conversation, c'est que
            // l'utilisateur a fermé la socket et que l'InputStream recoit un flux indefini
        }
        return true;
    }

    private int readLength(BufferedReader bf){
        int l = 0;
        String s = readNChar(bf, 3);
        try{
            l = Integer.parseInt(s);
        }catch(Exception e) {
            System.err.println("Longueur du message recu non valide. Fermeture de la conversation");
            TextualInterface.receiveCLO();
            this.closeConversation();
        }
        return l;
    }

    private int readFileLength(BufferedReader bf){
        int l = 0;
        String tmp = readNChar(bf, 1);
        String n = "";

        while(tmp.matches("[-+]?\\d*\\.?\\d+")){
            n += tmp;
            tmp = readNChar(bf, 1);
        }

        try{
            l = Integer.parseInt(n);
        }catch(Exception e) { e.printStackTrace(); }

        return l;
    }

    /** Lis "n" char dans le flux de "bf" et renvoie le String associé **/
    private String readNChar(BufferedReader bf, int n){
        char[] t = new char[n];
        try {
            bf.read(t, 0, n);
        }catch(Exception e) { /* e.printStackTrace();*/ }
        return String.valueOf(t);
    }

    /** Envoie un message de type MSG **/
    public void sendMSG(Length l, Seqchars s){
        writeInOutputStream("MSG " + l + " " + s);
    }

    /** Envoie un message de type FIL **/
    public void sendFIL(FileLength fl, Filename fn, Port p){
        writeInOutputStream("FIL " + fl + " " + fn + " " + p);
    }

    /** Envoie un message ACK **/
    public void sendACK(){
        writeInOutputStream("ACK");
    }

    /** Envoie un message NAK **/
    public void sendNAK(){
        writeInOutputStream("NAK");
    }

    /** Envoie un message de type CLO ET ferme la conversation **/
    public void sendCLO(){
        writeInOutputStream("CLO");
        closeConversation();
    }

    /** Envoie la chaine de caractere st dans le flux sortant **/
    private void writeInOutputStream(String st){
        if(socketConversation != null) {
            try {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(socketConversation.getOutputStream()));
                pw.print(st);
                pw.flush();
            } catch (Exception e) { e.printStackTrace(); }
        }else
            System.err.println("Operation impossible : fil de conversation inexistant");
    }

    public static void main(String[] arg){
        ServiceTCP t = new ServiceTCP(60000);
    }
}
