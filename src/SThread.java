import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SThread extends Thread {
	private Object[][] RTable; // routing table
	private PrintWriter out, outTo; // writers (for writing back to the machine and to destination)
	private BufferedReader in; // reader (for reading from the machine connected to)
	private String inputLine, outputLine, clientNickname, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // index in the routing table //port will store the port of the client/server
	private String nickname;

	/**
	 * Constructor
	 */
	SThread(Object[][] Table, Socket toClient, int index, String nickname) throws IOException {
		out = new PrintWriter(toClient.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
		RTable = Table;
		addr = toClient.getInetAddress().getHostAddress();
		RTable[index][0] = addr; // IP addresses
		RTable[index][1] = toClient; // sockets for communication
		RTable[index][2] = nickname;
		this.nickname = nickname;

		System.out.println("\nForeign address port (Client/server port) " + toClient.getPort() + "\n");
	}

	// Run method (will run for each machine that connects to the ServerRouter)
	public void run() {
		try {
			// Store client nickname into RTable and send confirmation fo connection
			clientNickname = in.readLine(); // initial read (the destination for writing)
			System.out.println(clientNickname + " has been stored in the RTable\n");
			out.println("Connected to the router."); // confirmation of connection

			// waits 10 seconds to let the routing table fill with all machines' information
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException ie) {
				System.out.println("Thread interrupted");
			}

			// loops through the routing table to find the destination
			for (int i = 0; i < 10; i++) {
				if (clientNickname.equals((String) RTable[i][0])) {
					outSocket = (Socket) RTable[i][1]; // gets the socket for communication from the table
					System.out.println("Found destination: " + clientNickname);
					outTo = new PrintWriter(outSocket.getOutputStream(), true); // assigns a writer
				}
			}

			// Communication loop
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Client/Server said: " + inputLine);

				outputLine = inputLine; // passes the input from the machine to the output string for the destination

				if (outSocket != null) {
					outTo.println(outputLine); // writes to the destination
				}

				if (inputLine.equals("Bye.")) {
					break; // exit statement
				}
			} // end while
		} catch (IOException e) {
			System.err.println("Could not listen to socket.");
			System.exit(1);
		}
	}

	public String getNickname() {
		return nickname;
	}

	public boolean lookupNickname(String nickname) {
		return RTable[0][2].equals(nickname);
	}

//	Return IP for client socket setup
	public String getIP(String nickname) {
		for (int i = 0; i < RTable.length; i++) {
			for (int j = 0; j < RTable[i].length; j++) {
				if (RTable[i][j] != null && RTable[i][2].equals(nickname)) {
					return (String) RTable[i][j];
				}
			}
		}

		return "";
	}
}