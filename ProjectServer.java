package Fall2013Project;

/**
 * Sean Vincent
 * Computer Science II
 * Semester Project
 * Fall 2013
 */

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class ProjectServer extends JFrame
{
    private int numClients = 0;
    private int count = 0;
    
    private JTextArea jta = new JTextArea();
    private Socket[] clients;
    private JButton jbtOk = new JButton("Ok");
    private JPanel cns = new JPanel();
    private JPanel dis = new JPanel();
    private JTextField jtf = new JTextField(5);
    
    public static void main(String[] args)  
    {
        ProjectServer projectServer = new ProjectServer();
    }
    
    class OkListener implements ActionListener
    {
        @Override
        //Listen for button press
        public void actionPerformed(ActionEvent e)
        {
            if(numClients == 0)
            {
                numClients = Integer.parseInt(jtf.getText());
                
                clients = new Socket[numClients];

                jta.append("Server start at " + new Date() + '\n');
            }
            
            if(numClients < 0 || numClients > 5)
            {
                System.out.println("The number of clients " + 
                        "must be between 1 and 5.");
                
                System.exit(0);
            }
        }
    }
    
    public ProjectServer()
    {
        //Create Panel
        cns.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        cns.add(new JLabel("Multi-Client Chat Room -"));
        cns.add(new JLabel("Enter the number of clients (Max 5)"));
        cns.add(jtf);
        cns.add(jbtOk);
        
        //Create Panel
        dis.setLayout(new BorderLayout(5, 10));
        dis.add(new JScrollPane(jta), BorderLayout.CENTER);
        
        //Create GUI Frame
        setLayout(new BorderLayout());
        add(cns, BorderLayout.NORTH);
        add(dis, BorderLayout.CENTER);
        setTitle("Server");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        OkListener listener1 = new OkListener();
        jbtOk.addActionListener(listener1);
        
        try
        {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            
            // Number a client
            int clientID = 1;
            
            while(true)
            {
                // Listen for a new connection request
                Socket clientSocket = serverSocket.accept();
                clients[count] = clientSocket;
                count++;
                
                // Display the client number
                jta.append("Client " + clientID + " connected on " 
                        + new Date() + '\n');
                
                // Find the client's Host Name and IP address
                InetAddress clientAddress = clientSocket.getInetAddress();
                jta.append("Host name for client " + clientID + " is: " 
                        + clientAddress.getHostName() + '\n');
                jta.append("IP Address for client " + clientID + " is: " 
                        + clientAddress.getHostAddress() + '\n');
                
                PrintWriter outID = new PrintWriter(
                        clientSocket.getOutputStream(), true);
                outID.println("Client" + clientID);
                
                // Create a new thread for the connection
                NewClient task = new NewClient(clientSocket);
                
                // Start the new thread
                new Thread(task).start();
                
                // Increment clientID
                clientID++;
            }
        }
        catch(IOException e)
        {
            System.out.println("Exception caught when trying to listen on port "
                + "or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    
    // Inner class
    // Define the thread class for handling new connection
    class NewClient implements Runnable
    {
        private Socket cSocket;
        
        /** Construct a thread */
        public NewClient(Socket clientSocket)
        {
            this.cSocket = clientSocket;
        }
        
        @Override
        /** Run a thread */
        public void run()
        {
            try
            {
                PrintWriter out = new PrintWriter(
                        cSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(cSocket.getInputStream()));
                
                // Continuously serve the client
                while(true)
                {
                    //string to hold client input
                    String inputLine;
                    
                    //identify the client
                    InetAddress inet = cSocket.getInetAddress();

                    //while string has next
                    while((inputLine = in.readLine()) != null)
                    {
                        //set flag to catch forbidden word
                        boolean clean = true;//set flag to catch forbidden word

                        //split input into words and store into an array
                        String[] word = inputLine.split("\\s+");

                        //scan array 
                        for(int i = 0; i < word.length; i++)
                        {
                            //flag if words are detected
                            if(word[i].equals("damn") || word[i].equals("ugly") 
                                    || word[i].equals("bad"))
                            {
                                clean = false;
                            }
                        }

                        //if code is clean, send it back to client
                        if(clean)
                        {
                            for(int i = 0; i < numClients; i++)
                            {
                                PrintWriter outAll = new PrintWriter(
                                        clients[i].getOutputStream(), true);
                                outAll.println(inputLine);
                            }
                        }

                        //if forbidden word is detected
                        else
                        {
                           //append detection to server
                           jta.append("Message sent at " + new Date() + '\n');
                           jta.append("forbidden word detected\n");
                           jta.append("hostname: " + inet.getHostName() + '\n');
                           jta.append("host address: " + inet.getHostAddress() + 
                                   '\n');
                           //send message to client
                           out.println("forbidden word detected");
                        }
                    }
                }
            }
            catch(IOException ex)
            {
                System.err.println(ex);
            }
        }
    }
}