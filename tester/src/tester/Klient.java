package tester;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * Klienten til chat-aplikationen.
 * GUI er lavet med Jframe.
 * 
 * 
 * Klienten følger den protokol, vi lavede i vores chatserver klasse.
 * Altså, klienten skal gøre forskellige ting, alt efter om den modtger følgende
 * "kodeord"
 * 
 * BRUGERNAVNACCEPTERET
 * BESKED
 * SENDBRUGERNAVN
 * 
 * 
 * 
 * 
 */

public class Klient {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Soren's Ultimate Chat");
    JTextField textField = new JTextField(80);
    JTextArea messageArea = new JTextArea(16, 80);

    /*
     * 
     * Klientens kunstrøktor, der sætter vores GUI elementers attributter.
     * Herudover opretter den en listener til "textfield", så vi kan sende
     * dens indhold tilbage til serveren. -TExtfield kan først bruges,
     * når klienten er gokendt af serveren.
     * 
     */

    public Klient() {

        // Layout GUI
    	messageArea.setBackground(Color.black);
    	textField.setBackground(Color.black);
    	frame.setBackground(Color.black);
    	
    	messageArea.setForeground(Color.green);
    	textField.setForeground(Color.green);
    	
    	
    	
    	frame.setForeground(Color.green);
    	
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();
        
      

        // Add Listeners
        textField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                
          
                textField.setText("");
          
                
            }
        });
    }


    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Indtast Serverens IP-adresse.",
            "Velkommen",
            JOptionPane.QUESTION_MESSAGE);
    }


    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Indtast brugernavn.",
            "Brugernavn valg",
            JOptionPane.PLAIN_MESSAGE);
    }


    private void run() throws IOException {

      
       String serverAddress = getServerAddress();
       
		Socket socket = new Socket(serverAddress, 2350);
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"), true);
      
        
      
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SENDBRUGERNAVN")) {
                out.println(getName());
            } else if (line.startsWith("BRUGERNAVNACCEPTERET")) {
                textField.setEditable(true);
            } else if (line.startsWith("BESKED")) {
      
                messageArea.append(line.substring(8) + "\n");
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
            }
        }
    }

 
    public static void main(String[] args) throws Exception {
        Klient client = new Klient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}