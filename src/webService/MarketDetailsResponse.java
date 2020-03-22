package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.json_items.ProductItem;
import tools.json_items.SupermarketItem;

public class MarketDetailsResponse extends GenericResponse {
	private SupermarketItem supermarket;
	
	public SupermarketItem getSupermarket() {
		return supermarket;
	}
	public void setSupermarket(SupermarketItem supermarket) {
		this.supermarket = supermarket;
	}		
}
