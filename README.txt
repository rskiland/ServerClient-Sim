ServerClient-Sim
Rob Skiland
December 6, 2018

Simulate a server containing a cache of several different files. 
Clients connect to the server to receive files from the cache. Received files are placed in the clients directory.

-------------------------
--- Program Structure ---

ServerClient-Sim
-- cache
---- (cache files)
-- logs
---- (log files)
-- src
---- TestCases.java
---- Client
------ Client.java
---- Server
------ Server.java
------ ServerHandler.java
------ ServerUsage.java
------ StartServer.java


- Package Breakdown -
TestCases.java			(executable)

Client (package)
-- Client.java

Server (package):
-- Server.java
-- ServerHandler.java
-- ServerUsage.java
-- StartServer.java		(executable)


------------------------
--- Program Assembly ---

The project must contain a cache file with cache files inside to function. If not, the server will fail to start. 

I used the files in the cache: "dog.jpg, cat.jpg, house.jpg, waterfall.jpg"

Place any type of file in the cache and it will function properly. All tests used JPG files.

Server is defaulted to 127.0.0.1:9789


--------------
--- Server ---

Server Constructor:
	Server(int portNumber)
		- portNumber: specify which port the server will establish a socket on

Server Methods:
	void Server.start(boolean allowTextLogs)
		- allowTextLogs: toggle server text logs. 
			- if true, all system output placed in "/logs/server-logs.txt" 
			- if false, all system output is in the console


--------------
--- Client ---

Client Constructor:
	Client(String ServerAddress, int ServerPort, String clientName)							-- main constructor
	Client(String ServerAddress, int ServerPort, String clientName, boolean allowTextLogs)				-- main constructor, system output place in "/logs/clientName-log.txt"
	Client(String ServerAddress, int ServerPort, String clientName, int accessNumber)				-- testing constructor
	Client(String ServerAddress, int ServerPort, String clientName, int accessNumber, boolean allowTextLogs)	-- testing constructor, system output place in "/logs/clientName-log.txt"
		- ServerAddress: the address the client is connecting to
		- ServerPort: the port that the server is bound to
		- clientName: the name of the client to the server
		- allowTextLogs: toggle client text logs
			- if true, all system output placed in "/logs/clientName-log.txt"
			- if false, all system output is in the console
		- accessNumber: the amount of access the client has on the server, randomized value (50-300) but can be set for testing purposes
			- farthest right digit = 1,3,5,7,9 (odd number): gold access level (5 requests)
			- farthest right digit = 2,4,6,8 (even number): silver access level (3 requests)
			- farthest right digit = 0: platinum access level (infinite requests)
				- Platinum access can request all clients requests (by sending a request to the server with "http://clientsusage.com")

Client Methods:
	boolean Client.Connect()
		- Used to initialize a connection with a server

	boolean Client.Request(String fileRequest)
		- fileRequest: the name of the file to be requested from the server cache

	boolean Client.Close()
		- Used to close the socket connection with a server

	