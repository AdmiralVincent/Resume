package Project3;

/**
 * Sean Vincent (sxv126330) & Mhd Muaz Dada (mxd132730)
 * CS 4348.501
 * Project 3 - Sockets
 * 12/6/14
 */

// List of imports
import java.io.*;
import java.net.*;
import java.util.*;

// Main class
public class Project3Server 
{
    // List of declared variables
    private int x = 0;
    private int y = 0;
    private int port = 0;
    
    // Declaring the files and the data array
    private File data;
    private File imageFolder;
    private String[][] weather;
    
    // List of constant variables
    private int city = 0;
    private int temp = 1;
    private int wind = 2;
    private int pics = 3;
    
    // Main method
    public static void main(String[] args)
    {
        Project3Server project3Server = new Project3Server();
        
        project3Server.ProjectServer();
        
        // Check for command line arguments and call ProjectServer method
        /*
        if(args.length != 0)
        {
            project3Server.port = Integer.parseInt(args[0]);
            
            project3Server.ProjectServer();
        }
        else
        {
            System.out.println("A port number is needed " + 
                    "in order to start the server.");
            
            System.exit(1);
        }
        */
    }
    
    // ProjectServer Method
    public void ProjectServer()
    {
        try
        {
            // Create the socket on port number
            //ServerSocket serverSocket = new ServerSocket(port);
            ServerSocket serverSocket = new ServerSocket(8000);
            
            //System.out.println("Server Running on Port " + port);
            System.out.println("Server Running on Port " + 8000);
            
            x = 0;
            y = 0;
            
            // Opening the input file
            data = new File("Project3_Input.txt");
            
            // Check validity of file
            if(data.exists() && data.isFile())
            {
                // Set length of data array
                weather = new String[(int)data.length()][(int)data.length()];
                
                // Create scanner for file input
                Scanner input = new Scanner(data);
                
                // Read in file to the data array
                while(input.hasNext())
                {
                    if(y < 4)
                    {
                        weather[x][y] = input.next();

                        y++;
                    }
                    else
                    {
                        x++;
                        y = 0;
                    }
                }
                
                System.out.println("Input file has been read.");
            }
            else
            {
                System.out.println("Input file was not found.");
                
                System.exit(1);
            }
            
            int clientID = 1;
            
            // Continuously listen for connections to the socket
            while(true)
            {
                // Accept client connection to the server over the socket
                Socket clientSocket = serverSocket.accept();
                
                System.out.println("Client " + clientID 
                        + " connected on " + new Date());
                
                InetAddress clientAddress = clientSocket.getInetAddress();
                System.out.println("Host name for client " 
                        + clientID + " is: " 
                        + clientAddress.getHostName());
                System.out.println("IP Address for client " 
                        + clientID + " is: " 
                        + clientAddress.getHostAddress());
                
                // Create a new thread for the connection
                NewClient task = new NewClient(clientSocket);
                
                // Start the new thread
                new Thread(task).start();
                
                clientID++;
            }
        }
        catch(IOException e)
        {
            System.out.println("Exception caught when trying to " 
                + "listen on port or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    
    // Subclass NewClient
    class NewClient implements Runnable
    {
        private Socket cSocket;
        
        // Link to socket
        public NewClient(Socket clientSocket)
        {
            this.cSocket = clientSocket;
        }
        
        @Override
        // Start running the thread
        public void run()
        {
            try
            {
                // Create PrintWriter OutputStream on the socket
                PrintWriter out = new PrintWriter(
                        cSocket.getOutputStream(), true);
                // Create BufferedReader InputStream on the socket
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                cSocket.getInputStream()));
                // Create DataOutputStream on the socket
                DataOutputStream dos = new DataOutputStream(
                        cSocket.getOutputStream());
                
                // List of declared variables
                String pic = null;
                String inputLine;
                String location = null;
                
                int a = 0;
                int pos = 0;
                
                // Declaring the file array, and the byte array
                File[] imageList;
                boolean inFile = false;
                byte[] buffer;
                
                // Opening the folder containing the images
                imageFolder = new File("Images");
                
                System.out.println("Now Serving New Client");
                
                // Continuously serve the clients
                while(true)
                {
                    // Wait for client request over the InputStream
                    while((inputLine = in.readLine()) != null)
                    {
                        // Evaluate client input
                        switch(inputLine)
                        {
                            // If "city", then
                            case "city": 
                                System.out.println("List of cities requested.");
                                
                                // Send the strings of city names to the client
                                // over the PrintWriter OutputStream, and flush
                                while(weather[a][city] != null)
                                {
                                    out.println(weather[a][city]);
                                    out.flush();
                                    
                                    a++;
                                }
                                
                                a = 0;
                                
                                // Send acknowledgment string to the client
                                // over the PrintWriter OutputStream, and flush
                                out.println("false");
                                out.flush();
                                
                                // Wait for client choice from the InputStream
                                // and store the sent string
                                while(true)
                                {
                                    if((inputLine = in.readLine()) != null)
                                    {
                                        location = inputLine;
                                        
                                        break;
                                    }
                                }
                                
                                System.out.println("List of cities sent.");
                                
                                break;
                            // If "temp", then
                            case "temp": 
                                System.out.println("Temperature at " + 
                                        location + " requested.");
                                
                                // Send the temperature string for the saved 
                                // location to the client
                                // over the PrintWriter OutputStream, and flush
                                while(weather[a][city] != null)
                                {
                                    if(weather[a][city].equals(location))
                                    {
                                        out.println(weather[a][temp]);
                                        out.flush();
                                        
                                        break;
                                    }
                                    
                                    a++;
                                }
                                
                                a = 0;
                                
                                // Send acknowledgment string to the client
                                // over the PrintWriter OutputStream, and flush
                                out.println("false");
                                out.flush();
                                
                                System.out.println("Temperature at " + 
                                        location + " sent.");
                                
                                break;
                            // If "wind", then
                            case "wind": 
                                System.out.println("Wind speed and " + 
                                        "direction at " + location + 
                                        " requested.");
                                
                                // Send the wind string for the saved 
                                // location to the client
                                // over the PrintWriter OutputStream, and flush
                                while(weather[a][city] != null)
                                {
                                    if(weather[a][city].equals(location))
                                    {
                                        out.println(weather[a][wind]);
                                        out.flush();
                                        
                                        break;
                                    }
                                    
                                    a++;
                                }
                                
                                a = 0;
                                
                                // Send acknowledgment string to the client
                                // over the PrintWriter OutputStream, and flush
                                out.println("false");
                                out.flush();
                                
                                System.out.println("Wind speed and " + 
                                        "direction at " + location + " sent.");
                                
                                break;
                            // If "pics", then 
                            case "pics": 
                                System.out.println("Image of " + location + 
                                        " requested.");
                                
                                // Check validity of file folder and either 
                                // store files in the file array, or send 
                                // error message to the client through the 
                                // PrintWriter over the socket and flush the
                                // OutputStream
                                if(imageFolder.exists() && 
                                        imageFolder.isDirectory())
                                {
                                    imageList = new File[
                                            (int)imageFolder.length()];
                                    
                                    imageList = imageFolder.listFiles();
                                }
                                else
                                {
                                    out.println("Directory of images " + 
                                            "is not available.");
                                    out.flush();
                                    
                                    out.println("false");
                                    out.flush();
                                    
                                    System.out.println("Image of " + location + 
                                            " not available.");
                                    
                                    break;
                                }
                                
                                // Compare saved location to image names
                                // and save the match
                                while(weather[a][city] != null)
                                {
                                    if(weather[a][city].equals(location))
                                    {
                                        pic = weather[a][pics];
                                        
                                        break;
                                    }
                                    
                                    a++;
                                }
                                
                                a = 0;
                                
                                // Compare saved image name 
                                // to file names in the file array
                                for(int b = 0; b < imageList.length; b++)
                                {
                                    if((imageList[b].getName()).equals(pic))
                                    {
                                        inFile = true;
                                        pos = b;
                                    }
                                }
                                
                                // If image file exists
                                if(inFile == true)
                                {
                                    // Set byte array to the size of 
                                    // the image file
                                    buffer = new byte[
                                            (int)imageList[pos].length()];
                                    
                                    // Create FileInputStream for the image file
                                    FileInputStream fis = new FileInputStream(
                                            imageList[pos]);
                                    
                                    // Read the image file into the byte array
                                    // through the FileInputStream
                                    fis.read(buffer);
                                    
                                    // Send signal string to the client over 
                                    // the PrintWriter on the socket 
                                    // and flush the OutputStream
                                    out.println("true");
                                    out.flush();
                                    
                                    // Send image file size to the client
                                    // through the DataOutputStream on the 
                                    // socket, and flush
                                    dos.writeInt((int)imageList[pos].length());
                                    dos.flush();
                                    
                                    // Wait for client acknowledgment string 
                                    // from the BufferedReader InputStream
                                    while(!"OK1".equals(inputLine))
                                    {
                                        inputLine = in.readLine();
                                    }
                                    
                                    // Send byte array to the client though the
                                    // DataOutputStream on the socket, and flush
                                    dos.write(buffer, 0, buffer.length);
                                    dos.flush();
                                    
                                    // Wait for client acknowledgment string 
                                    // from the BufferedReader InputStream
                                    while(!"OK2".equals(inputLine))
                                    {
                                        inputLine = in.readLine();
                                    }
                                    
                                    // Send acknowledgment string to the client
                                    // over the PrintWriter on the socket, and 
                                    // flush the OutputStream
                                    out.println(pic + " has been sent." + '\n');
                                    out.flush();
                                    
                                    System.out.println("Image of " + location + 
                                            " sent.");
                                }
                                
                                // If image does not exist, then send error 
                                // message to the client over the PrintWriter 
                                // on the socket, and flush the OutputStream
                                if(inFile == false)
                                {
                                    out.println("There is no image available " + 
                                            "for this city.");
                                    out.flush();
                                    
                                    System.out.println("Image of " + location + 
                                            " not available.");
                                }
                                
                                // Send acknowledgment string to the client
                                // over the PrintWriter on the socket, and 
                                // flush the OutputStream
                                out.println("false");
                                out.flush();
                                
                                break;
                            default: 
                                System.out.println(
                                        "Invalid message from socket.");
                                System.exit(1);
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
