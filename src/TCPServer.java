   import java.io.*;
   import java.net.*;

    public class TCPServer {
       public static void main(String[] args) throws IOException {
      	
			// Variables for setting up connection and communication
         Socket Socket = null; // socket to connect with ServerRouter
         PrintWriter out = null; // for writing to ServerRouter
         BufferedReader in = null; // for reading form ServerRouter
			InetAddress addr = InetAddress.getLocalHost();
//           String[] command1 = { "netsh", "interface", "ip", "set", "address",
//                   "name=", "Local Area Connection" ,"source=static", "addr=","127.0.0.1",
//                   "mask=", "127.0.0.2"};
//           Process pp = java.lang.Runtime.getRuntime().exec(command1);
			String host = addr.getHostAddress(); // Server machine's IP
           System.out.println("Server Host: " + host);

//           String routerName = "j263-08.cse1.spsu.edu"; // ServerRouter host name
			String routerName = "localhost"; // ServerRouter host name needs to be computer ip address or localhost
			int SockNum = 5555; // port number
			
			// Tries to connect to the ServerRouter
         try {
            Socket = new Socket(routerName, SockNum, addr, 49998);// needs to be updated to constructor that sets local address and local port
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
         }
             catch (UnknownHostException e) {
               System.err.println("Don't know about router: " + routerName);
               System.exit(1);
            } 
             catch (IOException e) {
               System.err.println("Couldn't get I/O for the connection to: " + routerName);
               System.exit(1);
            }
				
      	// Variables for message passing			
         String fromServer; // messages sent to ServerRouter
         String fromClient; // messages received from ServerRouter      
// 			String address ="10.5.3.196"; // destination IP (Client)
 			String address ="192.168.68.112:49999"; // destination IP (Client)  /// DEBUGGED AND LOOKED AT THE DESTINATION in line 49 in sthread
           // BUT, this doesn't work because the IP Address is the same for all
           // this one needs to be the IP of the device running the client it is looking for or 127.0.0.1
           // needs to be ip address:port number in the 40000 and computer ip address needs to be checked to make sure it hasn't changed

			// Communication process (initial sends/receives)
			out.println(address);// initial send (IP of the destination Client)
			fromClient = in.readLine();// initial receive from router (verification of connection)
			System.out.println("ServerRouter: " + fromClient);
			         
			// Communication while loop
      	while ((fromClient = in.readLine()) != null) {
            System.out.println("Client said: " + fromClient);
            if (fromClient.equals("Bye.")) // exit statement
					break;
				fromServer = fromClient.toUpperCase(); // converting received message to upper case
				System.out.println("Server said: " + fromServer);
            out.println(fromServer); // sending the converted message back to the Client via ServerRouter
         }
			
			// closing connections
         out.close();
         in.close();
         Socket.close();
      }
   }
