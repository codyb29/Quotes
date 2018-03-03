package quotes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Quote data object.
 * 
 * @author Mongkoldech Rajapakdee & Jeff offutt Date: Nov 2009 A quote has two
 *         parts, an author and a quoteText. This bean class provides getters
 *         and setters for both, plus a toString()
 */
public class Quote {
	private String author;
	private String quoteText;
	private List<String> keywords;

	// Default constructor does nothing
	public Quote() {
	}

	// Constructor that assigns both strings
	public Quote(String author, String quoteText) {
		this.author = author;
		this.quoteText = quoteText;
	}

	// Getter and setter for author
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	// Getter and setter for quoteText
	public String getQuoteText() {
		return quoteText;
	}

	public void setQuoteText(String quoteText) {
		this.quoteText = quoteText;
	}
	
	public List<String> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String keywords) {
		int max = 2 * (author.split(" ").length + quoteText.split(" ").length);
		ArrayList<String> temp;
		
		if (keywords.split(",").length > max) {
			this.keywords = Arrays.asList((keywords.split(","))).subList(0, max);
			for(int i = 0; i < this.keywords.size(); i++) {
				if(this.keywords.get(i).contains(" ") || this.keywords.subList(0, i).contains(this.keywords.get(i))) {
					temp = new ArrayList<String>(this.keywords);
					temp.remove(i--);
					this.keywords = temp;
				}
			}						
		} else {
			this.keywords = Arrays.asList((keywords.split(",")));
			for(int i = 0; i < this.keywords.size(); i++) {
				if(this.keywords.get(i).contains(" ") || this.keywords.subList(0, i).contains(this.keywords.get(i))) {
					temp = new ArrayList<String>(this.keywords);
					temp.remove(i--);
					this.keywords = temp;
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Quote {" + "author='" + author + '\'' + ", quoteText='" + quoteText + '\'' + '}';
	}
}
