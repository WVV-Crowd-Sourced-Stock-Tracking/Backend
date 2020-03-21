package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.json_items.SupermarketItem;

public class MarketScrapeResponse extends GenericResponse {
	private List<SupermarketItem> supermarket = new ArrayList<SupermarketItem>();

	public List<SupermarketItem> getSupermarket() {
		return supermarket;
	}

	public void setSupermarket(List<SupermarketItem> supermarket) {
		this.supermarket = supermarket;
	}
}
