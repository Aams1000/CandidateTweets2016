import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TweetSearch implements Callable<String> {

	//API locations and variables
	private final String API_ADDRESS = "https://api.twitter.com/1.1/search/tweets.json";
	private final String SEARCH_SIGNIFIER = "?";
	private final String SEARCH_FIELD_LABEL = "q";
	private final String SEARCH_EQUALS = "=";
	private final String HTTP_METHOD = "GET";
	
	//Oauth authorization info
	private final String OAUTH_CONSUMER_KEY_LABEL = "oauth_consumer_key";
	private final String OAUTH_NONCE_LABEL = "oauth_nonce";
	private final String OAUTH_SIGNATURE_LABEL = "oauth_signature";
	private final String OAUTH_SIGNATURE_METHOD_LABEL = "oauth_signature_method";
	private final String OAUTH_TIMESTAMP_LABEL = "oauth_timestamp";
	private final String OAUTH_TOKEN_LABEL = "oauth_token";
	private final String OAUTH_VERSION_LABEL = "oauth_version";
	private final String OAUTH_CONSUMER_KEY_VALUE = "zKaaBprvr6EahzKxYpIyloF2O";
	private final String OAUTH_SIGNATURE_METHOD_VALUE = "HMAC-SHA1";
	private final String OAUTH_TOKEN_VALUE = "444352102-UhXng5txkHcddZ742O6EUFvwzUWSjkNULNwOvaUD";
	private final String OAUTH_VERSION_VALUE = "1.0";
	private final String OAUTH_CONSUMER_SECRET_VALUE = "0ImkbCzNBUgWLN2CE7Nxm93ehsJPBmg75XmFeZhWLT3mgId25l";
	private final String OAUTH_ACCESS_TOKEN_SECRET_VALUE = "GrsAMOpJxuN4PyUmIQ4pImO5K6KlpiOjSrRXyyZgJU1Ab";
	private String OAUTH_NONCE_VALUE;
	private String OAUTH_SIGNATURE_VALUE;
	private String OAUTH_TIMESTAMP_VALUE;
	
	//values for constructing header
	private final String OAUTH_HEADER_LABEL = "Authorization";
	private final String OAUTH_BEGIN = "OAuth ";
	private final String OAUTH_SPACE = " ";
	private final String OAUTH_SEPARATOR = ", ";
	private final String OAUTH_VALUE_WRAPPER = "\"";
	private final String OAUTH_EQUALS = "=";
	private final String OAUTH_NEW_FIELD = "&";
	
	//variables for constructing OAUTH_SIGNATURE
	private final String OAUTH_HMAC_ALGORITHM = "HmacSHA1";
	
	//parameter to search
	private String searchParameter;
	private String query;
	
	//list of QueryParameters
	private ArrayList<QueryParameter> parameterList;
	
	//random number generator
	private Random rand = new Random();
	
	//constructor for testing requests
	public TweetSearch(String searchParameter){
		this.searchParameter = searchParameter;
		query = constructUrl(searchParameter);
		System.out.println(generateAuthorizationHeader());

		System.out.println(queryAPI());
//		String testResult = queryAPI();
//		Object jsonString;
//		try {
//			jsonString = mapper.readValue(testResult, Object.class);
//			String testJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonString);
//			System.out.println(testJson);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
				
//		Object json = mapper.readValue(input, Object.class);
//
//		and then write it out with indentation:
//
//		String indented = mapper.defaultPrettyPrintingWriter().writeValueAsString(json);
		
	}
	
	//call generates Oauth authorization header, queries api
	public String call(){
		String result = queryAPI();
		return result;
	}
	
	//queryAPI uses url and Oauth authorization header to pull JSON from server
	private String queryAPI(){
		String result = "";
		try{
			result = Request.Get(query)
					.addHeader(OAUTH_HEADER_LABEL, generateAuthorizationHeader())
					.execute().returnContent().asString();
			//result = request.execute().returnContent().asString();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		return result;
	}
//	HttpClient client = HttpClientBuilder.create().build();
//    HttpGet request = new HttpGet("https://www.google.com/?q=java");
//    try {
//        HttpResponse response = client.execute(request);
//        System.out.println(response.getStatusLine());

	
//	//request movie profile from OMDB
//			try{
//				result = Request.Get(url).execute().returnContent().asString();
//			}
//			catch(IOException ex){
//				ex.printStackTrace();
//			}
	//generateAuthorizationHeader takes Oauth tokens and access keys, constructs header. Returns string.
	private String generateAuthorizationHeader(){
		//initialize OAUTH_NONCE_VALUE and OAUTH_TIMESTAMP_VALUE, generate OAUTH_SIGNATURE_VALUE
		OAUTH_NONCE_VALUE = generateOauthNonce();
		OAUTH_TIMESTAMP_VALUE = getTimestamp();
		OAUTH_SIGNATURE_VALUE = generateOauthSignature();
		//append parameters to header
		StringBuffer header = new StringBuffer();
		header.append(OAUTH_BEGIN);
		header.append(percentEncode(OAUTH_CONSUMER_KEY_LABEL)); //OAUTH_CONSUMER_KEY
		header.append(OAUTH_EQUALS);
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(percentEncode(OAUTH_CONSUMER_KEY_VALUE));
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(OAUTH_SEPARATOR);
		header.append(percentEncode(OAUTH_NONCE_LABEL)); //OAUTH_NONCE
		header.append(OAUTH_EQUALS);
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(percentEncode(OAUTH_NONCE_VALUE));
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(OAUTH_SEPARATOR);
		header.append(percentEncode(OAUTH_SIGNATURE_LABEL)); //OAUTH_SIGNATURE
		header.append(OAUTH_EQUALS);
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(percentEncode(OAUTH_SIGNATURE_VALUE));
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(OAUTH_SEPARATOR);
		header.append(percentEncode(OAUTH_SIGNATURE_METHOD_LABEL)); //OAUTH_SIGNATURE_METHOD
		header.append(OAUTH_EQUALS);
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(percentEncode(OAUTH_SIGNATURE_METHOD_VALUE));
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(OAUTH_SEPARATOR);
		header.append(percentEncode(OAUTH_TIMESTAMP_LABEL)); //OAUTH_TIMESTAMP
		header.append(OAUTH_EQUALS);
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(percentEncode(OAUTH_TIMESTAMP_VALUE));
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(OAUTH_SEPARATOR);
		header.append(percentEncode(OAUTH_TOKEN_LABEL)); //OAUTH_TOKEN
		header.append(OAUTH_EQUALS);
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(percentEncode(OAUTH_TOKEN_VALUE));
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(OAUTH_SEPARATOR);
		header.append(percentEncode(OAUTH_VERSION_LABEL)); //OAUTH_VERSION
		header.append(OAUTH_EQUALS);
		header.append(OAUTH_VALUE_WRAPPER);
		header.append(percentEncode(OAUTH_VERSION_VALUE));
		header.append(OAUTH_VALUE_WRAPPER);
		
		return header.toString();
	}
	
	//random string generator for creating oauth-nonce
	private String generateOauthNonce(){
		final char FIRST_CHAR = 'a';
		final int TOTAL_CHARS = 25;
		final int STRING_LENGTH = 42;
		StringBuffer random = new StringBuffer();
		for (int i = 0; i < STRING_LENGTH; i++){
			char randomChar = (char) (rand.nextInt(TOTAL_CHARS) + FIRST_CHAR);
			//System.out.println("Random char: " + randomChar);
			random.append(randomChar);
		}
		return random.toString();
	}
		
	//generateOauthSignature creates OAUTH_SIGNATURE_VALUE from other Oauth values and query parameters
	private String generateOauthSignature(){
		//create parameterString
		parameterList = storeQueryParameters();
		String oauthParameterString = generateParameterString(parameterList);
		//System.out.println(oauthParameterString);
		String oauthSignatureBaseString = generateSignatureBaseString(oauthParameterString);
		//System.out.println(oauthSignatureBaseString);
		String oauthSigningKey = generateSigningKey(OAUTH_CONSUMER_SECRET_VALUE, OAUTH_ACCESS_TOKEN_SECRET_VALUE);
		//System.out.println(oauthSigningKey);
		String oauthSignature= generateSignature(oauthSigningKey, oauthSignatureBaseString);
		return oauthSignature;
	}
	
	//generateParameterString creates parameter String from Oauath values and query parameters
	private String generateParameterString(ArrayList<QueryParameter> parameterList){
		StringBuffer result = new StringBuffer();
		//sort encoded parameterList
		Collections.sort(parameterList, new QueryParameterComparator());
		for (int i = 0; i < parameterList.size(); i++){
			QueryParameter curr = parameterList.get(i);
			result.append(curr.getEncodedKey());
			result.append(OAUTH_EQUALS);
			result.append(curr.getEncodedValue());
			if (i + 1 < parameterList.size())
				result.append(OAUTH_NEW_FIELD);
		}
		return result.toString();
	}
	
	//generateSignatureBaseString creates the oauthSignaureBaseString for OAUTH_SIGNATURE. Takes the parameter string
	//as parameter, returns String
	private String generateSignatureBaseString(String parameterString){
		StringBuffer result = new StringBuffer();
		//add HTTP_METHOD, encoded base url, and encoded parameterString to buffer
		result.append(HTTP_METHOD.toUpperCase());
		result.append(OAUTH_NEW_FIELD);
		result.append(percentEncode(API_ADDRESS));
		result.append(OAUTH_NEW_FIELD);
		result.append(percentEncode(parameterString));
		return result.toString();
	}
	
	//generateSigningKey takes OAUTH_CONSUMER_SECRET_VALUE and OAUTH_ACCESS_TOKEN_SECRET_VALUE to create signing key. Returns String
	private String generateSigningKey(String consumerSecret, String accessTokenSecret){
		StringBuffer result = new StringBuffer();
		//add encoded consumerSecret followed by encoded accessTokenSecret
		result.append(percentEncode(consumerSecret));
		result.append(OAUTH_NEW_FIELD);
		result.append(percentEncode(accessTokenSecret));
		return result.toString();
	}
	
	//generateSignature creates HMAC-SHA1 hash from oauthSignatureBaseString and oauthSigningKey. Returns String
	private String generateSignature(String key, String value){
		String result = "";
		try {
			//get signed key from key's bytes
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signedKey = new SecretKeySpec(keyBytes, OAUTH_HMAC_ALGORITHM);
			
			//initialize Mac with signed key
			Mac mac = Mac.getInstance(OAUTH_HMAC_ALGORITHM);
			mac.init(signedKey);
			//find hmac from value
			byte[] rawHmac = mac.doFinal(value.getBytes());
			Encoder encoder = Base64.getEncoder();
			result = encoder.encodeToString(rawHmac);
			return result;
		}
		catch (IllegalArgumentException ex){
			ex.printStackTrace();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	//storeQueryParameters creates a list of all QueryParameters EXCEPT OAUTH_SIGNATURE. This list is needed in order to
	//create OAUTH_SIGNATURE
	private ArrayList<QueryParameter> storeQueryParameters(){
		ArrayList<QueryParameter> parameterList = new ArrayList<QueryParameter>();
		//make sure OAUTH_NONCE and OAUTH_TIMESTAMP have been initialized
		if (OAUTH_NONCE_VALUE == null)
			OAUTH_NONCE_VALUE = generateOauthNonce();
		if (OAUTH_TIMESTAMP_VALUE == null)
			OAUTH_TIMESTAMP_VALUE = getTimestamp();
		//create list of QueryParameters
		QueryParameter parameterOne = new QueryParameter(OAUTH_CONSUMER_KEY_LABEL, OAUTH_CONSUMER_KEY_VALUE);
		parameterList.add(parameterOne);
		QueryParameter parameterTwo = new QueryParameter(OAUTH_NONCE_LABEL, OAUTH_NONCE_VALUE);
		parameterList.add(parameterTwo);
		QueryParameter parameterThree = new QueryParameter(OAUTH_SIGNATURE_METHOD_LABEL, OAUTH_SIGNATURE_METHOD_VALUE);
		parameterList.add(parameterThree);
		QueryParameter parameterFour = new QueryParameter(OAUTH_TIMESTAMP_LABEL, OAUTH_TIMESTAMP_VALUE);
		parameterList.add(parameterFour);
		QueryParameter parameterFive = new QueryParameter(OAUTH_TOKEN_LABEL, OAUTH_TOKEN_VALUE);
		parameterList.add(parameterFive);
		QueryParameter parameterSix = new QueryParameter(OAUTH_VERSION_LABEL, OAUTH_VERSION_VALUE);
		parameterList.add(parameterSix);
		QueryParameter parameterSeven = new QueryParameter(SEARCH_FIELD_LABEL, searchParameter);
		parameterList.add(parameterSeven);
		return parameterList;
	}

	//variables for testing google oauth
//	private JsonFactory jsonFactory = new JacksonFactory();
//	private HttpTransport transport = new ApacheHttpTransport();
//	

	
//	private HttpResponse executeGet(HttpTransport transport, JsonFactory jsonFactory, String accessToken, GenericUrl url) throws IOException {
//		    Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
//		    HttpRequestFactory requestFactory = transport.createRequestFactory(credential);
//		    return requestFactory.buildGetRequest(url).execute();
//	}
	
	//for some reason the import won't let this function be used without writing out the entire package in front of it
	//making function here to save space, make things easier
	private String percentEncode(String s){
		return net.oauth.OAuth.percentEncode(s);
	}
	
	//getTimestamp returns current timestamp as String
	private String getTimestamp(){
		final long SECONDS_CONVERSION = 1000;
		String timestamp = Long.toString((new Date().getTime() / SECONDS_CONVERSION));
		return timestamp;
	}
	
	//constructUrl function builds query url as String
	private String constructUrl(String searchParameter){
		StringBuffer query = new StringBuffer();
		query.append(API_ADDRESS);
		query.append(SEARCH_SIGNIFIER);
		query.append(SEARCH_FIELD_LABEL);
		query.append(SEARCH_EQUALS);
		query.append(percentEncode(searchParameter));
		return query.toString();
	}
	
	//QueryParameterComparator sorts QueryParameters alphabetically by encoded key
	class QueryParameterComparator implements Comparator<QueryParameter>{
		public int compare(QueryParameter a, QueryParameter b){
			if (a.getEncodedKey().compareTo(b.getEncodedKey()) < 0)
				return -1;
			if (a.getEncodedKey().compareTo(b.getEncodedKey()) == 0)
				return 0;
			return 1;
		}
	}
}

