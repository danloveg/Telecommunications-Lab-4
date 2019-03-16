package lab4.sectionB01.daniellovegrove.gobackn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lab4.sectionB01.daniellovegrove.entity.Packet;
import lab4.sectionB01.daniellovegrove.entity.Message;
import lab4.sectionB01.daniellovegrove.logic.NetworkSimulator;

public class StudentNetworkSimulator extends NetworkSimulator
{
    private final int WindowSize;
    private final double RxmtInterval;

    public StudentNetworkSimulator(int numMessages,
                                   double loss,
                                   double corrupt,
                                   double avgDelay,
                                   int trace,
                                   int seed,
                                   int winsize,
                                   double delay)
    {
        super(numMessages, loss, corrupt, avgDelay, trace, seed);
	WindowSize = winsize;
	RxmtInterval = delay;
    }

    protected void Simulation_done() { }

    // -------------------------------------------------------------------------
    // --- SENDER (entity A) ---
    // -------------------------------------------------------------------------
    protected static class SenderState {
        protected static boolean timerRunning = false;
        protected static double retransmitInterval;
        protected static List<Packet> previousSentPackets;
        protected static Queue<Message> messageBuffer;
        protected static int nextSequenceNumber;
        protected static int baseSequenceNumber;
        protected static int windowSize;
    }

    protected void aInit()
    {
        SenderState.retransmitInterval = this.RxmtInterval;
        SenderState.previousSentPackets = new ArrayList<>();
        SenderState.messageBuffer = new LinkedList<>();
        SenderState.nextSequenceNumber = 0;
        SenderState.baseSequenceNumber = 0;
        SenderState.windowSize = this.WindowSize;
    }

    // FSM: rdt_send(data)
    protected void aOutput(Message message)
    {
        if (SenderState.nextSequenceNumber <
           (SenderState.baseSequenceNumber + SenderState.windowSize)) {

            // Create a new packet to send to B
            Packet packetToSend = NetworkUtilities.createPacket(
                    message.getData(), SenderState.nextSequenceNumber, -1);

            // Send the new packet
            toLayer3(A, packetToSend);

            // Start a timer for the first packet in the window only
            if (SenderState.baseSequenceNumber == SenderState.nextSequenceNumber) {
                if (SenderState.timerRunning) stopTimer(A);
                startTimer(A, SenderState.retransmitInterval);
                SenderState.timerRunning = true;
            }

            // Update the sender's state
            SenderState.previousSentPackets.add(packetToSend);
            SenderState.nextSequenceNumber++;
        } else {
            // Do not refuse data beyond the window size, just buffer it
            SenderState.messageBuffer.add(message);
        }
    }

    // FSM: rdt_rcv(rcvpkt)
    protected void aInput(Packet packet)
    {
        boolean isACK = NetworkUtilities.packetIsACK(packet);

        if (false == NetworkUtilities.packetIsCorrupt(packet) && isACK) {
            // Increment window
            SenderState.baseSequenceNumber = packet.getAcknum() + 1;

            // Send a message that has been queued
            if (false == SenderState.messageBuffer.isEmpty()) {
                aOutput(SenderState.messageBuffer.remove());
            };

            // Reset timer if the first packet in the window is received
            if (SenderState.baseSequenceNumber == SenderState.nextSequenceNumber) {
                if (SenderState.timerRunning) stopTimer(A);
                SenderState.timerRunning = false;
            } else {
                if (SenderState.timerRunning) stopTimer(A);
                startTimer(A, SenderState.retransmitInterval);
                SenderState.timerRunning = true;
            }
        }
    }

    // FSM: timeout
    protected void aTimerInterrupt()
    {
        startTimer(A, SenderState.retransmitInterval);
        SenderState.timerRunning = true;

        // Resend previous packets that receiver did not get
        for (int i = SenderState.baseSequenceNumber; i < SenderState.nextSequenceNumber; i++) {
            toLayer3(A, SenderState.previousSentPackets.get(i));
        }
    }
    
    // -------------------------------------------------------------------------
    // --- RECEIVER (entity B) ---
    // -------------------------------------------------------------------------
    protected static class ReceiverState {
        protected static int sequenceNumberRequired;
        protected static int lastGoodPacketReceived;
    }

    protected void bInit()
    {
        ReceiverState.sequenceNumberRequired = 0;
        ReceiverState.lastGoodPacketReceived = -1;
    }

    // FSM: rdt_rcv(rcvpkt)
    protected void bInput(Packet packet)
    {
        if (false == NetworkUtilities.packetIsCorrupt(packet)) {
            if (packet.getSeqnum() == ReceiverState.sequenceNumberRequired) {
                // Send accumulative ACK
                Packet packetToSend = NetworkUtilities.createPacket(
                        "ACK", -1, ReceiverState.sequenceNumberRequired);

                toLayer3(B, packetToSend);
                toLayer5(packet.getPayload());

                ReceiverState.sequenceNumberRequired++;
            } else if (ReceiverState.lastGoodPacketReceived == -1) {
                // Send NAK if first packet received is out of order
                Packet packetToSend = NetworkUtilities.createPacket("NAK", -1, -1);

                toLayer3(B, packetToSend);
            } else {
                // Send duplicate ACK for out of order packet
                Packet packetToSend = NetworkUtilities.createPacket(
                        "ACK", -1, ReceiverState.lastGoodPacketReceived);

                toLayer3(B, packetToSend);
            }
        }
    }
}
