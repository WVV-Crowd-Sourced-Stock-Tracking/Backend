package webService;

public class MarketCustomersRequest {
	private String mapsId = "";
	private int customers = 0;
	private int maxLoad = 0;
	public String getMapsId() {
		return mapsId;
	}
	public void setMapsId(String mapsId) {
		this.mapsId = mapsId;
	}
	public int getCustomers() {
		return customers;
	}
	public void setCustomers(int customers) {
		this.customers = customers;
	}
	public int getMaxLoad() {
		return maxLoad;
	}
	public void setMaxLoad(int maxLoad) {
		this.maxLoad = maxLoad;
	}
}
