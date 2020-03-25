package webService;

import java.util.ArrayList;
import java.util.List;

import tools.GenericResponse;
import tools.ProductAvailabilityItem;
import tools.SupermarketItem;

public class MarketDetailsResponse extends GenericResponse {
	private SupermarketItem supermarket;
	
	public SupermarketItem getSupermarket() {
		return supermarket;
	}
	public void setSupermarket(SupermarketItem supermarket) {
		this.supermarket = supermarket;
	}		
}
