package CLI;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Vendor implements Runnable {
    private String vendorId;
    private int ticketReleaseRate;
    private TicketPool ticketPool;
    private int ticketPoolCapacity;
    private static final AtomicInteger totalTicketsReleased = new AtomicInteger(0);
    private int totalTickets;

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public Vendor(String vendorId,int totalTickets, int ticketReleaseRate ,int ticketPoolCapacity, TicketPool ticketPool) {
        this.vendorId = vendorId;
        this.ticketReleaseRate = ticketReleaseRate;
        this.ticketPoolCapacity = ticketPoolCapacity;
        this.ticketPool = ticketPool;
        this.totalTickets = totalTickets;
    }

    public Vendor() {
        // Default constructor
    }

    public AtomicInteger getTotalTicketsReleased() {
        return totalTicketsReleased;
    }

    @Override
    public void run() {
        do {
            try {
                if (totalTicketsReleased.get() == ticketPoolCapacity) {
                    break;
                }
                synchronized (ticketPool){
                    if(ticketPool.getCurrentPoolSize() < ticketPoolCapacity) {
                        ticketPool.produceTicket(vendorId, 1, ticketPoolCapacity, totalTicketsReleased);
                        ticketPool.notifyAll();
                        Thread.sleep(ticketReleaseRate);
                    }
                }
            }catch (Exception e) {
                System.out.println("ERROR"+e.getMessage());
                logger.warning("ERROR "+e.getMessage());
            }
        }while (totalTicketsReleased.get() != totalTickets);
//        continue to release tickets until totalTicketsReleased equals totalTickets assigned to the vendor.
    }
}
