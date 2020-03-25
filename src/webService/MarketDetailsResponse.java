package webService;


import tools.GenericResponse;
import tools.SupermarketItem;

public class MarketDetailsResponse extends GenericResponse {
	private SupermarketItem supermarket = new SupermarketItem();
	
	public SupermarketItem getSupermarket() {
		return supermarket;
	}
	public void setSupermarket(SupermarketItem supermarket) {
		this.supermarket = supermarket;
	}		
}
