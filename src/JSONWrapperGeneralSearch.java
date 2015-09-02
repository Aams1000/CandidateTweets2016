import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//wrapper class for general TweetSearch. Handles searches for hashtags
@JsonIgnoreProperties (ignoreUnknown = true)
public class JSONWrapperGeneralSearch {
	
	//contains array of Tweets
	private Tweet[] tweets;
	
	@JsonCreator
	public JSONWrapperGeneralSearch(@JsonProperty("statuses") Tweet[] tweets){
		this.tweets = tweets;
	}
	
	//getters and setters
	public Tweet[] getTweets(){
		return tweets;
	}

}
