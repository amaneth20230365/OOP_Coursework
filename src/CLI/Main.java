package CLI;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName()); // gets the full name with package name
    public static void main(String[] args){

        try {
            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("Logging_file.log");
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error: ", e);
        }

        ConfigureSystem system = new ConfigureSystem();
        TicketPool ticketPool = new TicketPool();
        Vendor vendor = new Vendor();
        Scanner input = new Scanner(System.in);

        int option;
        do {
            logger.info("Loading Real Time Ticketing System ");

            System.out.println("\n       Real Time Ticketing System");
            System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
            System.out.println("                 MENU");
            System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
            System.out.println("1. Load Existing Configuration File");
            System.out.println("2. Create New Configuration File");
            System.out.println("3. Exit program");
            try {
                System.out.println("Please select an option:");
                option = input.nextInt();
                if (option <= 0 || option > 3) {
                    System.out.println("Invalid option. Please try again.");
                    logger.warning("Invalid Option.Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid option! Please try again.");
                logger.warning("Invalid Option. Please try again.");
                input.nextLine();
                option = -1;
            }
            switch (option) {
                case 1:
                    System.out.println("Load Existing Configuration File");
                    system.loadConfigurationSettingsFromFile();
                    logger.info("Loading "+system.getEventName()+" file.");
                    threadRunning(system, vendor,ticketPool);
                    option =3;
                    break;
                case 2:
                    System.out.println("Create New Configuration File");
                    logger.info("Creating"+system.getEventName()+" file.");
                    system.configureMenu();
                    threadRunning(system, vendor,ticketPool);
                    option=3;
                case 3:
                    System.out.println("Exit program");
                    logger.info("User is exiting the program.");
                    System.exit(0);
                    break;

            }
        } while (option != 3);
    }
    public static void threadRunning(ConfigureSystem system, Vendor vendor, TicketPool ticketPool) {

        int totalTickets = system.getTotalTicket();
        int maxTickets = system.getMaxTicketCapacity();
        int ticketReleaseRate = system.getTicketReleaseRate();
        int customerRetrievalRate = system.getCustomerRetrieveRate();

        AtomicInteger counter = vendor.getTotalTicketsReleased();

        System.out.println("\nStarting Simulation >>>");

        Vendor vendor1 = new Vendor("vendor #01", totalTickets, ticketReleaseRate, maxTickets, ticketPool);
        Vendor vendor2 = new Vendor("vendor #02", totalTickets, ticketReleaseRate, maxTickets, ticketPool);

        Thread thread1 = new Thread(vendor1);
        Thread thread2 = new Thread(vendor2);


        Customer customer1 = new Customer("customer #01", customerRetrievalRate, ticketPool, counter, totalTickets);
        Customer customer2 = new Customer("customer #02", customerRetrievalRate, ticketPool, counter, totalTickets);
        Customer customer3 = new Customer("customer #03", customerRetrievalRate, ticketPool, counter, totalTickets);

        Thread thread5 = new Thread(customer1);
        Thread thread6 = new Thread(customer2);
        Thread thread7 = new Thread(customer3);

        thread1.start();
        thread2.start();

        thread5.start();
        thread6.start();
        thread7.start();

        try {
            thread1.join();
            thread2.join();

            thread5.join();
            thread6.join();
            thread7.join();
        } catch (InterruptedException e) {
            System.out.println("ERROR: Thread was interrupted.");
            logger.warning("ERROR: Thread was interrupted.");
        }

        System.out.println("All tickets were processed.");
        System.out.println("Total tickets released: " + counter.get());
        System.out.println("Ending simulation.");
        logger.info("ALL tasks completed.");
    }

}