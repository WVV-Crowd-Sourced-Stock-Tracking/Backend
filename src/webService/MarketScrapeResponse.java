package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.json_items.ProductItem;
import tools.json_items.SupermarketItem;

public class MarketScrapeResponse extends GenericResponse {
	private List<SupermarketItem> supermarket = new ArrayList<SupermarketItem>();
	private List<ProductItem> productItems = new ArrayList<ProductItem>();

	public List<SupermarketItem> getSupermarket() {
		return supermarket;
	}

	public void setSupermarket(List<SupermarketItem> supermarket) {
		this.supermarket = supermarket;
	}

	public List<ProductItem> getProductItems() {
		return productItems;
	}

	public void setProductItems(List<ProductItem> productItems) {
		this.productItems = productItems;
	}
}
