package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.MarketStockItem;

public class MarketStockResponse extends GenericResponse {
	private List<MarketStockItem> products = new ArrayList<MarketStockItem>();

	public List<MarketStockItem> getProducts() {
		return products;
	}

	public void setProducts(List<MarketStockItem> products) {
		this.products = products;
	}
}
