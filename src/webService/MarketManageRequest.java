package webService;

import java.util.ArrayList;
import java.util.List;

import tools.PeriodItem;
import tools.SupermarketItem;

public class MarketManageRequest {
	private String operation = "";
	private int market_id = 0;
	private String market_name = "";
	private String city = "";
	private String street = "";
	private String zip = "";
	private String longitude = "";
	private String latitude = "";
	private String maps_id = "";
	private String icon_url = "";
	private List<PeriodItem> periods = new ArrayList<PeriodItem>();
	private long osm_id = 0L;
	
	public void setEverythingButOperation(SupermarketItem market) {
		setMarket_id(market.getMarket_id());
		setMarket_name(market.getMarket_name());
		setZip(market.getZip());
		setLongitude(market.getLongitude());
		setLatitude(market.getLatitude());
		setMaps_id(market.getMaps_id());
		setStreet(market.getStreet());
		setCity(market.getCity());
		setPeriods(market.getPeriods());
		setIcon_url(market.getIcon_url());
		setOsm_id(market.getOsm_id());
	}
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public int getMarket_id() {
		return market_id;
	}
	public void setMarket_id(int market_id) {
		this.market_id = market_id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getMarket_name() {
		return market_name;
	}
	public void setMarket_name(String market_name) {
		this.market_name = market_name;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getMaps_id() {
		return maps_id;
	}
	public void setMaps_id(String maps_id) {
		this.maps_id = maps_id;
	}
	public String getIcon_url() {
		return icon_url;
	}
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}
	public List<PeriodItem> getPeriods(){
		return periods;
	}
	public void setPeriods(List<PeriodItem> periods) {
		this.periods = periods;
	}

	public long getOsm_id() {
		return osm_id;
	}

	public void setOsm_id(long osm_id) {
		this.osm_id = osm_id;
	}
	
}
