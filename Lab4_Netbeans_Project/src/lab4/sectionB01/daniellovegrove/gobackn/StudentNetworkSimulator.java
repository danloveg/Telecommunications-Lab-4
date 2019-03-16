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
    /*   Please use the following variables in your routines.
     *   int WindowSize  : the window size
     *   double RxmtInterval   : the retransmission timeout
     *   int LimitSeqNo  : when sequence number reaches this value, it wraps around
     */

    private final int WindowSize;
    private final double RxmtInterval;
    private final int LimitSeqNo;
    
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
	LimitSeqNo = winsize+1;
	RxmtInterval = delay;
    }

    protected void Simulation_done() { }

    // -------------------------------------------------------------------------
    // --- SENDER (entity A) ---
    // -------------------------------------------------------------------------

    protected static class SenderState {
        protected static double retransmitInterval;
        protected static boolean messageInTransit = false;
        protected static List<Packet> previousSentPackets;
        protected static Queue<Message> messageBuffer;
        protected static int nextSequenceNumber;
        protected static int baseSequenceNumber;
        protected static int windowSize;
    }

    // FSM: Upside down V
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

    }

    // FSM: rdt_rcv(rcvpkt)
    protected void aInput(Packet packet)
    {
        if (false == NetworkUtilities.packetIsCorrupt(packet)) {
            // Increment window
            SenderState.baseSequenceNumber = packet.getAcknum() + 1;

            // Send a message that has been queued
            if (false == SenderState.messageBuffer.isEmpty()) {
                aOutput(SenderState.messageBuffer.remove());
            };

            if (SenderState.baseSequenceNumber == SenderState.nextSequenceNumber) {
                stopTimer(A);
            } else {
                startTimer(A, SenderState.retransmitInterval);
            }
        }
    }

    // FSM: timeout
    protected void aTimerInterrupt()
    {
        startTimer(A, SenderState.retransmitInterval);

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
    }

    // FSM: upside down V
    protected void bInit()
    {
        ReceiverState.sequenceNumberRequired = 0;
    }

    // FSM: rdt_rcv(rcvpkt)
    protected void bInput(Packet packet)
    {

    }
}
