package CLI;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ConfigureSystem {

    private String eventName;
    private int totalTicket;
    private int ticketReleaseRate;
    private int customerRetrieveRate;
    private int maxTicketCapacity;

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public ConfigureSystem(int totalTicket, int ticketReleaseRate, int maxTicketCapacity) {
        this.totalTicket = totalTicket;
        this.ticketReleaseRate = ticketReleaseRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public ConfigureSystem() {}

    public void configureMenu(){
        Scanner input = new Scanner(System.in);

        logger.info("Configure Settings");

        System.out.println("\n            Administrative Access Only ");
        System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
        System.out.println("        Configure the system to your liking:");
        System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾\n");
        eventName = validationString(input,"Enter Event name:");
        totalTicket = validationInteger(input, "1. Input, Total number of ticketsList available:");
        ticketReleaseRate = validationInteger(input, "2. Input, Ticket Release Rate (Milli Seconds)[ 1s = 1000ms ]:");
        customerRetrieveRate = validationInteger(input, "3. Input, Customer Retrieval Rate (Milli Seconds) [ 1s = 1000ms ]:");
        maxTicketCapacity = validationInteger(input, "4. Input, Max Ticket Capacity:");
        saveConfigurationSettingsToFile();
    }

    public static int validationInteger(Scanner input, String prompt) {
        int validInteger = -1;
        boolean isValid = false;

        while (!isValid) {
            try {
                System.out.println(prompt);
                validInteger = input.nextInt(); // Try to read an integer
                if (validInteger < 0) {
                    System.out.println("Please enter a positive integer.");
                } else if (validInteger ==0) {
                    System.out.println("The input must be greater than zero");
                } else {
                    isValid = true; // if valid input entered it exit loop
                }
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid input. Please enter an integer.");
                logger.warning("ERROR: Invalid input. Please enter an integer.");
                input.next(); // Clear the invalid input from the scanner
            }
        }
        return validInteger;
    }

    public static String validationString(Scanner input, String prompt) {
        String validString = null;
        boolean isValid = false;
        while (!isValid) {
            try {
                System.out.println(prompt);
                validString = input.nextLine();
                if (validString == null) {
                    System.out.println("ERROR:"+prompt);
                }
                else{
                    isValid = true;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid input. Please enter an String.");
                logger.warning("ERROR: Invalid input. Please enter an String.");
                input.next();
            }
        }
        return validString;
    }

    public void saveConfigurationSettingsToFile() {
        String fileName = String.format("%s.txt",(eventName));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("      System Configuration Settings\n");
            writer.newLine();
            writer.write("1. Name of the Event: " + eventName);
            writer.newLine();
            writer.write("2. Total Number of Tickets: " + totalTicket);
            writer.newLine();
            writer.write("3. Ticket Release Rate (Milli Seconds):" + ticketReleaseRate);
            writer.newLine();
            writer.write("4. Customer Retrieval Rate (Milli Seconds):" + customerRetrieveRate);
            writer.newLine();
            writer.write("5. Max Ticket Capacity :" + maxTicketCapacity);
            writer.newLine();

            System.out.println("Configuration saved to " + fileName);
            logger.info("Configuration saved to " + fileName);

        } catch (IOException e) {
            System.out.println("ERROR: Unable to save configuration to file. Please try again.");
            logger.warning("ERROR: Unable to save configuration to file.");
        }
    }

    public void loadConfigurationSettingsFromFile() {
        Scanner input = new Scanner(System.in);
        String fileName;
        String line;
        boolean fileLoaded = false;

        while (!fileLoaded) {
            try {
                System.out.print("Please enter the file name: ");
                fileName = input.nextLine();

                // Try opening the file
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    System.out.println("\n‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");

                    logger.info("Loading file.");

                    System.out.println("Successfully loaded the settings from " + fileName + " file");

                    while ((line = reader.readLine()) != null) {
                        System.out.println(line.trim()); // Remove any leading/trailing spaces

                        if (line.startsWith("1. Name of the Event:")) {
                            setEventName(line.substring(21).trim());
                        } else if (line.startsWith("2. Total Number of Tickets:")) {
                            setTotalTicket(Integer.parseInt(line.substring(27).trim()));
                        } else if (line.startsWith("3. Ticket Release Rate (Milli Seconds):")) {
                            setTicketReleaseRate(Integer.parseInt(line.substring(39).trim()));
                        } else if (line.startsWith("4. Customer Retrieval Rate (Milli Seconds):")) {
                            setCustomerRetrieveRate(Integer.parseInt(line.substring(43).trim()));
                        } else if (line.startsWith("5. Max Ticket Capacity :")) {
                            setMaxTicketCapacity(Integer.parseInt(line.substring(24).trim()));
                        }
                    }
                    logger.info("Successfully loaded the settings from " + fileName + " file");
                    fileLoaded = true; // Set flag to true once the file is successfully loaded

                } catch (FileNotFoundException e) {
                    System.out.println("ERROR: The file " + fileName + " does not exist. Please try again.");
                    logger.warning("ERROR: The file " + fileName + " does not exist.");
                }
            } catch (IOException e) {
                System.out.println("ERROR: An error occurred while reading input. Please try again.");
                logger.warning("ERROR: An error occurred while reading input.");
            }
        }
    }


    //    SETTERS
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setTotalTicket(int totalTicket) {
        this.totalTicket = totalTicket;
    }
    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }
    public void setCustomerRetrieveRate(int customerRetrieveRate) {
        this.customerRetrieveRate = customerRetrieveRate;
    }
    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }



    //    GETTERS
    public String getEventName() {
        return eventName;
    }
    public int getTotalTicket() {
        return totalTicket;
    }
    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }
    public int getCustomerRetrieveRate() {
        return customerRetrieveRate;
    }
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

}

