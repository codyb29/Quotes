package quotes;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class UnitTests {
	@Test
	void testReference() {
		String keywords = "JD";
		Quote quote = new Quote("John Doe", "Hello World!");
		quote.setKeywords(keywords);
		assertEquals(keywords, quote.getKeywords().get(0));
	}
	
	@Test
	void testKeywordLength() {
		String name = "John Doe";
		String text = "Hello World";
		int max = 2 * (name.split(" ").length + text.split(" ").length);
		String keywords = "1,2,3,4,5,6,7,8,9";

		Quote quote = new Quote(name, text);
		quote.setKeywords(keywords);

		assertEquals(max, quote.getKeywords().size());
	}
	
	@Test
	void testWordiness() {
		String invalidWord = "FIND ME,goodWord";
		Quote quote = new Quote("John Doe", "Hello World!");
		quote.setKeywords(invalidWord);
		assertFalse(quote.getKeywords().get(0).contains(" "));
		
				
	}
	@Test
	void testMultipleWordiness() {
		String invalidWord = "FIND ME, I'M AN ERROR, I'M THE THIRD ONE!!,goodWord";
		Quote quote = new Quote("John Doe", "Hello World!");
		
		quote.setKeywords(invalidWord);
		for (int i = 0; i < quote.getKeywords().size(); i++ ) {
			assertFalse(quote.getKeywords().get(i).contains(" "));
		}
	}
	
	@Test
	void testKeywordSearchSize() {
		QuoteServer server = new QuoteServer ("quotes/SearchHistory"); 
        try {
            server.getSearchHistory ().createNewFile ();
        } catch (IOException error) { }
        server.fillSearchHistory ("quotes/SearchHistory");
        server.setDatabase ("quotes/quotes.xml");
        
        QuoteList temp = server.getDatabase().keywordSearch("JD");
        assertTrue(temp.getSize() == 1);
	}
	
	@Test
	void testKeywordSearchQuote() {
		QuoteServer server = new QuoteServer ("quotes/SearchHistory"); 
        try {
            server.getSearchHistory ().createNewFile ();
        } catch (IOException error) { }
        server.fillSearchHistory ("quotes/SearchHistory");
        server.setDatabase ("quotes/quotes.xml");
        
        QuoteList temp = server.getDatabase().keywordSearch("JD");
        assertTrue(temp.getQuote(0).getKeywords().contains("JD"));
	}
	
	@Test
	void testKeywordSearchSizes() {
		QuoteServer server = new QuoteServer ("quotes/SearchHistory"); 
        try {
            server.getSearchHistory ().createNewFile ();
        } catch (IOException error) { }
        server.fillSearchHistory ("quotes/SearchHistory");
        server.setDatabase ("quotes/quotes.xml");
        
        QuoteList temp = server.getDatabase().keywordSearch("comedian");
        assertTrue(temp.getSize() == 2);
	}
	
	@Test
	void testKeywordSearchQuotes() {
		QuoteServer server = new QuoteServer ("quotes/SearchHistory"); 
        try {
            server.getSearchHistory ().createNewFile ();
        } catch (IOException error) { }
        server.fillSearchHistory ("quotes/SearchHistory");
        server.setDatabase ("quotes/quotes.xml");
        
        QuoteList temp = server.getDatabase().keywordSearch("comedian");
        for (int i = 0; i < temp.getSize(); i++) {
        		assertTrue(temp.getQuote(i).getKeywords().contains("comedian"));
        }
	}
	
	@Test
	void testDuplicatesSize() {
		String name = "John Doe";
		String text = "Hello World";
		String keywords = "1,2,3,4,5,6,1";
		int noDuplicates = 6;
		Quote quote = new Quote(name, text);
		quote.setKeywords(keywords);
		assertEquals(noDuplicates, quote.getKeywords().size());
	}
}
