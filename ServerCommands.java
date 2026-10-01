package CodiceV2;

import java.io.IOException;
import java.util.Scanner;

public class ServerCommands implements Runnable {
    Scanner fromConsole;

    @Override
    public void run() { 
        fromConsole = new Scanner(System.in);
        while (true) {
            String command = "init"; //stringa iniziale
            try {
                command = fromConsole.nextLine(); //leggo da console
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (command.equals("info")) {
                Server.showInfo(); //metodo
            } else if (command.equals("quit")) {
                System.err.println("Server closed.");
                try {
                   //ClientHandler.broadcastQuit();
                    Server.closeServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fromConsole.close();
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }
}
