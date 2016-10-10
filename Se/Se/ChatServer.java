package tester;
// IO imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

// net imports
import java.net.ServerSocket;
import java.net.Socket;

//util imports
import java.util.HashSet;

/**
 * Dette er en multitrådet chat server. Den gør øflgende.
 * 
 * 1. Sender klienten teksten "submitname" og beder om et brugenavn
 * 
 * 2. Når den modtager brugenravn, sender den "Nameaccepted" tilbage
 * 
 * 3. beskeden fra klienten broadcastes til alle andre klienter
 * 
*/

public class ChatServer {


    private static final int PORT = 2350;


    
    /* 
     * HashSet, der holder styr på alle brugernavne i systemet.
     * samt printwriters
     * 
     */
    private static HashSet<String> brugere = new HashSet<String>();

  
    private static HashSet<PrintWriter> printWriters = new HashSet<PrintWriter>();

 
/*
 * 
 * Main metoden, der opetter serverens socket og laver flere tråde.
 * 
 */
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    
    /** 
     * 
     * Handler tråd-klassen, der skal lave flere tråde i et loop 
     * og er ansvarlig for at hver klient kan sende beskeder.
     * 
     */

    private static class Handler extends Thread {
        private String brugernavn;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Konstruktør der setter socket for klienten.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /*
         * 
         * Håndtere denne tråds klient ved at bede om et navn, til et unikt navn er valgt.
         * Derefter godkender den navnet og registrere klientens output stream, så den kan sende beskeder.
         * 
         * 
         * 
         */
  
        public void run() {
            try {

                // Streams for klienten
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), "UTF-8"));
                out = new PrintWriter(socket.getOutputStream(), true);

                /**
                 * Her sender vi beskeder til klienten, så den ved hvad den skal gøre.
                 * 
                 */
                while (true) {
                    out.println("SENDBRUGERNAVN");
                    brugernavn = in.readLine();
                    if (brugernavn == null) {
                        return;
                    }
                    
                    //Synchronized skal bruges hvis flere tråde skal skrive til den samme variabel
                    synchronized (brugere) {
                        if (!brugere.contains(brugernavn)) {
                            brugere.add(brugernavn);
                            break;
                        }
                    }
                }
                
                /**
                 * Efter et navn er godkendt, skal printwriteren til klientens socket
                 * tilføjes til vores hashset af printwriters.
                 * 
                 * 
                 */

                out.println("BRUGERNAVNACCEPTERET");
                printWriters.add(out);

                
                /**
                 * Acceptere eller ignorer klienter, der ikke kan sendes til
                 * 
                 */
               
                
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : printWriters) {
                        writer.println("BESKED  " + brugernavn + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
            	
            	
                /**
                 * I tilfælde af en klients forbindelse lukkes,
                 * skal dens navn og socket fjernes fra systemet. samt dens
                 * printwriter
                 * 
                 * 
                 */
                if (brugernavn != null) {
                    brugere.remove(brugernavn);
                }
                if (out != null) {
                    printWriters.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}