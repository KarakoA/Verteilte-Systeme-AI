package data;

/**
 * 
 * Define an article from Wikipedia
 *
 */
public class Article {
	
	private String article;
	private long views;
	private int rank;
	
	public Article(String article, long views, int rank)
	{
		this.article = article;
		this.views = views;
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "Article: " + article + ", Views: " + views + ", Rank: " + rank
				+ "\nhttps://en.wikipedia.org/wiki/"+article;
	}

}
