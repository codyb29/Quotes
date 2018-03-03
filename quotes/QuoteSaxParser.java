package quotes;

import java.io.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;


//REMOVE xml parser imports!

/**
 * SAX parser
 * @author Mongkoldech Rajapakdee & Jeff Offutt
 *         Date: Nov 2009
 */
public class QuoteSaxParser
{
	   private QuoteSaxHandler handler;

	   
	  //constructor 
public QuoteSaxParser (String fileName)
{
   try
   {
      File quoteFile = new File (fileName);

      handler = new QuoteSaxHandler();
      
      
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser =  factory.newSAXParser();
      
      saxParser.parse (quoteFile, handler);
   } catch (Exception e)
   {
      e.printStackTrace();
   }
}


//get qoute list
public QuoteList getQuoteList()
{
   return handler.getQuoteList();
}
}
