package tools;


public class MarketIcons {

	private static String rewe = "https://i.rewe-static.de/content/cms/legacy/assets/img/icons/icon_72.png";
	private static String aldi = "https://www.aldi-nord.de/etc/designs/aldi/web/frontend/aldi/images/favicons/favicon-32x32.png.res/1487002083394/favicon-32x32.png";
	private static String lidl = "https://www.lidl.de/imgs/touch-icon-ipad.png";
	private static String real = "https://www.real.de/static/dist/images/favicons/favicon-32x32.9cc43392.png";
	private static String kaufland = "https://www.kaufland.de/etc.clientlibs/kaufland/clientlibs/clientlib-site/resources/frontend/img/icon/favicon-32x32-0ef15f405f.png";
	private static String normaJPG = "https://www.kelheimer-einkaufscenter.de/upload/shops/1/1_l.jpg";
	private static String hit = "https://www.hit.de/favicon/apple-icon-72x72.png";
	private static String edeka = "https://www.edeka.de/b2c-design/global/core/assets/icons/favicon-32x32.png?version=1.7.30";
	private static String dm = "https://www.dm.de/cms/3-163-0/media/favicon/favicon-96x96.png";
	private static String spar = "https://image.jimcdn.com/app/cms/image/transf/none/path/s69216d0130bb4927/image/ied1ed00a6083069a/version/1445876320/image.png";
	
	
	public static SupermarketItem putIconURL(SupermarketItem market) {
		
		String name = market.getMarket_name().toLowerCase();
		String icon_url = market.getIcon_url();
		
		if (name.contains("rewe")) {icon_url = rewe;}
		else if (name.contains("aldi")) {icon_url = aldi;}
		else if (name.contains("lidl")) {icon_url = lidl;}
		else if (name.contains("real")) {icon_url = real;}
		else if (name.contains("kaufland")) {icon_url = kaufland;}
		else if (name.contains("norma")) {icon_url = normaJPG;}
		else if (name.contains("hit")) {icon_url = hit;}
		else if (name.contains("edeka")) {icon_url = edeka;}
		else if (name.contains("dm")) {icon_url = dm;}
		else if (name.contains("spar")) {icon_url = spar;}
		
		market.setIcon_url(icon_url);
		return market;
	}
}
