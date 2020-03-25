package webService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.fasterxml.jackson.databind.JsonNode;

import tools.GenericResponse;
import tools.Location;
import tools.MarketStockItem;
import tools.MarketTransmitItem;
import tools.ProductItem;
import tools.SupermarketItem;

@Path("/rest")
public class Rest extends RestBasis {
	private static final long serialVersionUID = 2L;

	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/market/transmit
	 *  JSON input 1
	 	{ 
			"market_id": 1,
			"product_id": 1,
			"quantity": 100
		}
	 *  JSON input 2
		{
		      "bulk":[{"market_id": 18,
		          "product_id": 1,
		           "quantity": 50
		      },
		      {"market_id": 18,
		          "product_id": 32,
		           "quantity": 100
		      }]
		}		
	 *  JSON output
	   	{
   			"result": "success"
		}
	 */
	@POST
	@Path("/market/transmit")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response marketTransmit(@Context HttpServletRequest request, MarketTransmitRequest req) {
		Response response = null;
		Connection con = null;
		GenericResponse res = new GenericResponse();
		try {
			con = initWS();
			if ( req.getMarket_id() > 0 && req.getProduct_id() > 0) {
				saveTransmit( con, req.getMarket_id(), req.getProduct_id(), req.getQuantity() );
			}
			if ( req.getBulk().size() > 0 ) {
				for( MarketTransmitItem i : req.getBulk() ) {
					saveTransmit( con, i.getMarket_id(), i.getProduct_id(), i.getAvailability() );
				}
			}
			con.commit();
			res.setResult("success");
		} catch (Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult("Exception " + ex.getMessage());
		} finally {
			finallyWs(con);
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}
	
	private static final int MAX_HISTORY = 5;			//Max # of records in stock_history for one stock/product
	private void saveTransmit( Connection con, int market_id, int product_id, int quantity) {
		int anz = 0;
		int sum = 0;
		Timestamp minTs = null;
		
		PreparedStatement pstmt = null;
		try {
			String sql = "select ts,quantity from stock_history where store_id=? and product_id=? order by ts desc";
			pstmt = con.prepareStatement( sql );
			pstmt.setInt(1, market_id);
			pstmt.setInt(2, product_id);
			ResultSet rs = pstmt.executeQuery();
			
			while( rs.next() && anz < MAX_HISTORY) {
				minTs = rs.getTimestamp(1);
				if ( ++anz < MAX_HISTORY ) {			//Only n-1 and act. quantity into sum 
					sum += rs.getInt(2);
				}
			}
			rs.close();
			pstmt.close();	
			if ( anz == MAX_HISTORY ) {
				pstmt = con.prepareStatement("delete from stock_history where store_id=? and product_id=? " +
											 "and ts <= ?");
				pstmt.setInt(1, market_id);
				pstmt.setInt(2, product_id);
				pstmt.setTimestamp(3, minTs);
				pstmt.executeUpdate();
				pstmt.close();
			}
			else {								//We are not at the max # of records 
				anz++;
			}
			sum += quantity;
			
			pstmt = con.prepareStatement("insert into stock_history(store_id,product_id,quantity) values(?,?,?) " +
										 "on conflict(store_id,product_id,ts) do update set quantity = ?");
			pstmt.setInt(1, market_id);
			pstmt.setInt(2, product_id);
			pstmt.setInt(3, quantity);
			pstmt.setInt(4, quantity);
			pstmt.executeUpdate();
			pstmt.close();
			
			quantity = sum/anz;
			
			pstmt = con.prepareStatement("insert into stock(store_id,product_id,quantity) values(?,?,?)" +
					 					 "on conflict(store_id,product_id) do update set quantity = ?");
			pstmt.setInt(1, market_id);
			pstmt.setInt(2, product_id);
			pstmt.setInt(3, quantity);
			pstmt.setInt(4, quantity);
			pstmt.executeUpdate();
			pstmt.close();

		} catch (SQLException ex) {
			if ( pstmt != null ) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
	}

	@HEAD
	@Path("/market/transmit")
	public Response marketTransmitHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}
	
	@OPTIONS
	@Path("/market/transmit")
	public Response marketTransmitOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/market/scrape JSON input {
	 * "zip":"61231", "radius": 12000, "product_id": [ 1, 2 ] } JSON output {
	 * "result": "success", "supermarket": [ { "id": 0, "name": "REWA Center Bad
	 * Nauheim", "city": "Bad Nauheim", "street": "Georg-Scheller-StraÃŸe 2-8",
	 * "longitude": "8.754167", "latitude": "50.361944" }] }
	 */
	@POST
	@Path("/market/scrape")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response marketScrape(@Context HttpServletRequest request, MarketScrapeRequest req) {
		Response response = null;
		Connection con = null;
		MarketScrapeResponse res = new MarketScrapeResponse();
		try {
			con = initWS();
			
			String zipString = req.getZip();
			String longitude = req.getLongitude();
			String latitude = req.getLatitude();
			
			ResultSet rsMarkets = null;
			ResultSet rsProducts = null;
			List<SupermarketItem> marketList = new ArrayList<SupermarketItem>();
			
			if (zipString.length() > 0 && (longitude.length() == 0 || latitude.length() == 0)) {
				//TODO SQL Alle mit gleicher Zip
				int zip = Integer.parseInt(zipString);
				
				PreparedStatement pstmt = null;
				pstmt = con.prepareStatement("select * from (store inner join location on store.location_id = location.location_id ) where location.zip=?");
				pstmt.setInt(1, zip);
				rsMarkets = pstmt.executeQuery();
				
				
				while( rsMarkets.next() ) {
					
					SupermarketItem supermarketItem = new SupermarketItem();
					supermarketItem.setMaps_id(rsMarkets.getString("google_id"));
					supermarketItem.setMarket_id(rsMarkets.getInt("store_id"));
					supermarketItem.setMarket_name(rsMarkets.getString("name"));
					supermarketItem.setCity(rsMarkets.getString("city"));
					supermarketItem.setLongitude(rsMarkets.getString("longitude"));
					supermarketItem.setLatitude(rsMarkets.getString("latitude"));
					supermarketItem.setStreet(rsMarkets.getString("street"));
					supermarketItem.setZip(rsMarkets.getInt("zip"));
					marketList.add(supermarketItem);					
				}
				res.setSupermarket(marketList);
				pstmt.close();
			}
			else if (longitude != null && latitude != null) {
				int radius = req.getRadius();
				// Get Supermarket IDs via API			
				
				JsonNode actualObj = google_api.mapsApi.scrapeAreaForMarkets(longitude, latitude, radius);
				 
				if (actualObj != null) {
					for(final JsonNode objNode : actualObj) {
						SupermarketItem market = new SupermarketItem();
						market.setMarket_name(objNode.path("name").asText());
						market.setMaps_id(objNode.path("id").asText());
						market.setDistance(objNode.path("distance").asText());
						market.setOpen(objNode.path("open_now").asBoolean());
						if(marketIsInDb( con, market)) {
							// load market from db
							
							String sql = "select l.zip, l.city, l.street, l.longitude, l.latitude from store s, location l "
									+ "where s.store_id = ? and l.location_id=s.location_id";
							PreparedStatement pstmt2 = null;
							pstmt2 = con.prepareStatement( sql );
							pstmt2.setInt(1, market.getMarket_id());
							ResultSet rs = pstmt2.executeQuery();
							Location location = new Location();
							if ( rs.next() ) {
								location.setZip(rs.getInt(1));
								location.setCity(rs.getString(2));
								location.setStreet(rs.getString(3));
								location.setLongitude(rs.getString(4));
								location.setLatitude(rs.getString(5));
							}
							rs.close();
							pstmt2.close();
							
							if (!(location.getZip()>500)) {
								//get zip and address-info from API
								location = apiGetLocation(market.getMaps_id(), location);
								location.setLongitude(objNode.findPath("longitude").asText());
								location.setLatitude(objNode.findPath("latitude").asText());
								
								MarketManageRequest mmr = new MarketManageRequest();
								market.setLocation(location);
								
								mmr.setOperation("modify");
								mmr.setMarket_id(market.getMarket_id());
								mmr.setMarket_name(market.getMarket_name());
								mmr.setZip(Integer.toString(location.getZip()));
								mmr.setLongitude(location.getLongitude());
								mmr.setLatitude(location.getLatitude());
								mmr.setMaps_id(market.getMaps_id());
								mmr.setStreet(location.getStreet());
								mmr.setCity(location.getCity());
								marketManageEdit(con, mmr);
								market.setMarket_id( mmr.getMarket_id() );
							}
							
							market.setLocation(location);
							
						} else {
							// add market to db
							MarketManageRequest mmr = new MarketManageRequest();
							Location location = new Location();
							location.setLongitude(objNode.findPath("longitude").asText());
							location.setLatitude(objNode.findPath("latitude").asText());
							location = apiGetLocation(market.getMaps_id(), location);
							market.setLocation(location);
							
							mmr.setOperation("create");
							mmr.setMarket_name(market.getMarket_name());
							mmr.setZip(Integer.toString(location.getZip()));
							mmr.setLongitude(location.getLongitude());
							mmr.setLatitude(location.getLatitude());
							mmr.setStreet(location.getStreet());
							mmr.setMaps_id(market.getMaps_id());
							mmr.setCity(location.getCity());
							marketManageAdd( con, mmr );
							market.setMarket_id( mmr.getMarket_id() );
						}
						
						marketList.add(market);						
					}
				}				
				res.setSupermarket(marketList);
			}
			
			//load products if requested
			if(req.isDetails_requested()) {
				//Unterscheidung, ob mit Produktfilter oder ohne
				String sqlFilter;
				if (req.getProduct_id().size() > 0) {
					//gefilterte sql Abfrage-Kriterien vorbereiten
					Iterator<Integer> productIDIterator = req.getProduct_id().iterator();
					StringBuilder sb = new StringBuilder();
					int currID;
					sb.append(" and ( ");
					while (productIDIterator.hasNext()) {
						currID = productIDIterator.next();
						sb.append("p.product_id=").append(currID);
						if (productIDIterator.hasNext()) {sb.append(" or ");}
					}
					sb.append(") ");
					sqlFilter = sb.toString();							
				} else {
					//alle Produkte abfragen anstatt gefiltert.
					sqlFilter = "";
				}
				
				//TODO alle Produkte(entsprechend der Filterung oder ohne Filterung) der Supermï¿½rkte aus Datenbank suchen und zurueckgeben
				SupermarketItem currMarket;
				List<List<tools.ProductAvailabilityItem>> productsInMarkets = new ArrayList<List<tools.ProductAvailabilityItem>>();
				Iterator<SupermarketItem> marketIterator = marketList.iterator();
				List<tools.ProductAvailabilityItem> singleMarketProducts = null;
				
				//TODO - Fuelle singleMarketProducts-Liste mit den angefragten Produkten
				
				while (marketIterator.hasNext()) {
					currMarket = marketIterator.next();
					singleMarketProducts = new ArrayList<tools.ProductAvailabilityItem>();
					//Create ProductID-String for sql query
					PreparedStatement pstmt = null;
					pstmt = con.prepareStatement("select p.product_id, p.name, s.quantity from product p, stock s where p.product_id=s.product_id and s.store_id=? " + sqlFilter + " order by s.quantity desc");
					pstmt.setInt(1, currMarket.getMarket_id());
					rsProducts = pstmt.executeQuery();
					
					//TODO einzele Auslese in singleMarketProduts-Liste packen und anschließend diese in productsInMarket-Liste
					while( rsProducts.next() ) {
						tools.ProductAvailabilityItem jsonProductItem = new tools.ProductAvailabilityItem(rsProducts.getInt(1),rsProducts.getString(2),rsProducts.getInt(3));
						singleMarketProducts.add(jsonProductItem);
					}
					rsProducts.close();
					pstmt.close();
					currMarket.setProducts(singleMarketProducts);
					productsInMarkets.add(singleMarketProducts);		
				} 
				
				res.setProductItems(productsInMarkets);
			}	
			res.setResult("success");
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}
	
	//TODO Google API fuer zip und AdressInfos
	private Location apiGetLocation(String google_id, Location location) throws IOException {

		JsonNode objNode = google_api.mapsApi.getPlaceDetails(google_id);
		if (objNode != null) {
			String street = objNode.path("route").asText() + " " + objNode.findPath("street_number").asText();
			location.setStreet(street);
			location.setCity(objNode.findPath("locality").asText());
			location.setZip(objNode.findPath("postal_code").asInt());
		}				   
		return location;   
	}
	
	
	
	private boolean marketIsInDb(Connection con, SupermarketItem market) {
		try {
			String sql = "";
			PreparedStatement pstmt = null;
				sql = "select store.store_id from store "
					+ "where store.google_id = ?";
				pstmt = con.prepareStatement( sql );
				pstmt.setString(1, market.getMaps_id());
				
				ResultSet rs = pstmt.executeQuery();
				int count = 0;
				int storeId = 0;
				while(rs.next()) {
					storeId = rs.getInt("store_id");
					count++;
				}
				market.setMarket_id(storeId);
				
				rs.close();
				pstmt.close();
				
				return count >= 1;
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@HEAD
	@Path("/market/scrape")
	public Response marketScrapeHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}

	@OPTIONS
	@Path("/market/scrape")
	public Response marketScrapeOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	@POST
	@Path("/market/details")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response marketDetails(@Context HttpServletRequest request, MarketDetailsRequest req) {
		Response response = null;
		Connection con = null;
		MarketDetailsResponse res = new MarketDetailsResponse();
		
		SupermarketItem supermarketItem = res.getSupermarket();
		try {
			con = initWS();
			
			int id = req.getMarket_id();
			String googleId = req.getMaps_id();
			
			ResultSet rsMarkets = null;
			ResultSet rsProducts = null;
			
			PreparedStatement pstmt = con.prepareStatement("select * from (store inner join location on store.location_id = location.location_id ) where (store.store_id=? or google_id = ?)");
			pstmt.setInt(1, id);
			pstmt.setString(2, googleId);
			rsMarkets = pstmt.executeQuery();
			
			while(rsMarkets.next() ) {
				supermarketItem.setMarket_name(rsMarkets.getString("name"));
				supermarketItem.setMaps_id(rsMarkets.getString("google_id"));
				supermarketItem.setMarket_id(rsMarkets.getInt("store_id"));
				supermarketItem.setCity(rsMarkets.getString("city"));
				supermarketItem.setStreet(rsMarkets.getString("street"));
				supermarketItem.setZip(rsMarkets.getInt("zip"));
				supermarketItem.setLongitude(rsMarkets.getString("longitude"));
				supermarketItem.setLatitude(rsMarkets.getString("latitude"));
			}
			pstmt.close();
			rsMarkets.close();
			
			//Create ProductID-String for sql query
			pstmt = con.prepareStatement("select p.product_id, p.name, s.quantity from product p, stock s where p.product_id=s.product_id and s.store_id=? order by s.quantity desc");
			pstmt.setInt(1, supermarketItem.getMarket_id());
			rsProducts = pstmt.executeQuery();
				
			while( rsProducts.next() ) {
				tools.ProductAvailabilityItem jsonProductItem = new tools.ProductAvailabilityItem(rsProducts.getInt(1),rsProducts.getString(2),rsProducts.getInt(3));
				supermarketItem.getProducts().add(jsonProductItem);
			}
			rsProducts.close();
			pstmt.close();
			res.setResult("success");
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}
	
	@HEAD
	@Path("/market/details")
	public Response marketDetailsHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}

	@OPTIONS
	@Path("/market/details")
	public Response marketDetailsOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}


	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/market/stock 
	 * JSON input version 1
	 *  {	"market_id": 1, "product_id": [ 1, 2 ] }
	 * JSON input version 2  
	 * {	"market_id": 0,	"google_id": "MLTEST","product_id":[1,2	]}
	 * JSON output 
	 *  { "result": "success","product": [ { "id": 1, "product_name": "test", "quantity": 50 }] }
	 * 
	 * {
			"market_id": 0,
			"google_id": "MLTEST",
			"product_id":    [
      			1,
      			2
   			]
		}
	 */
	@POST
	@Path("/market/stock")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response marketStock(@Context HttpServletRequest request, MarketStockRequest req) {
		Response response = null;
		Connection con = null;
		MarketStockResponse res = new MarketStockResponse();
		try {
			con = initWS();
			String sql = "";
			PreparedStatement pstmt = null;
			if ( req.getMarket_id() > 0 ) {
				sql = "select s.product_id,p.name,s.quantity from stock s, product p " +
					 "where s.store_id=? and s.product_id in(?) and s.product_id=p.product_id " +
					 "order by p.name";
				pstmt = con.prepareStatement( sql );
				pstmt.setInt(1, req.getMarket_id());
			}
			else if ( !req.getMaps_id().isEmpty() ) { 
				sql = "select s.product_id,p.name,s.quantity from store m, stock s, product p " +
						 "where m.google_id=? and s.product_id in(?) and m.store_id=s.store_id and s.product_id=p.product_id " +
						 "order by p.name";
				pstmt = con.prepareStatement( sql );
				pstmt.setString(1, req.getMaps_id());
			}
			if ( pstmt != null ) {
				for ( Integer p :req.getProduct_id() ) {
					pstmt.setInt(2, p.intValue() );
					ResultSet rs = pstmt.executeQuery();
					while( rs.next() ) {
						res.getProduct().add( new MarketStockItem( rs.getInt(1), rs.getString(2), rs.getInt(3) ));
					}
					rs.close();
				}
				pstmt.close();
				res.setResult("success");
			}
			else {
				res.setResult("error - no input criteria provided");
			}
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}

	@HEAD
	@Path("/market/stock")
	public Response marketStockHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}

	@OPTIONS
	@Path("/market/stock")
	public Response marketStockOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/market/manage JSON input {
	 * "operation":"create", "market_id": 1, "name":"REWE", "city":"Bad Nauheim",
	 * "zip":"61231", "street":"Georg-Scheller-Strasse 2-8",
	 * "longitude":"8.754167", "latitude":"50.361944" } JSON output { "result":
	 * "success" }
	 */
	@POST
	@Path("/market/manage")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response marketManage(@Context HttpServletRequest request, MarketManageRequest req) {
		Connection con = null;
		GenericResponse res = new GenericResponse();
		Response response = null;

		try {
			con = initWS();
			switch (req.getOperation()) {
			case "create":
				res = marketManageAdd(con, req);
				break;
			case "modify":
				res = marketManageEdit(con, req);
				break;
			case "delete":
				res = marketManageDelete( con, req);
				break;
			}
		} catch (Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			res.setResult("Exception " + ex.getMessage());
		} finally {
			finallyWs(con);
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}
	
	private GenericResponse marketManageDelete(Connection con, MarketManageRequest req) throws Exception {
		GenericResponse res = new GenericResponse();
		int locationId;
		// get location id
		String sql = "Select location_id from store where store_id = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, req.getMarket_id());
		
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		locationId = rs.getInt("location_id");
		
		rs.close();
		pstmt.close();

		// delete store first
		sql = "DELETE FROM store WHERE store_id = ?";
		
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, req.getMarket_id());

		pstmt.executeUpdate();
		pstmt.close();

		sql = "DELETE FROM location WHERE location_id = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, locationId);

		pstmt.executeUpdate();
		rs.close();
		pstmt.close();

		con.commit();
		res.setResult("success");

		return res;
	}
	
	private GenericResponse marketManageEdit(Connection con, MarketManageRequest req) throws Exception {
		GenericResponse res = new GenericResponse();
		int locationId;
		// get location id
		String sql = "Select location_id from store where store_id = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, req.getMarket_id());	
		
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		locationId = rs.getInt("location_id");
		
		rs.close();
		pstmt.close();

		// update location
		sql = "UPDATE location SET zip = ?, city = ?, street = ?, longitude = ?, latitude = ? WHERE location_id = ?";
		
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, Integer.valueOf(req.getZip()));
		pstmt.setString(2, req.getCity());
		pstmt.setString(3, req.getStreet());
		pstmt.setString(4, req.getLongitude());
		pstmt.setString(5, req.getLatitude());
		pstmt.setInt(6, locationId);

		pstmt.executeUpdate();
		pstmt.close();

		sql = "UPDATE store SET name = ? WHERE store_id = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, req.getMarket_name());
		pstmt.setInt(2, req.getMarket_id());

		pstmt.executeUpdate();
		rs.close();
		pstmt.close();

		con.commit();
		res.setResult("success");

		return res;
	}

	private GenericResponse marketManageAdd(Connection con, MarketManageRequest req) throws Exception {
		GenericResponse res = new GenericResponse();
		int locationId;

		String sql = "INSERT INTO location (zip, city, street, longitude, latitude) VALUES (?, ?, ?, ?, ?) returning location_id";
		// String sql = "INSERT INTO \"public\".\"location\" (\"zip\", \"city\",
		// \"street\", \"longitude\", \"latitude\") VALUES ('61267', 'Neu-Anspach',
		// 'TestStraï¿½e', '123', '456') returning location_id;";
		// TODO zip nicht in string
		PreparedStatement pstmt = con.prepareStatement(sql);
		int zip = 0;
		if ( !req.getZip().isEmpty() ) zip = Integer.valueOf(req.getZip());
		pstmt.setInt(1, zip );
		pstmt.setString(2, req.getCity());
		pstmt.setString(3, req.getStreet());
		pstmt.setString(4, req.getLongitude());
		pstmt.setString(5, req.getLatitude());

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		String data = rs.getString(1);
		locationId = Integer.parseInt(data);
		rs.close();
		pstmt.close();

		sql = "INSERT INTO store (name, location_id, google_id) VALUES (?, " + locationId + ", ?) returning store_id";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, req.getMarket_name());
		pstmt.setString(2, req.getMaps_id());
		// pstmt.setString(2, String.valueOf(locationId));

		rs = pstmt.executeQuery();
		rs.next();
		data = rs.getString(1);
		req.setMarket_id(Integer.valueOf(data));
		rs.close();
		pstmt.close();

		con.commit();
		res.setResult("success");

		return res;
	}

	@HEAD
	@Path("/market/manage")
	public Response marketManageHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}

	@OPTIONS
	@Path("/market/manage")
	public Response marketManageOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/product JSON input
	 * 
	 * JSON output { "result": "success", "product": [ { "id": 1, "product_name":
	 * "Milch", "quantity": 50 }] }
	 */
	@POST
	@Path("/product/scrape")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response productScrape(@Context HttpServletRequest request, ProductRequest req) {
		Response response = null;
		Connection con = null;
		ProductResponse res = new ProductResponse();
		try {
			con = initWS();

			String sql = "select product_id,name from product order by name";
			PreparedStatement pstmt = con.prepareStatement( sql );
			ResultSet rs = pstmt.executeQuery();
			while( rs.next() ) {
				res.getProduct().add( new ProductItem( rs.getInt(1), rs.getString(2)) );
			}
			rs.close();
			pstmt.close();		
			con.commit();
			res.setResult("success");
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}

	@HEAD
	@Path("/product/scrape")
	public Response productScrapeHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}
	
	@OPTIONS
	@Path("/product/scrape")
	public Response productScrapeOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/product/manage JSON input {
	 * "operation":"create", "product_id": 1, "name":"Milch", "language":"DE" } JSON
	 * output { "result": "success" }
	 */
	@POST
	@Path("/product/manage")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response productManage(@Context HttpServletRequest request, ProductManageRequest req) {
		Response response = null;
		Connection con = null;
		GenericResponse res = new GenericResponse();
		try {
			con = initWS();
			PreparedStatement pstmt = null;

			if (req.getOperation().equals("create")) {
				pstmt = con.prepareStatement("insert into product (name) VALUES (?)");
				pstmt.setString(1, req.getName());
			}

			else if (req.getOperation().equals("update")) {
				pstmt = con.prepareStatement("UPDATE product SET name = ? WHERE product_id = ?");
				pstmt.setString(1, req.getName());
				pstmt.setInt(2, req.getProduct_id());
			}

			else if (req.getOperation().equals("delete")) {
				pstmt = con.prepareStatement("DELETE FROM product WHERE product_id = ?");
				pstmt.setInt(1, req.getProduct_id());
			}

			int ret = pstmt.executeUpdate();
			pstmt.close();
			con.commit();
			if (ret == 1) {
				res.setResult("success");
			} else {
				con.rollback();
				res.setResult("failed, to many affected rows");
			}

		} catch (Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			res.setResult("Exception " + ex.getMessage());
		} finally {
			finallyWs(con);
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}

	@HEAD
	@Path("/product/manage")
	public Response productManageHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}

	@OPTIONS
	@Path("/product/manage")
	public Response productManageOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/product_ean/manage JSON input {
	 * "operation":"create", "ean": "0401234567890", "product_id": 1 } JSON output {
	 * "result": "success" }
	 */
	@POST
	@Path("/product_ean/manage")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response productEanManage(@Context HttpServletRequest request, ProductEanManageRequest req) {
		Response response = null;
		Connection con = null;
		GenericResponse res = new GenericResponse();
		PreparedStatement pstmt = null;
		int ret = 0;
		try {
			con = initWS();
			if ( req.getOperation().equalsIgnoreCase("CREATE") ) {
				pstmt = con.prepareStatement("insert into ean_is_kind_of(ean,product_id) values(?,?)");
				pstmt.setLong(1, Long.valueOf(req.getEan() ));
				pstmt.setInt(2, req.getProduct_id());
				ret = pstmt.executeUpdate();
				pstmt.close();
				if ( ret == 1 ) {
					res.setResult("success");
				}
				else {
					res.setResult("error");
				}
			}
			else if ( req.getOperation().equalsIgnoreCase("UPDATE") ) {
				pstmt = con.prepareStatement("update ean_is_kind_of set product_id=? where ean=?");
				pstmt.setInt(1, req.getProduct_id());
				pstmt.setLong(2, Long.valueOf(req.getEan() ));
				ret = pstmt.executeUpdate();
				pstmt.close();
				if ( ret == 1 ) {
					res.setResult("success");
				}
				else {
					res.setResult("error");
				}
			}
			else if ( req.getOperation().equalsIgnoreCase("DELETE") ) {
				pstmt = con.prepareStatement("delete from ean_is_kind_of where ean=?");
				pstmt.setLong(1, Long.valueOf(req.getEan() ));
				ret = pstmt.executeUpdate();
				pstmt.close();
				if ( ret == 1 ) {
					res.setResult("success");
				}
				else {
					res.setResult("error");
				}
			}
			con.commit();
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}

	@HEAD
	@Path("/product_ean/manage")
	public Response productEanManageHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.build();
	      return response;
	}

	@OPTIONS
	@Path("/product_ean/manage")
	public Response productEanManageOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/product_ean/scrape JSON input {
	 * "ean":"0401234567890", "language":"DE" } JSON output { "result": "success",
	 * "product_id": 1, "name": "Milch" }
	 */
	@POST
	@Path("/product_ean/scrape")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response productEanScrape(@Context HttpServletRequest request, ProductEanScrapeRequest req) {
		Response response = null;
		Connection con = null;
		ProductEanScrapeResponse res = new ProductEanScrapeResponse();
		try {
			con = initWS();
			String sql = "select e.product_id,p.name from ean_is_kind_of e, product p " +
						 "where e.ean=? and e.product_id=p.product_id";
			PreparedStatement pstmt = con.prepareStatement( sql );
			pstmt.setLong(1, Long.valueOf(req.getEan()));
			ResultSet rs = pstmt.executeQuery();
			if( rs.next() ) {
				res.setProduct_id( rs.getInt(1));
				res.setName(rs.getString(2));
				res.setResult("success");
			}
			else {
				res.setResult("not found");
			}
			rs.close();
			pstmt.close();		
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}

	@HEAD
	@Path("product_ean/scrape")
	public Response productEanScrapeHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  							.header("Access-Control-Allow-Origin", "*")
	  							.build();
	      return response;
	}

	@OPTIONS
	@Path("/product_ean/scrape")
	public Response productEanScrapeOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	
	@POST
	@Path("/market/customers")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response marketCustomers(@Context HttpServletRequest request, MarketCustomersRequest req) {
		Response response = null;
		Connection con = null;
		GenericResponse res = new GenericResponse();
		try {
			con = initWS();
//TODO
/*			
			String sql = "select e.product_id,p.name from ean_is_kind_of e, product p " +
						 "where e.ean=? and e.product_id=p.product_id";
			PreparedStatement pstmt = con.prepareStatement( sql );
			pstmt.setLong(1, Long.valueOf(req.getEan()));
			ResultSet rs = pstmt.executeQuery();
			if( rs.next() ) {
				res.setProduct_id( rs.getInt(1));
				res.setName(rs.getString(2));
				res.setResult("success");
			}
			else {
				res.setResult("not found");
			}
			rs.close();
			pstmt.close();
*/					
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}

	@HEAD
	@Path("/market/customers")
	public Response marketCustomersHead(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  							.header("Access-Control-Allow-Origin", "*")
	  							.build();
	      return response;
	}

	@OPTIONS
	@Path("/market/customers")
	public Response marketCustomersOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	
	/**
	 * URL http://127.0.0.1:8080/Backend/ws/rest/hello JSON input {"zahl":1} JSON
	 * output {"text": "HelloWorld 1"}
	 */
	@POST
	@Path("/hello")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response hello(@Context HttpServletRequest request, HelloRequest req) {
		Response response = null;
		Connection con = null;
		HelloResponse res = new HelloResponse();
		try {
			con = initWS();
//TODO just for testing			
			findMarkets( con, 50.363509, 8.751548, 500 );

			res.setText( "HelloWorld " + req.getZahl());
		}
		catch ( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			res.setResult( "Exception " + ex.getMessage() );
		}
		finally {
			finallyWs( con );
			response = Response.status(200).entity(res).header("Access-Control-Allow-Origin", "*").build();
		}
		return response;
	}

	@HEAD
	@Path("/hello")
	public Response helloHead(@QueryParam("param1") String param1) {
		Response response = Response.ok("this body will be ignored")
  								.header("Access-Control-Allow-Origin", "*")
  								.build();
		return response;
	}

	@OPTIONS
	@Path("/hello")
	public Response helloOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Allow-Method", "POST")
	  				.header("Access-Control-Allow-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	

	/**
	 * Get all markets inside a square around the GPS location
	 * https://de.wikipedia.org/wiki/Wegpunkt-Projektion
	 * @param lat	in °
	 * @param lng	in °
	 * @param radius in meter
	 * For better DB performance
	 * CREATE INDEX IDX_LONGITUDE ON location(longitude)
	 * CREATE INDEX IDX_LATITUDE ON location(latitude)
	 */
	private List<SupermarketItem> findMarkets( Connection con, double lat, double lng, long radius) {
		List<SupermarketItem> list = new ArrayList<SupermarketItem>();
		GpsPoint center = new GpsPoint(lat,lng);
		GpsPoint corners[] = new GpsPoint[2];
		corners[0] = bearing( center, radius, 315);			//Upper left
		corners[1] = bearing( center, radius, 135);			//Lower right
		
		String sql = "select l.zip, l.city, l.street, l.longitude, l.latitude, " +
					 "s.store_id,s.name,s.google_id " +
					 "from store s, location l " +
					 "where l.location_id=s.location_id and " +
					 "? <= latitude and latitude <= ? and " +
					 "? <= longitude and longitude <= ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement( sql );
			pstmt.setString(1,(String.format(Locale.US, "%f", corners[1].lat)));
			pstmt.setString(2,(String.format(Locale.US, "%f", corners[0].lat)));
			pstmt.setString(3,(String.format(Locale.US, "%f", corners[0].lng)));
			pstmt.setString(4,(String.format(Locale.US, "%f", corners[1].lng)));

			ResultSet rs = pstmt.executeQuery();
			while ( rs.next() ) {
				System.out.println(rs.getString(4));
				System.out.println(rs.getString(5));

				SupermarketItem item = new SupermarketItem();
	//TODO			item.setZip(rs.getInt(1));
				item.setCity(rs.getString(2));
				item.setStreet(rs.getString(3));
				item.setLongitude(rs.getString(4));
				item.setLatitude(rs.getString(5));
				item.setMarket_id( rs.getInt(6));
				item.setMarket_name(rs.getString(7));
				item.setMaps_id(rs.getString(8));
				int distance = distance(lat, lng, Double.valueOf(item.getLatitude()), Double.valueOf(item.getLongitude()) );
				item.setDistance(String.format("%d", distance));
				list.add(item);
			}
			rs.close();
			pstmt.close();
		}
		catch( Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
		return list;
	}
	
	private class GpsPoint {
		public double lat = 0.0;
		public double lng = 0.0;
		GpsPoint() {
		}
		GpsPoint( double lat, double lng ) {
			this.lat = lat;
			this.lng = lng;
		}
	}

	/**
	 * Calculate the GPS coordinate from point p in direction of angle and distance
	 * @param p
	 * @param distance in meter
	 * @param angle in ° (0=north, 90=east, ...)
	 * @return
	 */
	private GpsPoint bearing( GpsPoint p, long distance, int angle) {
		GpsPoint r = new GpsPoint();
		try {
			double dlat = ((double)distance)/1853.0*Math.cos(angle*Math.PI/180);
			double dlng =dlat*Math.tan(((double)angle)*Math.PI/180)/Math.cos(p.lng*Math.PI/180+dlat);
			r.lat = p.lat+dlat*Math.PI/180;
			r.lng = p.lng+dlng*Math.PI/180;
		}
		catch( Exception ex ) {
			
		}
		return r;
	}
	
	/**
	 * Get the distance between 2 GPS coordinates 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return	distance in meter
	 */
	private int distance( double lat1, double lng1, double lat2, double lng2 ) {
		double dist = 6378388.0 * Math.acos(Math.sin(lat1*Math.PI/180) * Math.sin(lat2*Math.PI/180) + Math.cos(lat1*Math.PI/180) * Math.cos(lat2*Math.PI/180) * Math.cos(lng2*Math.PI/180 - lng1*Math.PI/180));
		return (int) dist;
	}
}
