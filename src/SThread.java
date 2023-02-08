import java.io.*;
import java.net.*;
import java.lang.Exception;
import java.util.Arrays;


public class SThread extends Thread {
	private Object [][] RTable; // routing table
	private PrintWriter out, outTo; // writers (for writing back to the machine and to destination)
	private BufferedReader in; // reader (for reading from the machine connected to)
	private String inputLine, outputLine, destination, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // index in the routing table //port will store the port of the client/server

	/**
	 * Constructor
	 */
	SThread(Object [][] Table, Socket toClient, int index) throws IOException {
		out = new PrintWriter(toClient.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
		RTable = Table;
		addr = toClient.getInetAddress().getHostAddress();
		RTable[index][0] = addr; // IP addresses
		RTable[index][1] = toClient; // sockets for communication
		ind = index;

		System.out.println("\nForeign address port (Client/server port) " + toClient.getPort() + "\n");
	}

	// Run method (will run for each machine that connects to the ServerRouter)
	public void run() {
		try {
			// Initial sends/receive
			destination = in.readLine(); // initial read (the destination for writing)
			System.out.println("Forwarding to " + destination);
			out.println("Connected to the router."); // confirmation of connection

			// waits 10 seconds to let the routing table fill with all machines' information
			try {
				Thread.currentThread().sleep(10000);
			}
			catch(InterruptedException ie) {
				System.out.println("Thread interrupted");
			}

			// loops through the routing table to find the destination
			for ( int i=0; i<10; i++) {
				if (destination.equals((String) RTable[i][0])) {
					outSocket = (Socket) RTable[i][1]; // gets the socket for communication from the table
					System.out.println("Found destination: " + destination);
					outTo = new PrintWriter(outSocket.getOutputStream(), true); // assigns a writer
				}
			}


			// Communication loop
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Client/Server said: " + inputLine);

				outputLine = inputLine; // passes the input from the machine to the output string for the destination

				if (outSocket != null){
					outTo.println(outputLine); // writes to the destination
				}

				if (inputLine.equals("Bye.")) {
//					/*
//					Need to search for client/server that just disconnected to remove them from the routing table
//					This will open up slots for other client/servers that may want to join
//					 */
//// FIXME
////					As it stands, removing the client/server by setting the element to null, breaks the RTable array.
//
////					for (int i = 0; i < 2; i++) {
////
////						String RTableElement = Arrays.toString((RTable[i])); // Turns the Object RTabel[i] type into a String
////						String[] userSocketInfo = RTableElement.split(","); // Splits RTable elements info up into an array
////						String userIP = userSocketInfo[0].substring(1); // Isolates the IP of the user from userSocketInfo
////
////						if (userIP.equals(addr)) {
////							RTable[i] = null;
////						}
////						if (userIP.equals(destination)) {
////							RTable[i] = null;
////						}
////					}
//
					System.out.println("\n");
					for (int i = 0; i < 10; i++) {
						for (int j = 0; j < 2; j++) {
						}
						System.out.println(Arrays.toString(RTable[i]));
					}

					break; // exit statement
				}
			}// end while
		}
		catch (IOException e) {
			System.err.println("Could not listen to socket.");
			System.exit(1);
		}
	}
}