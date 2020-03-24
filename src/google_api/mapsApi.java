package google_api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class mapsApi {
	private static String GET_URL_SCRAPE =  "http://3.120.206.89/";
	private static  String USER_AGENT = "Mozilla/5.0";
	
	public static JsonNode scrapeAreaForMarkets(String longitude, String latitude, int radius) throws IOException, MalformedURLException, ProtocolException {
		// Get Supermarket IDs via API			
		String call_url = GET_URL_SCRAPE + "markets?latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius; 
		URL obj = new URL(call_url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = conn.getResponseCode();
		System.out.println("Maps API Scrape Area - GET Response Code :: " + responseCode);
		
		JsonNode actualObj = null;
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			InputStream in = conn.getInputStream();
		    StringBuilder textBuilder = new StringBuilder();
		    try (Reader reader = new BufferedReader(new InputStreamReader
		      (in, Charset.forName(StandardCharsets.UTF_8.name())))) {
		        int c = 0;
		        while ((c = reader.read()) != -1) {
		            textBuilder.append((char) c);
		        }
		    }
			ObjectMapper mapper = new ObjectMapper();
		    actualObj = mapper.readTree(textBuilder.toString());
		    if (actualObj.isArray()) {
		        for (final JsonNode objNode : actualObj) {
		            System.out.println(objNode);
		            JsonNode n = objNode.path("name");
		            String a = n.asText();
		            System.out.println(a);
		        }
		    }
		}
		return actualObj;
	}
	
	public static JsonNode getPlaceDetails(String googlePlaceID) throws IOException, MalformedURLException, ProtocolException{
		
		String call_url = GET_URL_SCRAPE + "market?place_id=" + googlePlaceID;
		URL obj = new URL(call_url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = conn.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		JsonNode objNode = null;
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			InputStream in = conn.getInputStream();
			
		    StringBuilder textBuilder = new StringBuilder();
		    try (Reader reader = new BufferedReader(new InputStreamReader
		      (in, Charset.forName(StandardCharsets.UTF_8.name())))) {
		        int c = 0;
		        while ((c = reader.read()) != -1) {
		            textBuilder.append((char) c);
		        }
		    }
		    
			ObjectMapper mapper = new ObjectMapper();
		   objNode = mapper.readTree(textBuilder.toString());		
		}
		return objNode;
	}
}
	
