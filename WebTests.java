import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

class WebTests {
	
	WebDriver driver;
	
	// Used for frequent need to update and search
	private class Query {
		WebElement searchBox; // Identify the search box on the webpage
		WebElement searchButton; // Identify the search button on the webpage
		WebElement radioButton; // Could be author, quote, or both
		String scope;
		
		Query (String scope) {
			this.scope = scope;
			refresh(); // instantiate by locating the search box and button
		}
		
		// upon a page refresh, it is required that we locate the search box and button
		void refresh () {
			searchBox = driver.findElement(By.xpath("//*[@id='searchText']"));
			searchButton = driver.findElement(By.xpath("//*[@name='submit' and @value='search']"));
			radioButton = driver.findElement(By.xpath(scope));
		}	
	}
	
	@BeforeEach
	void setUp () {
		System.setProperty("webdriver.gecko.driver","/Users/marshmallow/Downloads/geckodriver");
		driver  = new FirefoxDriver();
		driver.get("https://cs.gmu.edu:8443/offutt/servlet/quotes.quoteserve");
	}

	// Base test ensures that given a search query with no results, the proper message is given.
	// That is to say that no results should yield and will give a message saying no quotes were found.
	
	@Test
	void searchFunctionBaseTest () {
		String input = "Search query that should yield no results";
		Query query = new Query("//*[@value='both']");
		
		query.searchBox.sendKeys(input);
		query.radioButton.click();
		query.searchButton.click();

		assertEquals("Your search - " + input + " - did not match any quotes.", driver.findElement(By.xpath("//html/body/table/tbody/tr/td/p")).getText());
	    
		driver.close();
	}
	
	// To test the empty string case, the page should simply refresh and nothing more. To verify this,
	// we compare our random quote from an initial state with a refreshed state. If they are different,
	// we know that the page has indeed, refreshed.
	@Test
	void SearchFunctionEmptyStringTest () {
		Query query = new Query("//*[@value='both']");
		String initialQuote = driver.findElement(By.xpath("//html/body/div")).getText().trim();
		initialQuote = initialQuote.substring(0, initialQuote.indexOf("\n"));
		
		// There remains the possibility that upon refresh, it's still the same quote. 
		// To combat this, we will refresh the page at most 5 times.
		// This should ensure that we will get a different quote if page indeed refreshed and gave us a different quote from the initial state.
		int randomChance = 5;
		String refreshQuote = initialQuote;
		while (initialQuote.equals(refreshQuote) && randomChance > 0) {
			query.searchBox.sendKeys(""); // empty string
			query.radioButton.click();
			query.searchButton.click();
			refreshQuote = driver.findElement(By.xpath("//html/body/div")).getText().trim();
			refreshQuote = refreshQuote.substring(0, refreshQuote.indexOf("\n"));
			query.refresh();
			randomChance--;
		}
		
		if (randomChance == 0) {
			fail("Page failed to function properly when given an empty string.");
		}
		
		driver.close();
	}
	
	// Tests the instance when given a non-empty string, there will give one and only one author as the result.
	// Our base testing strategy is concerned with search queries that yield only one author.
	@Test
	void SearchFunctionOneAuthorTest () {
		Query query = new Query("//*[@value='author']");
		int results = 0;
		// Queries that yield only one author
		String Dijkstra = "Dijkstra";
		String Channel11News = "Channel 11 news";
		
		query.searchBox.sendKeys(Dijkstra);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(1, results); // Should yield only one result

		query.refresh();
		query.searchBox.sendKeys(Channel11News);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(1, results); // Should yield only one result
	
		driver.close();
	}
	
	// Test case is similar to previous test case, however will be concerned with multiple authors.
	@Test
	void SearchFunctionMultipleAuthorTest () {
		Query query = new Query("//*[@value='author']");
		int results = 0;
		// queries with multiple authors
		String Bob = "Bob";
		String Joe = "Joe";
		
		query.searchBox.sendKeys(Bob);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(true, results > 1); // should yield more than one result
		
		query.refresh();
		query.searchBox.sendKeys(Joe);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(true, results > 1); // one more for good measure
		
		driver.close();
	}
	
	// Test case is concerning that a given a search query focused on quotes, will it return one
	// quote as it should.
	@Test
	void SearchFunctionOneQuoteTest () {
		Query query = new Query("//*[@value='quote']");
		int results = 0;
		// queries with only one quote
		String Caesar = "Veni, Vidi, Vici";
		String Nixon = "what you heard is not what I meant.";
		
		query.searchBox.sendKeys(Caesar);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(1, results); // should yield more than one result
		
		query.refresh();
		query.searchBox.sendKeys(Nixon);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(1, results); // one more for good measure
		
		driver.close();
	}
	
	
	// Test case is concerning that a given a search query focused on quotes, will it return multiple
	// quote as it should.
	@Test
	void SearchFunctionMultipleQuoteTest () {
		Query query = new Query("//*[@value='quote']");
		int results = 0;
		// queries with only one quote
		String commonPhrase1 = "he said";
		String commonPhrase2 = "I know";
		
		query.searchBox.sendKeys(commonPhrase1);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(true, results > 1); // should yield more than one result
		
		query.refresh();
		query.searchBox.sendKeys(commonPhrase2);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(true, results > 1); // one more for good measure
		
		driver.close();
	}
	
	// Test case is concerning that a given a search query focused on both an author or the quote.
	// This case specifically wants to identify a single quote from the both scope, and we should
	// be able to yield the same quote given a name or the quote itself.
	@Test
	void SearchFunctionOneBothTest () {
		Query query = new Query("//*[@value='both']");
		int results = 0;
		// queries with only one quote
		String Cunningham = "Eschew";
		String Eschew = "Cunningham";
		
		query.searchBox.sendKeys(Cunningham);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(1, results); // should yield more than one result
		
		query.refresh();
		query.searchBox.sendKeys(Eschew);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(1, results); // one more for good measure
		
		driver.close();
	}
	
	// Similar to the previous test case, we are now concerned with multiple quotes within the scope
	// of both quotes or authors. 
	@Test
	void SearchFunctionMultipleBothTest () {
		Query query = new Query("//*[@value='both']");
		int results = 0;
		// queries with only one quote
		String JeffOffutt = "Jeff Offutt";
		String AlbertEinstein = "Albert Einstein";
		String commonPhrase = "a man";
		
		query.searchBox.sendKeys(JeffOffutt);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(true, results > 1); // should yield more than one result
		
		query.refresh();
		query.searchBox.sendKeys(AlbertEinstein);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(true, results > 1); // Yet another author query
	
		query.refresh();
		query.searchBox.sendKeys(commonPhrase);
		query.radioButton.click();
		query.searchButton.click();
		results = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/dl/dt")).size();
		assertEquals(true, results > 1); // Try out the quote path
		
		driver.close();
		}
		
		
		
	@Test
	void newRandomQuoteOnRefreshOrRequest () {
		Query query = new Query("//*[@value='both']");

		//Test if random quote changes on page refresh
		String oldQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		driver.navigate().refresh();	
		String newQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		assertNotEquals(newQuote, oldQuote);
		
		
		//Test if random quote changes when requested via button
		query.refresh();
		query.searchButton.click();
		String newerQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		assertNotEquals(newerQuote, newQuote);
 
		driver.close();

	}
	@Test
	void newRandomQuoteOnRequestOnly () {
		Query query = new Query("//*[@value='both']");

		//Test if random quote does NOT change on page refresh
		String oldQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		driver.navigate().refresh();	
		String newQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		assertNotEquals(true, newQuote.equals(oldQuote) == true);
		
		
		//Test if random quote changes when requested via button
		query.refresh();
		query.searchButton.click();
		String newerQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		assertNotEquals(newerQuote, newQuote);
 
		driver.close();

	}
	@Test
	void newRandomQuoteOnRefreshOnly () {
		Query query = new Query("//*[@value='both']");

		//Test if random quote changes on page refresh
		String oldQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		driver.navigate().refresh();	
		String newQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		assertNotEquals(newQuote, oldQuote);
		
		
		//Test if random quote does NOT change when requested via button
		query.refresh();
		query.searchButton.click();
		String newerQuote = driver.findElement(By.xpath("//text()[contains(.,'')]/ancestor::div[1]")).getText();
		assertNotEquals(true, newerQuote.equals(newQuote) == true);
 
		driver.close();
	}	
		
		
	@Test
	void findLastAuthorSearched () {
		Query query = new Query("//*[@value='author']");

		String author = "Henry";
		query.searchBox.sendKeys(author);
		query.radioButton.click();
		query.searchButton.click();
		
		int max = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/table/tbody/tr/td[2]/ol/li")).size();
		String results = driver.findElement(By.xpath("//html/body/table/tbody/tr/td[3]/table/tbody/tr[2]/td[2]/ol/li["+max+"]/a")).getText();
		assertEquals(author, results);
		driver.close();
		   
	}
	
	@Test
	void findLastQuoteSearched () {
		Query query = new Query("//*[@value='quote']");

		String quote = "Element";
		query.searchBox.sendKeys(quote);
		query.radioButton.click();
		query.searchButton.click();
		
		int max = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/table/tbody/tr/td[2]/ol/li")).size();
		String results = driver.findElement(By.xpath("//html/body/table/tbody/tr/td[3]/table/tbody/tr[2]/td[2]/ol/li["+max+"]/a")).getText();
		assertEquals(quote, results);
		driver.close();
	}
	
	@Test
	void findLastBothSearched () {
		Query query = new Query("//*[@value='both']");

		String any = "Dog";
		query.searchBox.sendKeys(any);
		query.radioButton.click();
		query.searchButton.click();
		
		int max = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/table/tbody/tr/td[2]/ol/li")).size();
		String results = driver.findElement(By.xpath("//html/body/table/tbody/tr/td[3]/table/tbody/tr[2]/td[2]/ol/li["+max+"]/a")).getText();
		assertEquals(any, results);
		driver.close();
	}
	
	@Test
	void findNotLastAuthorSearched () {
		//test if the author searched is NOT the last thing to be added to the queue of searches
		Query query = new Query("//*[@value='author']");

		String author = "McCarthy";
		query.searchBox.sendKeys(author);
		query.radioButton.click();
		query.searchButton.click();
		
		int max = driver.findElements(By.xpath("//html/body/table/tbody/tr/td/table/tbody/tr/td[2]/ol/li")).size();
		String results = driver.findElement(By.xpath("//html/body/table/tbody/tr/td[3]/table/tbody/tr[2]/td[2]/ol/li["+max+"]/a")).getText();
		//if author is the last thing on queue, then test 
		assertNotEquals(false, author.equals(results) == true);
		driver.close();
	}
	
}

