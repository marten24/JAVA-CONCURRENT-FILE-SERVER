package CodiceV2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static ArrayList<FileBox> files;
    private static ServerSocket listener;
    private static Socket s;

    public static void main(String args[]) {
        if (args.length < 1) {
            System.err.println("Usage: java Server <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        //carico i file su arraylist files contenente tutti i file.txt
        loadFiles();
        try {
            //creo listener per connessione con client
            listener = new ServerSocket(port);
            //Avvio thread per i comandi del server
            Thread serverCommandThread = new Thread(new ServerCommands());
            serverCommandThread.start();

            //Ciclo di vita del thread principale del server
            while (true) {
                System.out.println("Listening...");
                s = listener.accept(); //Connettiti a un client
                System.out.println("Connected");
                //Delega la gestione della nuova connessione a un thread ClientHandler dedicato
                ClientHandler ch = new ClientHandler(s, files, "userName");
                Thread clientHandlerThread = new Thread(ch);
                clientHandlerThread.start();
                // E rimettiti in ascolto
            }
        } catch (IOException e) {
            System.err.println("Error in I/O.");
        }
    }

    //carica i file di testo presenti nella cartella “data” ciclando su tutti i file e aggiungendoli in un ArrayList
    public static ArrayList<FileBox> loadFiles() {
        File folder = new File("C:/Users/Marco/IdeaProjects/Magghiu/data/");
        files = new ArrayList<>();
        for (File f : folder.listFiles()) {
            if (!f.isHidden()) {
                try {
                    String fileNameToLoad = (f.getName()).replace(".txt", "");
                    FileBox fileToLoad = new FileBox(fileNameToLoad, true);
                    files.add(fileToLoad);
                } catch (IOException e) {
                    System.err.println("Unable to fetch file name");
                    e.printStackTrace();
                }
            }
        }
        return files;
    }

    //mostra informazioni relative al Server
    public static void showInfo() {
        System.out.println("####  SERVER INFO  ####");
        System.out.println("Number of files: " + files.size());
        System.out.println("Number of writers: " + countAw());
        System.out.println("Number of readers: " + countAr());
        System.out.println("Number of blocked readers: " + countBr());
        System.out.println("Number of blocked writers: " + countBw());
    }

    private static int countAr() {
        int cont = 0;
        for (FileBox f : files) {
            cont = cont + f.handler.getAr();
        }
        return cont;
    }

    private static int countAw() {
        int cont = 0;
        for (FileBox f : files) {
            cont = cont + f.handler.isEdited();
        }
        return cont;
    }

    private static int countBr() {
        int cont = 0;
        for (FileBox f : files) {
            cont = cont + f.handler.getBr();
        }
        return cont;
    }

    private static int countBw() {
        int cont = 0;
        for (FileBox f : files) {
            cont = cont + f.handler.getBw();
        }
        return cont;
    }

    //metodo per chiudere Client aperti e Server
    public static void closeServer() throws IOException {  //molla tutti i lock
            if(s!=null){
            s.shutdownInput();
             }
        Client.closeClient();
        ClientHandler.broadcastQuit();
        if (listener != null) {
            try { 
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

