package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.MarketStockItem;
import tools.SupermarketItem;

public class MarketScrapeResponse extends GenericResponse {
	private List<SupermarketItem> supermarket = new ArrayList<SupermarketItem>();
	private List<List<MarketStockItem>> products = new ArrayList<List<MarketStockItem>>();

	public List<SupermarketItem> getSupermarket() {
		return supermarket;
	}

	public void setSupermarket(List<SupermarketItem> supermarket) {
		this.supermarket = supermarket;
	}

	public List<List<MarketStockItem>> getProducts() {
		return products;
	}

	public void setProducts(List<List<MarketStockItem>> products) {
		this.products = products;
	}
}
