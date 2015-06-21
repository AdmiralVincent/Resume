package Fall2013Project;

/**
 * Sean Vincent
 * Computer Science II
 * Semester Project
 * Fall 2013
 */

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProjectClient extends JFrame 
{
    // Text field for receiving text
    JTextField jtf = new JTextField();

    // Text area to display contents
    private JTextArea jta = new JTextArea();
    
    private JButton b1 = new JButton("Send");
    
    int count = 1;
    String getID;
    
    //open socket and set up printWriter and bufferedReader
    Socket socket = new Socket("localhost", 8000);
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));

    public static void main(String[] args) 
    {
        try
        {
            ProjectClient projectClient = new ProjectClient();
        }
        catch(IOException e)
        {
            System.out.println("exc in client");
        }
    }
       
    //action listener for button press
    class SendListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String userInput = jtf.getText().trim();//trim and store text
                
            if(!"".equals(userInput)) 
            {
                out.println(getID + ": " + userInput);//send to server
                out.flush();
                       
                jtf.setText("");//clear text field
            }
            if("".equals(userInput))
            {
                jtf.setText("");//clear text field
            }
        }
    }
    
    private ProjectClient() throws IOException
    {
    	// Set up GUI
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("Enter Message"), BorderLayout.WEST);
        p.add(b1,BorderLayout.EAST );
        p.add(jtf, BorderLayout.CENTER);
        jtf.setHorizontalAlignment(JTextField.RIGHT);
        setLayout(new BorderLayout());
        add(p, BorderLayout.SOUTH);
        add(new JScrollPane(jta), BorderLayout.CENTER);
    
        setTitle("Client");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); 
        
        SendListener listener1 = new SendListener();
        b1.addActionListener(listener1);
        
        while(true)
        {
            if(count == 1)
            {
                getID = in.readLine();
                
                jta.append(getID + '\n');
                
                count++;
                
                continue;
            }
            
            //read input from server
            jta.append(in.readLine() + '\n');
        }
    }
}
