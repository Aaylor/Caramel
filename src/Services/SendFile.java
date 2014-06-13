package Services;


import formats.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe qui envoie des fichiers
 */
public class SendFile extends Thread {

    private Filename filename;
    private File file;
    private FileLength fileLength;
    private Port port;
    private ServerSocket serverSocket;
    private int BUFFSIZE = 2048;

    /**
     * Stock le nom du fichier
     *
     * @param filename le nom du fichier
     */
    public SendFile(Filename filename) {
        this.filename = filename;
    }

    public Port getPort() {
        return port;
    }

    public Filename getFilename() {
        return filename;
    }

    public FileLength getFileLength() {
        return fileLength;
    }

    /**
     * Prepare l'envoie de fichier
     *
     * @param filename appel le constructeur
     * @return renvoie l'objet construit
     */
    public static SendFile init(Filename filename) {
        if (filename == null) {
            return null;
        }

        SendFile sendFile = new SendFile(filename);
        if (!sendFile.initFile()) {
            return null;
        }
        if (!sendFile.createPort()) {
            return null;
        }
        return sendFile;
    }

    /**
     * Thread qui envoie le fichier
     */
    synchronized public void run() {
        FileInputStream fileInputStream = null;
        try {
            Socket socket = serverSocket.accept();
            byte[] buf = new byte[BUFFSIZE];
            fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream((socket.getOutputStream()));

            int n;
            while ((n = fileInputStream.read(buf)) >= 0) {
                bufferedOutputStream.write(buf, 0, n);
            }

            bufferedOutputStream.flush();
            System.out.println("Envoie du fichier terminé.");
            bufferedOutputStream.close();
            bufferedInputStream.close();
            fileInputStream.close();
            socket.close();

        } catch (Exception e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prepare le fichier
     *
     * @return vrai si le fichier existe faux sinon
     */
    private boolean initFile() {
        if (filename != null) {
            file = new File(filename.trimmedFilename());
            /*
            System.out.println("Chemin absolu du fichier : " + file.getAbsolutePath());
            System.out.println("Nom du fichier : " + file.getName());
            System.out.println("Est-ce qu'il existe ? " + file.exists());
            System.out.println("Est-ce un répertoire ? " + file.isDirectory());
            System.out.println("Est-ce un fichier ? " + file.isFile());
            */
            if (!file.isFile()) {
                System.out.println("Erreur : le fichier " + filename.trimmedFilename() + " n'existe pas.");
                return false;
            }
        } else {
            System.out.println("Erreur : aucun nom de fichier fournis.");
            return false;
        }
        try {
            fileLength = new FileLength((int) file.length());
        } catch (WrongFormatException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Crée un port pour l'envoi
     * @return vrai si création du port réussi faux sinon
     */
    private boolean createPort() {
        try {
            serverSocket = null;
            try {
                serverSocket = new ServerSocket(0);
                port = new Port(serverSocket.getLocalPort());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (WrongFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}