import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnalyzeTweets {
	
	//global objects to hold Tweets
	private static Tweet[] tweets;
	
	//variables for threading API calls
	private static final int NUM_THREADS = 3;
	
	//variables for mapping JSON to objects
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static void main(String[] args) {
		tweets = retrieveTweetData("Obama");
		System.out.println("Number of tweets: " + tweets.length);
	}
	
	//retrieveTweetData pulls tweet data from API, stores it in 
	public static Tweet[] retrieveTweetData(String searchParameter){
		Tweet[] newTweets = null;
		ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		//test Callable and Future on TweetSearch
		Callable<String> testSearch = new TweetSearch(searchParameter);
		Future<String> searchResult = threadPool.submit(testSearch);
		String result = "";
		try{
			result = searchResult.get();
			JSONWrapperGeneralSearch tweetsWrapper = mapper.readValue(result, JSONWrapperGeneralSearch.class);
			newTweets = tweetsWrapper.getTweets();
		}
		catch (IOException | ExecutionException | InterruptedException ex){
			System.out.println("Tweet retrival or mapping failed: ");
			ex.printStackTrace();
		}
		finally{
			threadPool.shutdown();
			try{
				threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			}
			catch (InterruptedException ex){
				System.out.println("ExecutorService shutdown failed: ");
				ex.printStackTrace();
			}
		}
		return newTweets;
	}
}
