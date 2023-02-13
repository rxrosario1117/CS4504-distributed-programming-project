   import java.io.*;
   import java.net.*;


   public class TCPClient {
       //Variables for calculations
       int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;

       public static void main(String[] args) throws IOException {
           // Variables for setting up connection and communication
           Socket Socket = null; // socket to connect with ServerRouter
           PrintWriter out = null; // for writing to ServerRouter
           BufferedReader in = null; // for reading form ServerRouter
           InetAddress addr = InetAddress.getLocalHost();
		   String localHost = addr.getHostAddress(); // Client machine's IP
      	   String routerName = "10.0.0.57"; // ServerRouter localHost name
		   int SockNum = 5555; // port number

			// Tries to connect to the ServerRouter
           try {
               Thread.sleep(3000);
               Socket = new Socket(routerName, SockNum);
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
            } catch (InterruptedException e) {
               e.printStackTrace();
           }

           // Variables for message passing
        Reader reader = new FileReader("./file.txt");
        BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
         String fromServer; // messages received from ServerRouter
         String fromUser; // messages sent to ServerRouter
//			String address =routerName; // destination IP (Server)
			String address ="10.0.0.57"; // destination IP (Server)
			double t0, t1, t;
            //Variables for calculations
           int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;

			// Communication process (initial sends/receives
			out.println(address);// initial send (IP of the destination Server)
			fromServer = in.readLine();//initial receive from router (verification of connection)
			System.out.println("ServerRouter: " + fromServer);
			out.println(localHost); // Client sends the IP of its machine as initial send
			t0 = System.currentTimeMillis();

			// Communication while loop
         while ((fromServer = in.readLine()) != null) {
             System.out.println("Server: " + fromServer);
             t1 = System.currentTimeMillis();
             if (fromServer.equals("BYE.")){ /* exit statement */
                 break;
             }
             t = t1 - t0;
             totalTransmissionSize += t;
             System.out.println("Cycle time: " + t);

             fromUser = fromFile.readLine(); // reading strings from a file
             if (fromUser == null){
                 System.out.println("Client disconnecting...");
                 break;
             }
             if (fromUser != null) {
                 System.out.println("Client: " + fromUser);
                 out.println(fromUser); // sending the strings to the Server via ServerRouter
                 msgCount++; //Incrementing message count
                 totalMsgSize += fromUser.length(); //adding length of message to totalMsgSize
                 t0 = System.currentTimeMillis();
             }
         }

			// closing connections
         out.close();
         in.close();
         Socket.close();

         double avgMessageSize = 0, avgTransmissionTime = 0, avgLookUpTime = 0;
         if (msgCount > 0) {
             avgMessageSize = (double) totalMsgSize/msgCount; //Calculating average message size
             avgTransmissionTime = (double) totalTransmissionSize/msgCount;
         }

         //Writing statistical results to results.txt
         FileWriter resultFW = new FileWriter("results.txt");
         BufferedWriter resultBW = new BufferedWriter(resultFW);
         try {
             resultBW.write("Average message size is: " + avgMessageSize+" characters");
             resultBW.newLine();
             resultBW.write("Average transmission time is: "+(avgTransmissionTime/60)+" seconds");
         } catch (IOException e) {
             System.out.println("Error writing to file: "+e.getMessage());
         }
         finally {
             resultBW.close();
             resultFW.close();
         }
       }

   }
