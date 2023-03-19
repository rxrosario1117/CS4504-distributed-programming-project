import java.io.*;
import java.net.*;

   public class TCPClient {
       public static void main(String[] args) throws IOException {

           // Variables for setting up connection and communication
           Socket Socket = null; // socket to connect with ServerRouter
           PrintWriter out = null; // for writing to ServerRouter
           BufferedReader in = null; // for reading form ServerRouter
           InetAddress addr = InetAddress.getLocalHost();
		   String host = addr.getHostAddress(); // Client machine's IP
      	   String routerName = "192.168.1.76"; // ServerRouter host name
		   int SockNum = 5555; // port number

           // Tries to connect to the ServerRouter
           try {
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
            }

           // File name and the file type
//           String fileName = "file.txt";
           String fileName = "CantinaBand3.wav";
           File file = new File(fileName); // Used in the Reader object
           String fileType = getFileType(fileName);

           // Variables for message passing
           Reader reader = new FileReader(file);
           BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file

           String fromServer; // messages received from ServerRouter
           String fromUser; // messages sent to ServerRouter
           String address ="192.168.1.80"; // destination IP (Server)

           long t0, t1, t;
            // Variables for calculations and gathering data
            int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;
			
			// Communication process (initial sends/receives)
			out.println(address);// initial send (IP of the destination Server)
			fromServer = in.readLine();//initial receive from router (verification of connection)
			System.out.println("ServerRouter: " + fromServer);
			out.println(host); // Client sends the IP of its machine as initial send
			t0 = System.currentTimeMillis();

           // send the fileType
           out.println(fileType);
           out.println("");

            if (fileType.equalsIgnoreCase("txt")) {
                // Communication while loop
                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server: " + fromServer);
                    t1 = System.currentTimeMillis();

                    // Updated to receive a final capitalized phrase from the server
                    if (fromServer.equals("BYE.")) /* exit statement */
                        break;

                    t = t1 - t0;

                    // Captures the total transmission size of the message
                    totalTransmissionSize += t;
                    System.out.println("Cycle time: " + t);

                    fromUser = fromFile.readLine(); // reading strings from a file
                    if (fromUser != null) {

                        System.out.println("Client: " + fromUser);
                        out.println(fromUser); // sending the strings to the Server via ServerRouter

                        msgCount++; // Incrementing message count
                        totalMsgSize += fromUser.length(); //adding length of message to totalMsgSize
                        t0 = System.currentTimeMillis();
                    }
                }

            }

            else if (fileType.equalsIgnoreCase("wav")) {
                DataOutputStream dataOut = null;
                DataInputStream dataIn = null;
                Socket wavSocket = null;

                // Connection to the serverRouter to send the .wav file

//                try (ServerSocket serverSocket = new ServerSocket(Socket.getPort())) {
//                    wavSocket = serverSocket.accept();
                try {
                    wavSocket = new Socket(routerName, Socket.getPort());
                    dataIn = new DataInputStream(wavSocket.getInputStream());
                    dataOut = new DataOutputStream(wavSocket.getOutputStream());

                    int bytes = 0;
                    FileInputStream fileInputStream = new FileInputStream(file);

                    // Send to the server
                    dataOut.writeLong(file.length());
                    // Break up the data into pieces, like a datagram
                    byte[] buffer = new byte[4 * 1024];
                    while ((bytes = fileInputStream.read(buffer))!= -1) {
                        // Send to the serverSocket
                        dataOut.write(buffer, 0, bytes);
                        dataOut.flush();
                    }

                    fileInputStream.close();
                    dataIn.close();
                    dataOut.close();
                    wavSocket.close();

                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
         // closing connections
         out.close();
         in.close();
         Socket.close();

         // Stores the metrics gathered and performs some final calculations
         double avgMessageSize = 0, avgTransmissionTime = 0;
         if (msgCount > 0) {
             avgMessageSize = (double) totalMsgSize/msgCount; //Calculating average message size
             avgTransmissionTime = (double) totalTransmissionSize/msgCount;
         }

         //Writing statistical results to results.txt
         try {
             FileWriter resultFW = new FileWriter("results.txt");
             BufferedWriter resultBW = new BufferedWriter(resultFW);
             resultBW.write("Average message size is: " + avgMessageSize);
             resultBW.newLine();
             resultBW.write("Average transmission time is: "+avgTransmissionTime);
             resultBW.close();


         } catch (IOException e) {
             System.out.println("Error writing to file: "+e.getMessage());
         }
       }

       /**
        * Returns the file type of the given file
        */
       public static String getFileType(String fileName) {
           return fileName.split("\\.")[1];
       }
   }
