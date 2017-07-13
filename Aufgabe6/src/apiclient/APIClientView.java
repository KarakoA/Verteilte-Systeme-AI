package apiclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

import data.Article;

/**
 * 
 * Shows the received articles in the console.
 * First show the top 10 and allow user to navigate through the rest
 *
 */
public class APIClientView {

	public static void main(String[] args) throws IOException {
		int i;
		List<Article> articlesList = new WikimediaPageViewsClient().getTopPages(LocalDate.of(2016, 12, 25));
		  for(i=0; i<10; i++)
	        {
	        	System.out.println(articlesList.get(i));
	        	System.out.println();
	        }
	        while(true)
	        {
	        	System.out.println("\t(n) Next\t(q) Quit ");
	        	BufferedReader buReader = new BufferedReader( new InputStreamReader(System.in));
	        	String eingabe = buReader.readLine();
	        	if(eingabe.startsWith("n"))
	        	{
	        		System.out.println(articlesList.get(++i));
	        	}
	        	else if(eingabe.startsWith("q"))
	        		break;		
	        }

	}

}
