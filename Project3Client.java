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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// Main class
public class Project3Client 
{
    // List of declared variables
    private int x = 0;
    private int port = 0;
    private int list = 0;
    private int bufflen = 0;
    private String name = null;
    private String choice = null;
    private String location = null;
    private String inputLine = null;
    
    // Declaring the socket, file, and byte array
    Socket socket;
    private File image;
    byte[] buffer = null;
    
    // ArrayList for storing the list of cities
    ArrayList<String> cities = new ArrayList<>();
    
    // Creating scanner for user input
    Scanner input = new Scanner(System.in);
    
    PrintWriter out;
    
    // Main method
    public static void main(String[] args)
    {
        try
        {
            Project3Client project3Client = new Project3Client();
            
            project3Client.ProjectClient();

            // Checking for command line arguments 
            // and calling ProjectClient method
            /*
            if(args[0] != null && args[1] != null)
            {
                if("CS1".equals(args[0]))
                {
                    project3Client.name = "cs1.utdallas.edu";
                }
                else if("CS2".equals(args[0]))
                {
                    project3Client.name = "cs2.utdallas.edu";
                }
                else
                {
                    System.out.println("The only valid server " +
                            "names are CS1 and CS2." + '\n');

                    System.exit(1);
                }

                project3Client.port = Integer.parseInt(args[1]);
                
                project3Client.ProjectClient();
            }
            else
            {
                System.out.println("Both a Server name and a " +
                        "Port number are needed in order to " +
                        "connect with a server." + '\n');

                System.exit(1);
            }
            */
        }
        catch(IOException e)
        {
            System.out.println("exc in client");
        }
    }
    
    // ProjectClient Method
    private void ProjectClient() throws IOException
    {
        // Creating the socket on the host name and port number
        //socket = new Socket(name, port);
        socket = new Socket("localhost", 8000);
        
        /*
        System.out.println("Client Connected to " + name + 
                " on Port " + port + '\n');
        */
        
        System.out.println("Client Connected to " + "localhost" + 
                " on Port " + 8000 + '\n');
        
        // Opening the PrintWriter OutputStream on the socket
        out = new PrintWriter(socket.getOutputStream(), true);
        
        x = 0;
        
        // Continuously ask for input
        while(true)
        {
            // Display menu and request user input
            System.out.println("Choose from the following list " + 
                                "of commands:");
            System.out.println("A. Select a Location");
            System.out.println("B. Get Temperature at Selected " + 
                                "Location");
            System.out.println("C. Get Wind Direction and Speed " + 
                                "at Selected Location");
            System.out.println("D. Get Image from Selected " + 
                                "Location");
            System.out.println("E. Exit");
            System.out.print("Enter Choice: ");
            
            choice = input.nextLine();
            
            // Evaluate user input
            switch(choice)
            {
                // If 'A', then call city method
                case "A": 
                    city();
                    
                    break;
                // If 'B', then call temperature method
                case "B": 
                    if(location == null)
                    {
                        System.out.println("You must select " + 
                                "a location first." + '\n');
                        
                        break;
                    }
                    
                    temp();
                    
                    break;
                // If 'C', then call wind method
                case "C": 
                    if(location == null)
                    {
                        System.out.println("You must select " + 
                                "a location first." + '\n');
                        
                        break;
                    }
                    
                    wind();
                    
                    break;
                // If 'D', then call picture method
                case "D": 
                    if(location == null)
                    {
                        System.out.println("You must select " + 
                                "a location first." + '\n');
                        
                        break;
                    }
                    
                    pics();
                    
                    break;
                // If 'E', then end client program
                case "E": 
                    //System.out.println("Disconnected from " + name + '\n');
                    System.out.println("Disconnected from " + "localhost" + '\n');
                    socket.close();
                    System.exit(1);
                    break;
                default: 
                    System.out.println("Your input must be " 
                        + "a single capital letter A-E." + '\n');
                    break;
            }
        }
    }
    
    // City method
    private void city() throws IOException
    {
        // Create BufferedReader InputStream on the socket
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        
        // Send string "city" over the PrintWriter to the server
        // to request a list of cities and flush the OutputStream
        out.println("city");
        out.flush();
                    
        // Wait for a response
        while(true)
        {
            // Read in response from the server over the InputStream, add 
            // cities to arraylist, and print them to the screen
            if((inputLine = in.readLine()) != null)
            {
                if("false".equals(inputLine))
                {
                    break;
                }
                            
                cities.add(inputLine);
                            
                x++;
                            
                System.out.println(x + ": " + inputLine);
            }
        }
                    
        x = 0;
                    
        // Request user input
        System.out.print("Enter Choice: ");
                    
        list = input.nextInt();
                    
        while(cities.get(list - 1) == null)
        {
            System.out.print('\n' + "You must select a number from " + 
                    "the above list: ");
                        
            list = input.nextInt();
        }
                    
        // Set location based on user input
        location = cities.get(list - 1);
                    
        // Send string location over the PrintWriter to the server
        // to set city for future requests and flush the OutputStream
        out.println(location);
        out.flush();
    }
    
    // Temperature method
    private void temp() throws IOException
    {
        // Create BufferedReader InputStream on the socket
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        
        // Send string "temp" over the PrintWriter to the server to request 
        // the temperature at the saved location and flush the OutputStream
        out.println("temp");
        out.flush();
                    
        // Wait for a response
        while(true)
        {
            // Read in response from the server over the InputStream, 
            // and print it to the screen
            if((inputLine = in.readLine()) != null)
            {
                if("false".equals(inputLine))
                {
                    break;
                }
                            
                System.out.println("Temperature in " + location + 
                        " is: " + inputLine + '\n');
            }
        }
    }
    
    // Wind method
    private void wind() throws IOException
    {
        // Create BufferedReader InputStream on the socket
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        
        // Send string "wind" over the PrintWriter to the server to request 
        // the wind attributes at the saved location and flush the OutputStream
        out.println("wind");
        out.flush();
                    
        // Wait for a response
        while(true)
        {
            // Read in response from the server over the InputStream, 
            // and print it to the screen
            if((inputLine = in.readLine()) != null)
            {
                if("false".equals(inputLine))
                {
                    break;
                }
                            
                System.out.println("Wind reading in " + location + 
                        " is: " + inputLine + '\n');
            }
        }
    }
    
    // Picture method
    private void pics() throws IOException
    {
        // Create BufferedReader InputStream on the socket
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        
        // Send string "pics" over the PrintWriter to the server to request 
        // the an image of the saved location and flush the OutputStream
        out.println("pics");
        out.flush();
                    
        // Create new image file
        image = new File(location + ".JPG");
        
        // Wait for a response
        while(true)
        {
            // Read in response from the server over the InputStream, 
            // and evaluate the input 
            //(either call file download method or print the input to the screen)
            if((inputLine = in.readLine()) != null)
            {
                if("false".equals(inputLine))
                {
                    break;
                }
                
                if("true".equals(inputLine))
                {
                    fileDL();
                }
                            
                if(!"true".equals(inputLine))
                {
                    System.out.println(inputLine);
                }
            }
        }
    }
    
    // File Download method
    private void fileDL() throws IOException
    {
        // Create DataInputStream and FileOutputStream on the socket
        DataInputStream dis = new DataInputStream(
                socket.getInputStream());
        FileOutputStream fos = new FileOutputStream(image);
        
        // Sleep for a second
        try 
        {
            TimeUnit.SECONDS.sleep(1);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(Project3Client.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        
        // Read in the buffer length from the server over the DataInputStream,
        // and set byte array to that length
        bufflen = dis.readInt();
        buffer = new byte[bufflen];
        
        // Send string "OK1" to the server over the PrintWriter 
        // to siganl acknowledgment and readiness, 
        // and then flush the OutputStream
        out.println("OK1");
        out.flush();
        
        // Sleep for a second
        try 
        {
            TimeUnit.SECONDS.sleep(1);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(Project3Client.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        
        // Read in bytes to the byte array from the server over the 
        // DataInputStream, and write byte array to the new file over the 
        // FileOutputStream
        dis.readFully(buffer);
        fos.write(buffer, 0, buffer.length);
        
        // Send string "OK2" to the server over the PrintWriter 
        // to siganl acknowledgment and readiness, 
        // and then flush the OutputStream
        out.println("OK2");
        out.flush();
    }
}
