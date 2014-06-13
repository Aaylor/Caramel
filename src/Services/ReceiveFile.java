package Services;

import formats.FileLength;
import formats.Filename;
import formats.Machine;
import formats.Port;

import java.io.*;
import java.net.Socket;

/**
 * Classe de reception des fichiers
 */
public class ReceiveFile extends Thread {

    private final FileLength filelength;
    private final Filename filename;
    private final Port port;
    private final Machine machine;
    private File file;
    private String pathD = System.getProperty("user.home") + "/downloads/";
    private int BUFFSIZE;

    /**
     * Creer l'objet chargé de la réception des fichiers
     * @param filelength taille du fichier a recevoir
     * @param filename nom du fichier a recevoir
     * @param port port sur lequel on recupere le fichier
     * @param machine machine depuis laquelle le fichier est envoyer
     */
    public ReceiveFile(FileLength filelength, Filename filename, Port port, Machine machine) {

        this.filelength = filelength;
        this.filename = filename;
        this.port = port;
        this.machine = machine;

    }

    /**
     * Thread qui recupere le fichier
     */
    synchronized public void run() {
        createFile();
        try {
            Socket socket = new Socket(machine.toString(), port.parseToInteger());
            InputStream inputStream = socket.getInputStream();
            BUFFSIZE = socket.getReceiveBufferSize();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buf = new byte[BUFFSIZE];

            int n;

            while ((n = inputStream.read(buf)) > 0){
                bufferedOutputStream.write(buf, 0, n);
            }

            bufferedOutputStream.flush();
            System.out.println("Réception du fichier terminé.");
            bufferedOutputStream.close();
            fileOutputStream.close();
            inputStream.close();
            socket.close();
            if(file.length() != filelength.toInteger()){
                System.out.println("Warning: taille du fichier reçu différente de la taille annoncé.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Creer le fichier qui sera récupéré et si besoin le répertoire
     */
    private void createFile(){
        File downloads = new File(pathD);
        if(!downloads.exists()){
            System.out.println("Création du répertoire : " + pathD + ".");
            if(!downloads.mkdirs()){
                System.out.println("Echec de la création du répertoire : " + pathD + ".");
            }
            else{
                System.out.println(pathD + " a été créé avec succès.");
            }
        }

        file = new File(pathD + filename.trimmedFilename());
        System.out.println("Chemin absolu du fichier : " + file.getAbsolutePath());

        int n = 1;
        while(file.exists()){
            file = new File(pathD + filename.trimmedFilename() + "(" + n + ")");
            n++;
        }
        try {

            if(!file.createNewFile()){
                System.out.println("Echec de la création du nouveau fichier.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
