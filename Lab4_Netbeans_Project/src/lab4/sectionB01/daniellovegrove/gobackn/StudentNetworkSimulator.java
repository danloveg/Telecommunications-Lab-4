package lab4.sectionB01.daniellovegrove.gobackn;

import java.util.LinkedList;
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
        protected static Queue<Message> messageBuffer;
        protected static int nextSequenceNumber;
        protected static int baseSequenceNumber;
        protected static int windowSize;
    }

    // FSM: Upside down V
    protected void aInit()
    {
        SenderState.retransmitInterval = this.RxmtInterval;
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

    }

    // FSM: timeout
    protected void aTimerInterrupt()
    {

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
