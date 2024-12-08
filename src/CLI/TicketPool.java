package CLI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TicketPool {
    List<String> tickets = Collections.synchronizedList(new ArrayList<>());

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public int getCurrentPoolSize() {
        return tickets.size();
    }

    public synchronized void produceTicket(String vendorId, int count, int poolSize, AtomicInteger totalTicketsReleased) {
        if (tickets.size() + count > poolSize) {
            try {
                System.out.println("TicketPool is full.");
                System.out.println("Waiting");
                logger.info("TicketPool is full., waiting...");
                wait(); // Wait until there's space in the pool
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(vendorId + " was interrupted while waiting to add tickets.");
                logger.warning("ERROR: "+e.getMessage());
            }
        } else if (tickets.size()+ count <= poolSize) {
            for (int i = 0; i < count; i++) {
                tickets.add("Ticket-" + (tickets.size() + 1));
                System.out.println(vendorId + " added 1 ticket. Current pool size: " + tickets.size()+". Total tickets released: "+totalTicketsReleased);
                logger.info(vendorId + " added 1 ticket. Current pool size: " + tickets.size()+". Total tickets released: "+totalTicketsReleased);
                notifyAll(); // Notify all waiting threads (likely customers)
            }
        }
    }

    public synchronized void consumeTicket(String customerId, int count) {
        while (tickets.isEmpty()) { // tickets.size() < 1
            try {
                System.out.println("TicketPool is empty. No available tickets.");
                System.out.println(customerId+" is waiting.");
                logger.info("TicketPool is empty. No available tickets.");
                wait();

            }catch (InterruptedException e){
                System.out.println(customerId+" was interrupted while waiting to buy tickets.");
                logger.warning("ERROR: "+e.getMessage());
            }
        }
        for (int i = 0; i < count; i++) {
            tickets.remove(0);
        }
        System.out.println(customerId + " bought 1 ticket. Current Pool size: "+tickets.size());
        logger.info(customerId + " bought 1 ticket. Current pool size: "+tickets.size());
        notifyAll();

    }
}

