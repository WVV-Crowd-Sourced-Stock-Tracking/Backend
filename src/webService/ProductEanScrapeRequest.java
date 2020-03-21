package webService;

public class ProductEanScrapeRequest {
	private String ean = "";
	private String language = "";		//Maybe sometimes

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
