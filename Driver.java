package quotes;

import java.util.Scanner;
import java.io.IOException;

public class Driver {
    private static final String historyFilePath = "quotes/SearchHistory"; // <immediate directory/filename>
    private static final String xmlFilePath = "quotes/quotes.xml"; // <immediate directory/filename>

    public static void main (String[] args) {
        // Establish our search history from potential previous sessions
        QuoteServer server = new QuoteServer (historyFilePath); 
        try {
            server.getSearchHistory ().createNewFile (); // create file if it hasn't been created
        } catch (IOException error) { }
        server.fillSearchHistory (historyFilePath); // read in our history file

        // Establish our Database of quotes
        server.setDatabase (xmlFilePath);
        
        // Print out random quote at the beginning of program
        server.generateRandomQuote ();

        // Drive home the program
        QuoteList results = new QuoteList (); // will store the results
        String query; // will store user inputs for search queries
        int mode = 2; // default behaviour will start with both
        Scanner input = new Scanner (System.in);
        while (mode != -1) {
            System.out.print ("How would you like to search?"
            + "\nAuthor: 0\tText: 1\t\tBoth: 2\t\tAnother random quote: 3"
            + "\t\tRecent Searches: 4\t\tEnter a new quote: 5\nTO EXIT: -1\n-> ");
            try {
                mode = Integer.parseInt(input.nextLine ());
            } catch (NumberFormatException error) {
                System.out.println ("Invalid menu input, please try again");
                continue;
            }

            switch (mode) {
                case 0:
                    System.out.print ("Search: ");
                    query = input.nextLine (); 
                    server.searchDatabase (query, mode);
                    break;
                case 1:
                    System.out.print ("Search: ");
                    query = input.nextLine (); 
                    server.searchDatabase (query, mode);
                    break;
                case 2:
                    System.out.print ("Search: ");
                    query = input.nextLine (); 
                    server.searchDatabase (query, mode);
                    break;
                case 3:
                    server.generateRandomQuote ();
                    break;
                case 4:
                    server.printHistory ();
                    break;
                case 5:
                    QuoteServer.addQuote ();
                    server.setDatabase (xmlFilePath);
                    break;
                case -1:
                    server.closeSearchHistory (historyFilePath);
                    System.out.println ("Program now exiting.");
                    break;
                default:
                    System.out.println ("Invalid menu input, please try again.");
            }
        } 
    }
}
