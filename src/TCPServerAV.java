import javax.sound.sampled.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPServerAV {

    public static void main(String[] args) throws IOException {

        // Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        InetAddress addr = InetAddress.getLocalHost();
        String host = addr.getHostAddress(); // Server machine's IP
//			String routerName = "192.168.1.77"; // ServerRouter host name
        String routerName = "10.0.0.66"; // ServerRouter host name
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
// 			String address ="192.168.1.76"; // destination IP (Client)
// 			String address ="10.74.24.131"; // destination IP (Client 1)
        String address ="10.0.0.66"; // destination IP (Client 2)

        // Communication process (initial sends/receives)
        out.println(address);// initial send (IP of the destination Client)
        fromClient = in.readLine();// initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromClient);

        OutputStream outStream = Socket.getOutputStream();
        // Communication while loop
        while (true) {
            AudioInputStream ain = testPlay("CantinaBand3.wav");
            if (ain != null) {
                AudioSystem.write(ain, AudioFileFormat.Type.WAVE,outStream);
            }
            break;
        }
        out.close();
        in.close();
        Socket.close();
        outStream.close();

        // closing connections
    }
    public static AudioInputStream testPlay(String filename) {
        AudioInputStream din = null;
        try {
            File file = new File(filename);
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            System.out.println("Before :: " + in.available());

            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, baseFormat.getSampleRate(),
                            8, baseFormat.getChannels(), baseFormat.getChannels(),
                            baseFormat.getSampleRate(), false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            System.out.println("After :: " + din.available());
            return din;
        } catch (Exception e) {
            // Handle exception.
            e.printStackTrace();
        }
        return din;
    }
}
