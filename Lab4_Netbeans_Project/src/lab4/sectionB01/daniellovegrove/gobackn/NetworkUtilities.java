package lab4.sectionB01.daniellovegrove.gobackn;

import lab4.sectionB01.daniellovegrove.entity.Packet;

public class NetworkUtilities {
    public static Packet createPacket(String message, int seqNum, int ackNum) {
        int checksum = NetworkUtilities.calculateChecksum(message, seqNum, ackNum);
        return new Packet(seqNum, ackNum, checksum, message);
    }

    // Calculate the checksum for a given packet
    public static int calculateChecksum(Packet packet) {
        return NetworkUtilities.calculateChecksum(packet.getPayload(), packet.getAcknum(), packet.getSeqnum());
    }

    // Calculate a checksum as the addition of string bytes, seqNum, and ackNum
    private static int calculateChecksum(String str, int seqNum, int ackNum) {
        int sum = 0;
        for (byte b: str.getBytes()) {
            sum += b;
        }
        return sum + ackNum + seqNum;
    }

    // Determine if a packet is corrupt
    public static boolean packetIsCorrupt(Packet packet) {
        return calculateChecksum(packet) == packet.getChecksum();
    }

    // Determine if a packet is a NAK packet
    public static boolean packetIsACK(Packet packet) {
        return packet.getPayload().equals("ACK");
    }
}
