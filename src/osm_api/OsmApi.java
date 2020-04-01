package osm_api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tools.SupermarketItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

public class OsmApi {
	public static void addGoogleMapsId(SupermarketItem market) {
		try {
			JsonNode actualObj = google_api.mapsApi.scrapeAreaForMarkets(market.getLongitude(), market.getLatitude(), 200);
			
			Map<Double, SupermarketItem> googleItemsMap = new HashMap();
			
			if (actualObj != null) {
				for(final JsonNode objNode : actualObj) {
					SupermarketItem item = new SupermarketItem();
					
					String mapsId = objNode.path("id").asText();
					String distance = objNode.path("distance").asText();
					String name = objNode.path("name").asText();
					
					item.setMaps_id(mapsId);
					item.setDistance(distance);
					item.setMarket_name(name);
					
					double similarity = similarity(market.getMarket_name(), name);
					googleItemsMap.put(similarity, item);
				}
				List<Entry<Double, SupermarketItem>> googleItemsList = new ArrayList<>(googleItemsMap.entrySet());
				googleItemsList.sort(Entry.comparingByKey());
				if(googleItemsList.size() > 0) {
					market.setMaps_id(googleItemsList.get(0).getValue().getMaps_id());
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static double similarity(String s1, String s2) {
	    String longer = s1, shorter = s2;
	    if (s1.length() < s2.length()) { // longer should always have greater length
	      longer = s2; shorter = s1;
	    }
	    int longerLength = longer.length();
	    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
	    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
	    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
	    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	  }

	  private static int editDistance(String s1, String s2) {
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }

}
