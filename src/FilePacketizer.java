import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilePacketizer {
    private static final int PACKET_SIZE = 1024;

    public static byte[][] packetizeFile(String fileName) {
        byte[][] packets = null;
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            // Determine the number of packets
            long fileSize = fileInputStream.available();
            int numPackets = (int) Math.ceil((double) fileSize / PACKET_SIZE);
            packets = new byte[numPackets][];

            // Read the file into packets
            int bytesRead;
            byte[] packet = new byte[PACKET_SIZE];
            for (int i = 0; i < numPackets; i++) {
                bytesRead = fileInputStream.read(packet);
                packets[i] = new byte[bytesRead];
                System.arraycopy(packet, 0, packets[i], 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return packets;
    }

    public static void reassemblePackets(String filename, byte[][] packets) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            for (byte[] i: packets){
                fos.write(i);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
