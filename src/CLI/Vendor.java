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
            while (true) {
                synchronized (ticketPool) {
                    if (totalTicketsReleased.get() >= totalTickets) {
                        break;
                    }
                    if (ticketPool.getCurrentPoolSize() + 1 <= ticketPoolCapacity) {
                        totalTicketsReleased.incrementAndGet();
                        ticketPool.produceTicket(vendorId, 1, ticketPoolCapacity, totalTicketsReleased);

                        try {
                            Thread.sleep(ticketReleaseRate);
                        } catch (InterruptedException e) {
                            System.out.println("ERROR: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Ticket pool is full, waiting for cutsomers...");
                    }
                }
            }
    }
}
