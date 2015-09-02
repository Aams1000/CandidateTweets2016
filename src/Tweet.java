import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {

	//all important tweet information, maybe a properties object?
	
	@JsonCreator
	public Tweet(){
		
	}
}
