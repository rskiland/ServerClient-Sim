// Rob Skiland - 10186568
// CISC 435 - Computer Networks F18
// Course Project
// December 6, 2018

// ServerHandler class
// Threaded java class used to manage client requests. Each client has its own thread.
// Each thread created by Server.java
package Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerHandler extends Thread{
    // ----- Instantiate global vars -----
    // Class private vars
    private Socket socket;
    private ObjectInputStream dis;
    private ObjectOutputStream dos;

    // Shared data structure
    ServerUsage serverUsage;

    // Data format
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    // Get list of files contained in the servers cache
    private static File[] cacheFiles = new File("./cache").listFiles();

    // ----- Server handler thread constructor ------
    public ServerHandler(Socket socket, ObjectInputStream dis, ObjectOutputStream dos, ServerUsage serverUsage){
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
        this.serverUsage = serverUsage;
    }


    // ----- run() function -----
    @Override
    public void run(){
        // Incoming and outgoing holder vars
        String[] receivedData;
        String[] sendData;

        boolean CloseConnection = false;

        // Infinite loop until connection is closed
        while(true){
            try{
                // Store captured object data from client
                receivedData = (String[]) dis.readObject();

                // ----- Initialize client connection -----
                if (receivedData[0].equals("init")) {
                    // Check if client hasn't be initialized
                    if (!serverUsage.containsClient(socket.getRemoteSocketAddress()) && serverUsage.uniqueClientName(receivedData[1])) {
                        String tempQuota;
                        // Get clients request limit from access number
                        if (getClientAccessQuota(receivedData[2]) == 0){
                            tempQuota = "infinite";
                        }
                        else{
                            tempQuota = String.valueOf(getClientAccessQuota(receivedData[2]));
                        }
                        // Insert client data into a shared hashmap
                        serverUsage.putClientData(socket.getRemoteSocketAddress(), new String[]{receivedData[1], receivedData[2], tempQuota});
                        // Put data to be sent in a holder variable
                        sendData = new String[]{"init", receivedData[1], getCacheList(), tempQuota};
                        // Send data to client through object output stream
                        dos.writeObject(sendData);
                        // Write to server that a new client connected with a access number
                        System.out.println("[INIT] " + receivedData[1] + "   [ACCESS] " + receivedData[2]);
                    }
                    // Failed init if client already exists
                    else{
                        // Put data to be sent in a holder variable
                        sendData = new String[]{"init", receivedData[1], "failed"};
                        // Send data to client through object output stream
                        dos.writeObject(sendData);
                        // Write to server a client init failed and close the connection
                        System.out.println("[INIT] " + receivedData[1] + "   [ACCESS] " + receivedData[2]+ " -- FAILED");
                        System.out.println("[SERVER] [INIT] [CLOSE] Failed initializing " + socket.getRemoteSocketAddress() + " as client " + receivedData[1]);
                        serverUsage.removeConnectedUsers();
                        socket.close();
                        break;
                    }
                }

                // ----- Handle client requests -----
                else if (receivedData[0].equals("request")) {
                    // Check if client is initialized
                    if (serverUsage.containsClient(socket.getRemoteSocketAddress())){
                        // Hold client data
                        String[] clientData = serverUsage.getClientData(socket.getRemoteSocketAddress());

                        // Check if requested file exists in cache
                        if (cacheLookup(receivedData[2])) {
                            // decrement client requests
                            decrementClientUsage(clientData);
                            // Get file byte array
                            byte[] fileBytes = Files.readAllBytes(cacheFiles[getCacheListFileIndex(receivedData[2])].toPath());
                            byte sendBytesAccessLevel;
                            //Check if access quota reached
                            if (clientData[2].equals("0")) {
                                // Data to be sent to client store in holder var, add flag to indicate the server is closing the connection
                                sendBytesAccessLevel = 0;
                                CloseConnection = true;
                            }
                            else{
                                // Data to be sent to client store in holder var
                                if (serverUsage.getClientData(socket.getRemoteSocketAddress())[2].equals("infinite")) {
                                    sendBytesAccessLevel = 99;
                                } else{
                                    int holderInt = Integer.valueOf(serverUsage.getClientData(socket.getRemoteSocketAddress())[2]);
                                    sendBytesAccessLevel = (byte) holderInt;
                                }
                            }
                            byte[] mergedFileBytes = new byte[1 + fileBytes.length];
                            mergedFileBytes[0] = sendBytesAccessLevel;
                            // Merged byte arrays
                            for (int index = 1; index < mergedFileBytes.length; index++){
                                mergedFileBytes[index] = fileBytes[index-1];
                            }
                            // Add to request log
                            serverUsage.appendRequestList(new String[]{dateFormat.format(new Date()), "accept", clientData[0], receivedData[2]});
                            // Send data to client through object output stream
                            dos.writeObject(mergedFileBytes);
                            // Write to server
                            System.out.println("[REQUEST] " + receivedData[1] + "  [FILE] " + receivedData[2]);

                        }
                        // Handle client usage reports
                        else if (receivedData[2].equals("http://clientsusage.com")){
                            // Check if client is a platinum client     (This request can only be accessed by platinum clients)
                            if (clientData[1].substring(clientData[1].length()-1).equals("0")) {
                                // Add to request log
                                serverUsage.appendRequestList(new String[]{dateFormat.format(new Date()), "accept", clientData[0], receivedData[2]});
                                // Send data to client
                                dos.writeObject(serverUsage.returnRequestList());
                                // Write to server
                                System.out.println("[REQUEST] " + receivedData[1] + "  [FILE] " + receivedData[2]);
                            }
                            // Handle clients that don't have platinum access level
                            else{
                                // decrement client requests
                                decrementClientUsage(clientData);
                                //Check if access quota reached
                                if (clientData[2].equals("0")) {
                                    // Data to be sent to client store in holder var, add flag to indicate the server is closing the connection
                                    sendData = new String[]{"request", receivedData[1], "failed. insufficient access level", serverUsage.getClientData(socket.getRemoteSocketAddress())[2], "close"};
                                    CloseConnection = true;
                                }
                                else{
                                    // Data to be sent to client store in holder var
                                    sendData = new String[]{"request", receivedData[1], "failed. insufficient access level", serverUsage.getClientData(socket.getRemoteSocketAddress())[2]};
                                }
                                // Add to request log
                                serverUsage.appendRequestList(new String[]{dateFormat.format(new Date()), "fail", clientData[0], receivedData[2]});
                                // Send data to client
                                dos.writeObject(sendData);
                                // Write to server
                                System.out.println("[REQUEST] " + receivedData[1] + "  [FILE] " + receivedData[2] + " -- FAILED. ");
                            }
                        }
                        // Handle failed/wrong requests
                        else{
                            // Increment client requests
                            decrementClientUsage(clientData);
                            //Check if access quota reached
                            if (clientData[2].equals("0")) {
                                // Data to be sent to client store in holder var, add flag to indicate the server is closing the connection
                                sendData = new String[]{"request", receivedData[1], "failed", serverUsage.getClientData(socket.getRemoteSocketAddress())[2], "close"};
                                CloseConnection = true;
                            }
                            else{
                                // Data to be sent to client store in holder var
                                sendData = new String[]{"request", receivedData[1], "failed", serverUsage.getClientData(socket.getRemoteSocketAddress())[2]};
                            }
                            // Add to request log
                            serverUsage.appendRequestList(new String[]{dateFormat.format(new Date()), "fail", clientData[0], receivedData[2]});
                            // Send data to client
                            dos.writeObject(sendData);
                            // Write to server
                            System.out.println("[REQUEST] " + receivedData[1] + "  [FILE] " + receivedData[2] + " -- FAILED");
                        }
                        // After a request, check if a user reached request access limit
                        // Check if last access number is even
                        if (CloseConnection){
                            System.out.println("[SERVER] [CLOSE] " + clientData[0] + " reached request limit.");
                            // Decrement server user limit
                            serverUsage.removeConnectedUsers();
                            // Close connection with client
                            socket.close();
                            break;
                        }
                    }
                    else{
                        // Put failed request in holder variable
                        sendData = new String[]{receivedData[0], receivedData[1], "failed. client not initialized"};
                        // Send data to client through object output stream
                        dos.writeObject(sendData);
                        // Write to server a client command failed
                        System.out.println("[REQUEST] " + receivedData[1] + "  [FILE] " + receivedData[2] + " -- FAILED. NO INIT");

                        // Close connection
                        serverUsage.removeConnectedUsers();
                        socket.close();
                        break;
                    }
                }

                // ----- Handle wrong commands -----
                else{
                    // Put failed command in holder variable
                    sendData = new String[]{receivedData[0], receivedData[1], "failed. unknown command"};
                    // Send data to client through object output stream
                    dos.writeObject(sendData);
                    // Write to server a client command failed
                    System.out.println("[COMMAND] " + receivedData[1] + receivedData[0]+ " -- FAILED");
                }
            }
            catch (IOException e){
                serverUsage.removeConnectedUsers();
                try {
                    System.out.println("[SERVER] [CLOSE] Connection closed by client " + serverUsage.getClientData(socket.getRemoteSocketAddress())[0]);
                }
                catch (ArrayIndexOutOfBoundsException ae){
                    System.out.println("[SERVER] [CLOSE] Connection closed by uninitialized client " + socket.getRemoteSocketAddress());
                    break;
                }
                break;
            }// End of try statement
            catch (ClassNotFoundException e2){ }
        } // End of while(true)

        // Close data streams
        try {
            this.dis.close();
            this.dos.close();
        } catch(IOException e){ }
    } // End of run()


    // ----------------------------
    // ----- Helper Functions -----
    // ----------------------------

    // decrementClientUsage helper function. Used to increment a clients requests used value
    public void decrementClientUsage(String[] clientData){
        // Increment client requests
        if (!clientData[2].equals("infinite")){
            clientData[2] = String.valueOf(Integer.valueOf(clientData[2]) - 1);
            // Increment client requests stored in data structure
            serverUsage.putClientData(socket.getRemoteSocketAddress(), clientData);
        }
    }

    public int getCacheListFileIndex(String lookupFile){
        for (int i = 0; i < cacheFiles.length; i++){
            if (cacheFiles[i].getName().equals(lookupFile)){
                return i;
            }
        }
        return 0;
    }


    public int getClientAccessQuota(String accessNumber){
        // Check if last access number is even
        if (Double.valueOf(accessNumber.substring(accessNumber.length()-1)) % 2 == 0){
            // Check if the last number is not zero      [Silver = 2,4,6,8]
            if (!accessNumber.substring(accessNumber.length()-1).equals("0")){
                return 3;
            }
            // Last number is zero      [Platinum = 0]
            else{
                return 0;
            }
        }
        // Check if last access number is odd
        else {
            // Check if request limit is reached for odd numbers [Gold = 1,3,5,7,9]
            return 5;
        }
    }

    // getCacheList helper function. Used to merge cache file list into a string
    public static String getCacheList(){
        String mergedCacheList = "";
        for (int i = 0; i < cacheFiles.length; i++){
            if (i == cacheFiles.length -1){
                mergedCacheList += cacheFiles[i].getName();
            }
            else{
                mergedCacheList += cacheFiles[i].getName() + ", ";
            }
        }
        return mergedCacheList;
    }

    // cacheLookup helper function. used to check if specific files in the cache exist
    public static boolean cacheLookup(String lookupFile){
        for (int i = 0; i < cacheFiles.length; i++){
            if (cacheFiles[i].getName().equals(lookupFile)){
                return true;
            }
        }
        return false;
    }
} // End of ServerHandler class