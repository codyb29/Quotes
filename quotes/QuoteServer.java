package quotes;

import java.util.Iterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Stack;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.regex.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

public class QuoteServer {
    private Stack<String> recentSearches = new Stack<String>(); // holds the top 5 recent searches
    private File searchHistory; // file containing the search queries from before/after session
    private QuoteList database; // will contain all quotes parsed in an xml file

    public QuoteServer (String historyFilePath) {
        this.searchHistory = new File (historyFilePath);
    }

    public void setDatabase(String xmlFilePath) {
        QuoteSaxParser qParser = new QuoteSaxParser (xmlFilePath);
        this.database = qParser.getQuoteList ();
    }

    public QuoteList getDatabase () {
        return this.database;
    }

    public void fillSearchHistory (String filename) {
        try {
            FileReader fReader = new FileReader (filename);
            BufferedReader bReader = new BufferedReader (fReader);
            String query;
            while ((query = bReader.readLine ()) != null) {
                recentSearches.push(query);
            } 
            bReader.close();
        } catch (Exception error) {
            System.out.println("ERROR: Could not find " + filename);
        }
    }

    public void closeSearchHistory (String filename) {
        try {
            FileWriter fWriter = new FileWriter(filename);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            Iterator<String> iter = this.recentSearches.iterator ();
            while (iter.hasNext ()) {
                bWriter.write(iter.next () + "\n");
            }
            bWriter.close();
        } catch (Exception error) {
            System.out.println("ERROR: Could not find " + filename);
        }

    }

    public File getSearchHistory () {
        return this.searchHistory;
    }

    public void searchDatabase (String query, int mode) {
        if (recentSearches.size () >= 5) {
                recentSearches.removeElementAt (0); // first in last out
        }
        recentSearches.push (query);
        
        QuoteList results;
        if (mode < 6) {
        		results = database.search(query, mode); // given mode
        } else {
        		results = database.keywordSearch(query);
        }
        System.out.print("\n");
        for (int i = 0; i < results.getSize(); i++) { // loop through all quotes found
          System.out.println(results.getQuote(i).getQuoteText());
          System.out.println("\t- " + results.getQuote(i).getAuthor());
        }
        System.out.print("\n");
    }

    public void generateRandomQuote () {
        Quote randQuote = this.database.getRandomQuote();
        System.out.println ("\nRANDOM QUOTE: " + randQuote.getQuoteText ()
                            + "\n\t- " + randQuote.getAuthor () + "\n");
    }

    public void printHistory () {
		int count = 1;
        for (int i = recentSearches.size () - 1; i > -1 ; i--) {
            System.out.println (count + ". " + recentSearches.elementAt(i));
			count++;
        }

        System.out.print("\n");
    }

    public static void addQuote () {
        Scanner scan = new Scanner(System.in);
        String author = "";
        String text = ""; 
        String keyword = "";
        
        while (!regex(AUTHOR_EXPRESSION, author)) {
            System.out.print("Enter the name of the Author: ");
            author = scan.nextLine();
            if (!regex(AUTHOR_EXPRESSION, author)) {
                System.out.println("Invalid Author name. Please try again.");
            }
        }
        
        while (!quoteValidation(text)) {
            System.out.print("Enter quote: ");
            text = scan.nextLine();
            if (!quoteValidation(text)) {
                System.out.println("Invalid quote. Please try again.");
            }
        }
        
        int max = 2 * (author.split(" ").length + text.split(" ").length);
        int counter = 1;
        
        System.out.print("Enter keywords (" + (max - (counter -1)) + " left): ");
        String temp = scan.nextLine();
        if(temp.contains(" ")) { 
        		System.out.println("Not a keyWORD");
        } else {
        		temp.trim();
        		keyword = temp;
        }
        while (!temp.equals("") && counter < max) {
        		System.out.print((max - counter) + " left: ");
        		temp = scan.nextLine().trim();
        		if (!temp.equals("") && !temp.contains(" ")) {
        			keyword += "," + temp;
        			counter++;
        		} else if(temp.contains(" ")) {
        			System.out.println("Not a keyWORD");
        		}
        }

        
        writeToXML(author, text, keyword);
    }
    
  // This should check if the input matches the regex
  public static boolean regex(String regex, String input) {
    // put the regex
    Pattern pattern = Pattern.compile(regex);

    // define matcher based on regex
    Matcher matcher = pattern.matcher(input);

    if (matcher.find() && matcher.end() == input.length()) {
      return true;
      // CHECK IF REG EX IS WORKING!
    }
    return false;
  }

  public static final String AUTHOR_EXPRESSION = "([0-9]*)?[1A-Za-z]+([A-Za-z0-9]*\\.{1})?((\\s([0-9]*)?[A-Za-z]+([A-Za-z0-9]*\\.{1})?)*)?";
  // a number maybe? but for sure a letter of some sort and they can have
  // some numbers and letter combinations if they want afterwards.

  public static boolean quoteValidation(String quote) {
    // quotes must begin with a capital letter and end with a punctuation.
    String punctuations = "?!.";
    if (quote.length() == 0 || Character.isLowerCase(quote.charAt(0))
        || !punctuations.contains(String.valueOf(quote.charAt(quote.length() - 1)))) {
      return false;
    }

    int pb = 0; // Parentheses balance
    int qb = 0; // Quote balance
    // need to check balanced parens && quotation marks
    for (int i = 0; i < quote.length(); i++) {
      if (quote.charAt(i) == '(' && pb == 0) {
        pb++;
      } else if (quote.charAt(i) == '"' && qb == 0) {
        qb++;
      } else if (quote.charAt(i) == ')' && pb == 1) {
        pb--;
      } else if (quote.charAt(i) == '"' && qb == 1) {
        qb--;
      }
    }
    if (pb != 0 || qb != 0) {
      return false;
    }

    return true;

  }

    public static void writeToXML(String author, String quote, String keyword) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document doc = documentBuilder.parse("quotes/quotes.xml");
            Element root = doc.getDocumentElement();

            Element newQuote = doc.createElement("quote");

            Element quoteText = doc.createElement("quote-text");
            quoteText.appendChild(doc.createTextNode(quote));
            newQuote.appendChild(quoteText);

            Element authTag = doc.createElement("author");
            authTag.appendChild(doc.createTextNode(author));
            newQuote.appendChild(authTag);
            
            if (!keyword.equals("")) {
            		Element keyTag = doc.createElement("keyword");
            		keyTag.appendChild(doc.createTextNode(keyword));
            		newQuote.appendChild(keyTag);
            }
            root.appendChild(newQuote);

            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("quotes/quotes.xml")));
        } catch (Exception e) {
            System.out.println("Could not write to XML file");
        }
    }
}
