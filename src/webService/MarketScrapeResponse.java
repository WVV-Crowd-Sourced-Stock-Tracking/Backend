package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.ProductAvailabilityItem;
import tools.SupermarketItem;

public class MarketScrapeResponse extends GenericResponse {
	private List<SupermarketItem> supermarket = new ArrayList<SupermarketItem>();
	private List<List<ProductAvailabilityItem>> productItems = new ArrayList<List<ProductAvailabilityItem>>();

	public List<SupermarketItem> getSupermarket() {
		return supermarket;
	}

	public void setSupermarket(List<SupermarketItem> supermarket) {
		this.supermarket = supermarket;
	}

	public List<List<ProductAvailabilityItem>> getProductItems() {
		return productItems;
	}

	public void setProductItems(List<List<ProductAvailabilityItem>> productItems) {
		this.productItems = productItems;
	}
}
