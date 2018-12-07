package Client;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Client {

    //CONSTRUCTOR
    private String ServerAddress;
    private int ServerPort;
    private String clientName;
    private int accessNumber;

    // PRINTSTREAM
    private PrintStream clientOutput;

    //SOCKET
    private Socket socket;

    //STREAMS
    private ObjectInputStream dis;
    private ObjectOutputStream dos;

    // COUNT REQUEST
    private int requestCount;

    // Standard constructor used for regular client creation
    public Client(String ServerAddress, int ServerPort, String clientName){
        this.ServerAddress = ServerAddress;
        this.ServerPort = ServerPort;
        this.clientName = clientName;
        accessNumber = new Random().nextInt(300) + 50;

        // Setting text output to default System.out
        clientOutput = new PrintStream(System.out);
        System.setOut(clientOutput);
    }

    // Standard constructor used for regular client creation. Boolean to create client text log output
    public Client(String ServerAddress, int ServerPort, String clientName, boolean allowTextLogs){
        this.ServerAddress = ServerAddress;
        this.ServerPort = ServerPort;
        this.clientName = clientName;
        accessNumber = new Random().nextInt(300) + 50;

        // Create client text logs
        if (allowTextLogs){
            if (new File("logs").exists()) {
                try {
                    clientOutput = new PrintStream("logs/" + clientName + "-log.txt");
                }catch(FileNotFoundException e){
                    clientOutput.println("ERROR: Unable to create client text logs");
                }
            }
            else{
                new File("logs").mkdirs();
                {
                    try {
                        clientOutput = new PrintStream("logs/" + clientName + "-log.txt");
                    }catch(FileNotFoundException e){
                        clientOutput.println("ERROR: Unable to create client text logs");
                    }
                }
            }
        }
    }

    // Constructor used to create clients with a specific access number. Used for testing.
    public Client(String ServerAddress, int ServerPort, String clientName, int accessNumber){
        this.ServerAddress = ServerAddress;
        this.ServerPort = ServerPort;
        this.clientName = clientName;
        this.accessNumber = accessNumber;

        // Setting text output to default System.out
        clientOutput = new PrintStream(System.out);
        System.setOut(clientOutput);
    }

    // Constructor used to create clients with a specific access number. Used for testing. boolean to create client text log output
    public Client(String ServerAddress, int ServerPort, String clientName, int accessNumber, boolean allowTextLogs){
        this.ServerAddress = ServerAddress;
        this.ServerPort = ServerPort;
        this.clientName = clientName;
        this.accessNumber = accessNumber;

        // Create client text logs
        if (allowTextLogs){
            if (new File("logs").exists()) {
                try {
                    clientOutput = new PrintStream("logs/" + clientName + "-log.txt");
                }catch(FileNotFoundException e){
                    clientOutput.println("ERROR: Unable to create client text logs");
                }
            }
            else{
                new File("logs").mkdirs();
                {
                    try {
                        clientOutput = new PrintStream("logs/" + clientName + "-log.txt");
                    }catch(FileNotFoundException e){
                        clientOutput.println("ERROR: Unable to create client text logs");
                    }
                }
            }
        }
    }

    // Connect function. Establish connection with a server and initialize the client.
    public boolean Connect(){
        // Catch if the current client object is connected to a socket.
        try {
            if (socket.isConnected()) {
                clientOutput.println(clientName + " unable to process client connection. Client already connected to a server.");
                return false;
            }
        }
        catch (NullPointerException npe){ }

        String[] sendData;
        String[] receivedData;

        try{
            // Establish connection
            socket = new Socket(ServerAddress, ServerPort);

            // Open streams
            dos = new ObjectOutputStream(socket.getOutputStream());
            dis = new ObjectInputStream(socket.getInputStream());


            // SEND INIT
            clientOutput.println(clientName + " is initializing connection with the server.");
            sendData = new String[]{"init", clientName, String.valueOf(accessNumber)};
            dos.writeObject(sendData);

            // RECEIVE INIT
            receivedData = (String[]) dis.readObject();
            if (receivedData[2].equals("failed")) {
                clientOutput.println(" <" + clientName + " failed to initialize connection with the server. Client name already exists." + ">");
                return false;
            }
            else{
                clientOutput.println(" <" + clientName + " successfully initialized a connection with the server. Access number " + accessNumber + ">");
                clientOutput.println(" <" + clientName + " Server Cache: " + receivedData[2] + ">");
                clientOutput.println(" <" + clientName + " requests: " + receivedData[3] + ">");
                if (!receivedData[3].equals("infinite")){
                    requestCount = Integer.valueOf(receivedData[3]);
                }
            }
            return true;
        }
        catch (EOFException e1){
            clientOutput.println(" <" + clientName + " is unable to connect. Server is at max capacity.>");
        } catch (ConnectException e4){
            clientOutput.println(clientName + " is unable to connect. Connection refused.");
        } catch (IOException e2){
            e2.printStackTrace();
        } catch (ClassNotFoundException e3){
            e3.printStackTrace();
        }
        return false;
    }

    // Request function. Request a file from a server with the fileRequest parameter only if the client is connected to a server.
    public boolean Request(String fileRequest){
        // Catch if a client is not connected to a socket
        try {
            if (!socket.isConnected()) {
                clientOutput.println(clientName + " is not connected to a server. Request for file " + fileRequest + " was not processed by the client.");
                return false;
            }
        }
        catch (NullPointerException npe){
            clientOutput.println(clientName + " is not connected to a server. Request for file " + fileRequest + " was not processed by the client.");
            return false;
        }

        // Variables used to differentiate objects
        String[] tempStringTest = new String[]{};
        byte[] tempByteTest = new byte[]{};
        Object tempData;

        // IO Vars
        String[] sendData = null;
        String[] receivedData = null;
        byte[] receivedBytes = null;
        ArrayList<String[]> requestList = null;

        clientOutput.println(clientName + " requesting file: " + fileRequest);

        try{
            // SEND REQUEST
            sendData = new String[]{"request", clientName, fileRequest};
            dos.writeObject(sendData);

            // RECEIVE REQUEST
            tempData = dis.readObject();

            // Handle sent files
            if (tempData.getClass().equals(tempByteTest.getClass())){
                receivedBytes = (byte[]) tempData;
                // temp byte array for the file
                byte[] tempReceivedBytes = new byte[receivedBytes.length-1];

                // Print to client the file was received
                clientOutput.println(" <" + clientName + " received file: " + fileRequest + ">");
                if ((int) receivedBytes[0] == 99) {
                    clientOutput.println(" <" + clientName + " requests left: infinite>");
                }
                else{
                    clientOutput.println(" <" + clientName + " requests left: " + (int) receivedBytes[0] + ">");
                }

                // Put received bits into the temp byte array to create the file from the byte array
                for (int index = 1; index < receivedBytes.length; index++){
                    tempReceivedBytes[index-1] = receivedBytes[index];
                }
                if (new File(clientName + "-files").exists()) {
                    // Write the file in the client folder
                    Files.write(Paths.get(clientName + "-files/" + fileRequest), tempReceivedBytes);
                }
                else{
                    // Write the file in the client folder
                    new File(clientName + "-files").mkdirs();
                    Files.write(Paths.get(clientName + "-files/" + fileRequest), tempReceivedBytes);
                }
                if ((int) receivedBytes[0] == 0){
                    clientOutput.println("<" + clientName + " closing connection with the server. Request quota reached.>");
                    socket.close();
                    return true;
                }
                return true;
            }
            // Check if object is a String[]
            else if (tempData.getClass().equals(tempStringTest.getClass())){
                // Set the receive data as a String[] object
                receivedData = (String[]) tempData;
                // Check if request failed
                if (receivedData[2].equals("failed")) {
                    clientOutput.println(" <" + clientName + " failed to receive file " + fileRequest + ". File does not exist.>");
                    // Check if request quota reached flag has been set
                    try{
                        if (receivedData[4].equals("close")){
                            clientOutput.println("<" + clientName + " closing connection with the server. Request quota reached.>");
                            socket.close();
                            return false;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException aoe){ }
                    return false;
                }
                // Check if failed from insufficient access level
                else if (receivedData[2].equals("failed. insufficient access level")){
                    clientOutput.println(" <" + clientName + " failed to receive file " + fileRequest + ". Insufficient access level>");
                    // Check if request quota reached flag has been set
                    try{
                        if (receivedData[4].equals("close")){
                            clientOutput.println("<" + clientName + " closing connection with the server. Request quota reached.>");
                            socket.close();
                            return false;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException aoe){ }
                    return false;
                }
                return false;
            }
            // Check if object is an ArrayList<String[]>
            else {
                requestList = (ArrayList<String[]>) tempData;
                clientOutput.println(" <" + clientName + " Printing Client Usage>");
                clientOutput.println("  <<[Date], [Accept/Fail], [Client Name], [Requested File]>>");
                requestList.forEach((entry) -> {
                    clientOutput.print("  <<" + entry[0] + ", ");
                    clientOutput.print(entry[1] + ", ");
                    clientOutput.print(entry[2] + ", ");
                    clientOutput.println(entry[3] + ">>");
                });
            }
        }
        catch (EOFException eof){
            clientOutput.println(clientName + " is not connected to a server. Request for file " + fileRequest + " failed.");
        }
        catch (SocketException ese){
            clientOutput.println(clientName + " is not connected to a server. Request for file " + fileRequest + " failed.");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Close function. Close a connection with a server manually.
    public boolean Close() {
        if (socket.isConnected()) {
            try{
                socket.close();
                clientOutput.println(clientName + " closed the connection with the server.");
                return true;
            }
            catch (SocketException ese){
                clientOutput.println(clientName + " is not connected to a server. Unable to close a connection");
            }
            catch(Exception e){
                clientOutput.println(clientName + " failed to close connection.");
                return false;
            }
        }
        return false;
    }
}
