   import java.io.*;
   import java.net.*;
   import java.util.Arrays;

   public class TCPServer {

       static DataInputStream dataIn = null;
       static DataOutputStream dataOut = null;


        public static void main(String[] args) throws IOException {

           // Variables for setting up connection and communication
           Socket Socket = null; // socket to connect with ServerRouter
           PrintWriter out = null; // for writing to ServerRouter
           BufferedReader in = null; // for reading form ServerRouter
           InetAddress addr = InetAddress.getLocalHost();
           String host = addr.getHostAddress(); // Server machine's IP

           // This IP is the server router for my local network
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
				
      	 // Variables for message passing
         String fromServer; // messages sent to ServerRouter
         String fromClient; // messages received from ServerRouter

           // Client 1
// 			String address ="10.74.24.131"; // destination IP (Client 1)
           //Client2
 			String address ="192.168.1.71"; // destination IP (Client 2)

			// Communication process (initial sends/receives)
			out.println(address);// initial send (IP of the destination Client)
			fromClient = in.readLine();// initial receive from router (verification of connection)
			System.out.println("ServerRouter: " + fromClient);
            System.out.println("IP address: " + in.readLine());

//            String fileName = "./newWav.wav";
            String fileType = in.readLine();
            System.out.println("File type: " + fileType);

            if (fileType.equalsIgnoreCase("txt")) {
                // Communication while loop
                while ((fromClient = in.readLine()) != null) {
                    System.out.println("Client said: " + fromClient);

                    if (fromClient.equals("Bye.")) { // exit statement
                        out.println(fromClient.toUpperCase());
                        break;
                    }

                    fromServer = fromClient.toUpperCase(); // converting received message to upper case
                    System.out.println("Server said: " + fromServer);
                    out.println(fromServer); // sending the converted message back to the Client via ServerRouter
                }
            }
            else if (fileType.equalsIgnoreCase("wav")) {
                DataOutputStream dataOut = null;
                DataInputStream dataIn = null;
                Socket wavSocket = null;

                try {
                    // Connection to the serverRouter to send the .wav file
                    wavSocket = new Socket(routerName, Socket.getPort());

                    System.out.println("Remote Socket: " + wavSocket.getRemoteSocketAddress());
                    System.out.println("Local Socket: " + wavSocket.getLocalSocketAddress());

                    dataIn = new DataInputStream(wavSocket.getInputStream());
                    dataOut = new DataOutputStream(wavSocket.getOutputStream());
                    System.out.println("Connected");

                    int bytes = 0;
                    FileOutputStream fileOutputStream = new FileOutputStream("newWav.wav");

                    long fileSize = dataIn.readLong();
                    byte[] buffer = new byte[4 * 1024];
                    while (fileSize > 0 && (bytes = dataIn.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
                        // Write to the file
                        fileOutputStream.write(buffer, 0, bytes);
                        fileSize -= bytes; // update file size to the remaining bytes to be read
                    }

                    fileOutputStream.close();
                    dataIn.close();
                    dataOut.close();
//                    wavSocket.close();
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
			
			// closing connections
         out.close();
         in.close();
         Socket.close();
      }

      public static String getFileType(String fileName) {
            return fileName.split("\\.")[1];
      }

      public static void receiveWavFile(String filePath) throws Exception {
            int bytes;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            long fileSize = dataIn.readLong();
            byte[] buffer = new byte[4 * 1024];

            while (fileSize > 0 && (bytes = dataIn.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
                // Write message to the buffer
                fileOutputStream.write(buffer, 0, bytes);
                // Update the fileSize to the remaining bytes in the file
                fileSize -= bytes;
            }

            System.out.println(".wav file received");
            fileOutputStream.close();

      }
   }
