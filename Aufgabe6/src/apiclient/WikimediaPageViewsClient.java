package apiclient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import data.Article;

/**
 * 
 * Allows users to fetch the most viewed wikipedia's articles
 *
 */
public class WikimediaPageViewsClient {

	/**
	 * Get back the tops 1000 articles of a given date in JSON format, filter them and save them in a List
	 * @param date
	 * @return List of tops articles
	 * @throws IOException
	 */
	public List<Article> getTopPages(LocalDate date) throws IOException {
		URL myURL = new URL("https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia.org/all-access/"
				+ date.getYear() + "/" + (date.getMonthValue() < 10 ? "0"+date.getMonthValue() : date.getMonthValue()  ) + "/" + date.getDayOfMonth());
        URLConnection myURLConnection = myURL.openConnection();
            myURLConnection.connect();
        BufferedReader br =new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
        String data=br.readLine();
        
        Gson gson = new Gson();
        JsonElement jsonElement = new JsonParser().parse(data);
        
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonElement = jsonObject.get("items");
        JsonArray jsonArray = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("articles").getAsJsonArray();
        
        ArrayList<Article> articlesList = new ArrayList<Article>();
        for(JsonElement elmt : jsonArray)
        {
        	Article articles = gson.fromJson(elmt, Article.class);
        	articlesList.add(articles);
        }
        return articlesList;
        
	}

}
