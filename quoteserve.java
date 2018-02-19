package quotes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.regex.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class quoteserve {
  // new Data file Path
  private static final String quoteFileName = "quotes/quotes.xml";

  public static void main(String[] args) throws Exception {

    String[] recentSearches = new String[] { "", "", "", "", "" }; // will hold the top 5 recent searches by the
    // user

    // update the recentsearch history from previous session
    File file = new File("searches");
    if (!file.exists()) {
      file.createNewFile();
    }
    BufferedReader br = new BufferedReader(new FileReader(file));

    String read;
    int k = 0;
    while ((read = br.readLine()) != null) {
      recentSearches[k] = read;
      k++;
    }

    QuoteList db = new QuoteList(); // database
    QuoteSaxParser qParser = new QuoteSaxParser(quoteFileName); // extract data from xml file
    db = qParser.getQuoteList(); // parse the data into our database

    int mode = 2; // default behavior is both
    Quote randQuote = new Quote();
    randQuote = db.getRandomQuote();
    System.out.println("\nRANDOM QUOTE: " + randQuote.getQuoteText());
    System.out.println("\t- " + randQuote.getAuthor() + "\n");

    QuoteList quoteList = new QuoteList(); // will be used to display search results
    Scanner scan = new Scanner(System.in);
    while (mode != -1) {// loop until user would like to exit
      System.out.print("How would you like to search?"
          + "\nAuthor: 0\tText: 1\t\tBoth: 2\t\tAnother random quote: 3\t\tRecent Searches: 4\t\tEnter a new quote: 5\n"
          + "TO EXIT: -1\n-> ");
      while (true) { // loop until user inputs a correct value
        try {
          mode = Integer.parseInt(scan.nextLine()); // specifies the mode user would like to be in
          if (mode < -1 || mode > 5) {
            System.out.print("Invalid input. Try again: \n->");
            continue;
          }
          break; // valid input has been established
        } catch (NumberFormatException e) {
          System.out.print("Invalid input. Try again: \n->");
        }
      }

      // before the program exits. Write to search file.

      if (mode >= 0 && mode <= 2) { // user would like to make a search query
        System.out.print("Search: ");

        String search = scan.nextLine();

        // keeps track of the users recent searches.
        for (int i = 0; i < recentSearches.length; i++) {
          if (recentSearches[i] == "") {
            recentSearches[i] = search;
            break;
          } else if (recentSearches[recentSearches.length - 1] != "") {
            for (i = 0; i < recentSearches.length - 1; i++) {
              recentSearches[i] = recentSearches[i + 1];
            }
            recentSearches[i] = search;
          }

        }

        quoteList = db.search(search, mode); // given mode
        System.out.print("\n");
        for (int i = 0; i < quoteList.getSize(); i++) { // loop through all quotes found
          System.out.println(quoteList.getQuote(i).getQuoteText());
          System.out.println("\t- " + quoteList.getQuote(i).getAuthor());
        }
        System.out.print("\n");

      } else if (mode == 3) { // would like to print out another random quote
        randQuote = db.getRandomQuote();
        System.out.println("\nRANDOM QUOTE: " + randQuote.getQuoteText());
        System.out.println("\t- " + randQuote.getAuthor() + "\n");
      } else if (mode == 4) { // show top 5 recent searches
        for (int i = 0; i < recentSearches.length; i++) {
          if (recentSearches[i] != "")
            System.out.println(i + 1 + ". " + recentSearches[i]);
        }
        System.out.println();
      } else if (mode == 5) { // Enter a new quote-text/author
        String author = "";
        String text = "";

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
        writeToXML(author, text);

        qParser = new QuoteSaxParser(quoteFileName); // extract data from xml file
        db = qParser.getQuoteList(); // parse the data into our database
      }

      FileWriter fw = new FileWriter(file);
      BufferedWriter bw = new BufferedWriter(fw);
      for (int i = 0; i < recentSearches.length; i++) {
        if (recentSearches[i] != "")
          bw.write(recentSearches[i] + "\n");
      }
      bw.close();
    }
  }

  //////////////////

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

  public static void writeToXML(String author, String quote) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
    Document doc = documentBuilder.parse("quotes/quotes.xml");
    Element root = doc.getDocumentElement();

    Element newQuote = doc.createElement("quote");

    Element quoteText = doc.createElement("quote-text");
    quoteText.appendChild(doc.createTextNode(quote+"\n"));
    newQuote.appendChild(quoteText);

    Element authTag = doc.createElement("author");
    authTag.appendChild(doc.createTextNode(author+"\n"));
    newQuote.appendChild(authTag);

    root.appendChild(newQuote);

    DOMSource source = new DOMSource(doc);

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    StreamResult result = new StreamResult("quotes/quotes.xml");
    transformer.transform(source, result);
    
  }

}
