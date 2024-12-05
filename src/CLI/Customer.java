package CLI;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Customer implements Runnable {
    private String customerId;
    private int customerRetrievalRate;
    private TicketPool ticketPool;
    private AtomicInteger totalTicketsReleased;
    private int totalTickets;

    public static final Logger logger = Logger.getLogger(Customer.class.getName());

    public Customer(String customerId, int customerRetrievalRate, TicketPool ticketPool, AtomicInteger totalTicketsReleased, int totalTickets) {
        this.customerId = customerId;
        this.customerRetrievalRate = customerRetrievalRate;
        this.ticketPool = ticketPool;
        this.totalTicketsReleased = totalTicketsReleased;
        this.totalTickets = totalTickets;
    }



    @Override
    public void run() {
        try {
            while(totalTicketsReleased.get() != totalTickets || ticketPool.getCurrentPoolSize() >0) {
                try {
                    synchronized (ticketPool) {
                        if ((ticketPool.getCurrentPoolSize()) > 0){
                            ticketPool.consumeTicket(customerId,1);
                            Thread.sleep(customerRetrievalRate);
                        }else {
                            if (totalTicketsReleased.get() == totalTickets && ticketPool.getCurrentPoolSize() == 0){
                                break;
                            }else {
                                System.out.println("No tickets left");
                                System.out.println(customerId + " is waiting.");
                                ticketPool.wait();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: When consuming Ticket"+e.getMessage());
                    logger.warning("ERROR: "+e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.warning("ERROR: "+e.getMessage());
        }
    }

//    @Override
//    public void run() {
//        do {
//            try {
//                synchronized (ticketPool) {
//                    if ((ticketPool.getCurrentPoolSize()) > 0) {
//                        ticketPool.consumeTicket(customerId, 1);
//                        Thread.sleep(customerRetrievalRate);
//                    }
//                    else {
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println("ERROR: " + e.getMessage());
//            }
//        }while (totalTicketsReleased.get() != totalTickets);
//    }
}
