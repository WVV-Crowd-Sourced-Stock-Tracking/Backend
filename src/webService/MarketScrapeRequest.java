package webService;

import java.util.ArrayList;
import java.util.List;

public class MarketScrapeRequest {
	private String zip = "";
	private String gps_length = "";
	private String gps_width = "";
	private int radius = 0;				//UoM = meter
	private List<Integer> product_id = new ArrayList<Integer>();
	private boolean details_requested = true;
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getGps_length() {
		return gps_length;
	}
	public void setGps_length(String gps_length) {
		this.gps_length = gps_length;
	}
	public String getGps_width() {
		return gps_width;
	}
	public void setGps_width(String gps_width) {
		this.gps_width = gps_width;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public List<Integer> getProduct_id() {
		return product_id;
	}
	public void setProduct_id(List<Integer> product_id) {
		this.product_id = product_id;
	}
	public boolean isDetails_requested() {
		return details_requested;
	}
	public void setDetails_requested(boolean details_requested) {
		this.details_requested = details_requested;
	}	
	
}
