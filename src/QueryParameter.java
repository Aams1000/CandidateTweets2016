/* QueryParameter stores the key and value of a TwitterAPI query. Includes percent encoded
 * versions as well as type.
 */
public class QueryParameter {

	//parameter and key
	private String key;
	private String value;
	private String encodedKey;
	private String encodedValue;
	
	//constructor takes key and value, computes percent encoded values
	public QueryParameter(String key, String value){
		this.key = key;
		this.value = value;
		encodedKey = percentEncode(key);
		encodedValue = percentEncode(value);
	}
	
	//for some reason the import won't let this function be used without writing out the entire package in front of it
	//making function here to save space, make things easier
	private String percentEncode(String s){
		return net.oauth.OAuth.percentEncode(s);
	}
	
	//getters and setters
	public String getKey(){
		return key;
	}
	public String getValue(){
		return value;
	}
	public String getEncodedKey(){
		return encodedKey;
	}
	public String getEncodedValue(){
		return encodedValue;
	}
}
