// Rob Skiland - 10186568
// CISC 435 - Computer Networks F18
// Course Project
// December 6, 2018

// TestCases
// Class used to test desired requirements from the project rubric.

import Client.Client;

public class TestCases {

    // Set the IP that the client is suppose to connect to
    private static String ipAddress = "127.0.0.1";
    // Set the server port of the server
    private static int portNumber = 9789;

    // True: Print output into a text file in "logs/clientName-log.txt"
    // False: Print output in the default system console
    private static boolean enableClientLogs = true;

    public static void main(String[] args){
        // -- uncomment the functions to check test cases --

        //TestCaseOne();
        //TestCaseTwo();
        //TestCaseThree();
        //TestCaseFour();
        //TestCaseFive();
        //TestCaseSix();
        //TestCaseSeven();
        //TestCaseEight();
        //TestCaseNine();
        //TestCaseTen();
        //TestCaseEleven();
        //TestCaseTwelve();
    }

    // 1. Program can create a Server and client(s)
    public static void TestCaseOne(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        Client clientTwo = new Client(ipAddress, portNumber, "client2", enableClientLogs);
        clientOne.Connect();
        clientTwo.Connect();
    }

    // 2) Each client created is assigned a name and a random-access code
    public static void TestCaseTwo(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        clientOne.Connect();
    }

    // 3) Server can send list of contents
    public static void TestCaseThree(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        clientOne.Connect();
    }

    // 4) Clients can request contents
    public static void TestCaseFour(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        clientOne.Connect();
        clientOne.Request("dog.jpg");
    }
    // 5) Server can monitor and enforce clients’ access categories (quota)
    public static void TestCaseFive(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        Client clientTwo = new Client(ipAddress, portNumber, "client2", enableClientLogs);
        clientOne.Connect();
        clientTwo.Connect();
        clientOne.Request("dog.jpg");
        clientTwo.Request("cat.jpg");
        clientTwo.Request("house.jpg");
        clientOne.Request("cat.jpg");
    }

    // 6) Server can monitor and enforce clients’ access categories (quota)
    public static void TestCaseSix(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", 252, enableClientLogs);
        Client clientTwo = new Client(ipAddress, portNumber, "client2", 251, enableClientLogs);
        Client clientThree = new Client(ipAddress, portNumber, "client3", 250, enableClientLogs);
        clientOne.Connect();
        clientTwo.Connect();
        clientThree.Connect();
        clientOne.Request("dog.jpg");
        clientOne.Request("cat.jpg");
        clientOne.Request("house.jpg");
        clientTwo.Request("dog.jpg");
        clientTwo.Request("cat.jpg");
        clientTwo.Request("house.jpg");
        clientTwo.Request("waterfall.jpg");
        clientTwo.Request("cat.jpg");
        clientThree.Request("dog.jpg");
        clientThree.Request("dog.jpg");
        clientThree.Request("dog.jpg");
        clientThree.Request("dog.jpg");
        clientThree.Request("dog.jpg");
        clientThree.Request("dog.jpg");
    }

    // 7) A client that exceeds his/her quota or issue an exit before finishing is disconnected
    public static void TestCaseSeven(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        Client clientTwo = new Client(ipAddress, portNumber, "client2", 62, enableClientLogs);
        clientOne.Connect();
        clientTwo.Connect();
        clientOne.Close();
        clientTwo.Request("dog.jpg");
        clientTwo.Request("dog.jpg");
        clientTwo.Request("dog.jpg");
    }

    // 8) A client can be created as long as number of active users < 3
    public static void TestCaseEight(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        Client clientTwo = new Client(ipAddress, portNumber, "client2", enableClientLogs);
        Client clientThree = new Client(ipAddress, portNumber, "client3", enableClientLogs);
        Client clientFour = new Client(ipAddress, portNumber, "client4", enableClientLogs);
        Client clientFive = new Client(ipAddress, portNumber, "client5", enableClientLogs);
        clientOne.Connect();
        clientTwo.Connect();
        clientThree.Connect();
        clientFour.Connect();
        clientOne.Close();
        clientFive.Connect();
    }

    // 9) A platinum client can get other clients’ usage (active and disconnected clients)
    public static void TestCaseNine(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", 60, enableClientLogs);
        Client clientTwo = new Client(ipAddress, portNumber, "client2", 61, enableClientLogs);
        Client clientThree = new Client(ipAddress, portNumber, "client3", 62, enableClientLogs);
        clientOne.Connect();
        clientTwo.Connect();
        clientThree.Connect();
        clientTwo.Request("dog.jpg");
        clientThree.Request("cat.jpg");
        clientTwo.Close();
        clientOne.Request("http://clientsusage.com");
    }

    // 10) Client can receive and display responses from server (applies only for content name)
    public static void TestCaseTen(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", enableClientLogs);
        clientOne.Connect();
        clientOne.Request("dog.jpg");
    }

    // 11) Server can maintain clients’ usage details
    public static void TestCaseEleven(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", 64, enableClientLogs);
        Client clientTwo = new Client(ipAddress, portNumber, "client2", 61, enableClientLogs);
        Client clientThree = new Client(ipAddress, portNumber, "client3", 62, enableClientLogs);
        Client clientFour = new Client(ipAddress, portNumber, "client4", 60, enableClientLogs);
        clientOne.Connect();
        clientTwo.Connect();
        clientOne.Request("dog.jpg");
        clientThree.Connect();
        clientOne.Request("rhino.jpg");
        clientTwo.Request("dog.jpg");
        clientTwo.Request("http://clientsusage.com");
        clientThree.Request("house.jpg");
        clientOne.Request("waterfall.jpg");
        clientFour.Connect();
        clientTwo.Request("waterfall.jpg");
        clientFour.Request("http://clientsusage.com");
        clientFour.Request("dog.jpg");
        clientThree.Request("cat.jpg");
        clientThree.Close();
        clientFour.Request("http://clientsusage.com");
    }

    // 12) Server can serialize real contents/image
    public static void TestCaseTwelve(){
        Client clientOne = new Client(ipAddress, portNumber, "client1", 61, enableClientLogs);
        clientOne.Connect();
        clientOne.Request("cat.jpg");
        clientOne.Request("horse.jpg");
        clientOne.Request("dog.jpg");
        clientOne.Close();
    }
}
