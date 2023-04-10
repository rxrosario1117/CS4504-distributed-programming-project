import java.net.*;
import java.io.*;
import java.util.Arrays;

public class TCPServerRouter {
   public static void main(String[] args) throws IOException {
      Socket clientSocket = null; // socket for the thread
      Object[][] RoutingTable = new Object[100][3]; // routing table
      int SockNum = 5555; // port number
      Boolean Running = true;
      int ind = 0; // indext in the routing table
      String SenderNickname = "";
      String RecievNickname = "";

      Socket socket = null;

      // Accepting connections
      ServerSocket serverSocket = null; // server socket for accepting connections
      try {
         serverSocket = new ServerSocket(5555);

         socket = new Socket("10.0.0.13", 5556);

         System.out.println("SRouters connected");

         socket.close();

         System.out.println("ServerRouter is Listening on port: 5555.");
      } catch (IOException e) {
         System.err.println("Could not listen on port: 5555.");
         System.exit(1);
      }

      // Creating threads with accepted connections
      while (Running == true) {
         try {
            clientSocket = serverSocket.accept();
            SThread t = new SThread(RoutingTable, clientSocket, ind, SenderNickname, RecievNickname); // creates a
                                                                                                      // thread with a
                                                                                                      // random port
            t.start(); // starts the thread

            System.out.println("Nickname from the Srouter" + t.getSenderNickname());

            ind++; // increments the index0.

            System.out.println(
                  "ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
         } catch (IOException e) {
            System.err.println("Client/Server failed to connect.");
            System.exit(1);
         }
      } // end while

      // closing connections
      clientSocket.close();
      serverSocket.close();

   }
}