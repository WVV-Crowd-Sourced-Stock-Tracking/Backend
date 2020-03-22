package webService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import tools.GenericResponse;
import tools.Location;
import tools.MarketStockItem;
import tools.ProductItem;
import tools.Supermarket;
import tools.json_items.SupermarketItem;

@Path("/rest")
public class Rest extends RestBasis {
	private static final long serialVersionUID = 2L;

	/**
	 * 	URL http://127.0.0.1:8080//Backend/ws/rest/market/transmit
	 *  JSON input 
	 	{ 
			"market_id": 1,
			"product_id": 1,
			"quantity": 100
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
		int ret = 0;
		try {
			con = initWS();

			PreparedStatement pstmt = null;
			pstmt = con.prepareStatement("update stock set quantity=? where store_id=? and product_id=?");
			pstmt.setInt(1, req.getQuantity());
			pstmt.setInt(2, req.getMarket_id());
			pstmt.setInt(3, req.getProduct_id());
			ret = pstmt.executeUpdate();
			pstmt.close();

			if (ret == 0) {
				pstmt = con.prepareStatement("insert into stock(store_id,product_id,quantity) values(?,?,?)");
				pstmt.setInt(1, req.getMarket_id());
				pstmt.setInt(2, req.getProduct_id());
				pstmt.setInt(3, req.getQuantity());
				ret = pstmt.executeUpdate();
				pstmt.close();
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
	  				.header("Access-Control-Request-Method", "POST")
	  				.header("Access-Control-Request-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/market/scrape JSON input {
	 * "zip":"61231", "radius": 12000, "product_id": [ 1, 2 ] } JSON output {
	 * "result": "success", "supermarket": [ { "id": 0, "name": "REWA Center Bad
	 * Nauheim", "city": "Bad Nauheim", "street": "Georg-Scheller-StraÃŸe 2-8",
	 * "gps_length": "8.754167", "gps_width": "50.361944" }] }
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
			String gps_length = req.getGps_length();
			String gps_width = req.getGps_width();
			
			ResultSet rsMarkets = null;
			ResultSet rsProducts = null;
			List<SupermarketItem> marketList = new ArrayList<SupermarketItem>();
			
			if (zipString != null && (gps_length == null || gps_width == null)) {
				//TODO SQL Alle mit gleicher Zip
				int zip = Integer.parseInt(zipString);
				
				PreparedStatement pstmt = null;
				pstmt = con.prepareStatement("select * from (store inner join location on store.location_id = location.location_id ) where location.zip=?");
				pstmt.setInt(1, zip);
				rsMarkets = pstmt.executeQuery();
				pstmt.close();
				
			}
			else if (gps_length != null && gps_width != null) {
				int radius = req.getRadius();
				//TODO Get Supermarket IDs via API			
				
				String get_url = "http://3.120.206.89/markets?latitude=" + gps_width + "&longitude=" + gps_length + "&radius=" + radius;
				String USER_AGENT = "Mozilla/5.0";
				URL obj = new URL(get_url);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", USER_AGENT);
				int responseCode = conn.getResponseCode();
				System.out.println("GET Response Code :: " + responseCode);
				if (responseCode == HttpURLConnection.HTTP_OK) { // success
					InputStream in = conn.getInputStream();
					
				    StringBuilder textBuilder = new StringBuilder();
				    try (Reader reader = new BufferedReader(new InputStreamReader
				      (in, Charset.forName(StandardCharsets.UTF_8.name())))) {
				        int c = 0;
				        while ((c = reader.read()) != -1) {
				            textBuilder.append((char) c);
				        }
				    }
				    
					ObjectMapper mapper = new ObjectMapper();
				    JsonNode actualObj = mapper.readTree(textBuilder.toString());
				    if (actualObj.isArray()) {
				        for (final JsonNode objNode : actualObj) {
				            System.out.println(objNode);
				            JsonNode n = objNode.path("name");
				            String a = n.asText();
				            System.out.println(a);
				        }
				    }
	    
					for(final JsonNode objNode : actualObj) {
						Supermarket market = new Supermarket();
						market.setName(objNode.path("name").asText());
						market.setGoogle_id(objNode.path("id").asText());
						market.setDistance(objNode.path("distance").asText());
						if(marketIsInDb( con, market)) {
							// load market from db
							Location location = new Location();
							location.setStreet(objNode.path("vicinity").asText());
							location.setGpsLength(objNode.path("longitude").asText());
							location.setGpsWidth(objNode.path("latitude").asText());
							
							market.setLocation(location);
							
						} else {
							// add market to db
							MarketManageRequest mmr = new MarketManageRequest();
							Location location = new Location();
							location.setStreet(objNode.path("vicinity").asText());
							location.setGpsLength(objNode.path("longitude").asText());
							location.setGpsWidth(objNode.path("latitude").asText());
							
							market.setLocation(location);
							
							mmr.setOperation("create");
							mmr.setName(market.getName());
							mmr.setGps_length(location.getGpsLength());
							mmr.setGps_width(location.getGpsWidth());
							mmr.setGoogle_id(market.getGoogle_id());
							mmr.setStreet(objNode.path("vicinity").asText());
							marketManageAdd( con, mmr );
							market.setMarket_id( mmr.getMarket_id() );
						}
						
						marketList.add(new SupermarketItem(market));						
					}
				}
				
				res.setSupermarket(marketList);
			}
			
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
			List<List<tools.json_items.ProductItem>> productsInMarkets = new ArrayList<List<tools.json_items.ProductItem>>();
			Iterator<SupermarketItem> marketIterator = marketList.iterator();
			List<tools.json_items.ProductItem> singleMarketProducts = null;
			
			//TODO - Fuelle singleMarketProducts-Liste mit den angefragten Produkten
			
			while (marketIterator.hasNext()) {
				currMarket = marketIterator.next();
				singleMarketProducts = new ArrayList<tools.json_items.ProductItem>();
				//Create ProductID-String for sql query
				PreparedStatement pstmt = null;
				pstmt = con.prepareStatement("select p.product_id, p.name, s.quantity from product p, stock s where p.product_id=s.product_id and s.store_id=? " + sqlFilter + " order by s.quantity desc");
				pstmt.setInt(1, currMarket.getMarket_id());
				rsProducts = pstmt.executeQuery();
				
				//TODO einzele Auslese in singleMarketProduts-Liste packen und anschließend diese in productsInMarket-Liste
				while( rsProducts.next() ) {
					tools.ProductCategory productCategory = new tools.ProductCategory();
					productCategory.setId(rsProducts.getInt(1));
					productCategory.setName(rsProducts.getString(2));
					
					tools.Product singleProduct = new tools.Product(productCategory);
					singleProduct.setQuantity(rsProducts.getInt(3));
					
					tools.json_items.ProductItem jsonProductItem = new tools.json_items.ProductItem(singleProduct);
					
					singleMarketProducts.add(jsonProductItem);
				}
				rsProducts.close();
				pstmt.close();
				currMarket.setProduct(singleMarketProducts);
				productsInMarkets.add(singleMarketProducts);		
			} 
			
			res.setProductItems(productsInMarkets);
			
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
	
	private boolean marketIsInDb(Connection con, Supermarket market) {
		try {
			String sql = "";
			PreparedStatement pstmt = null;
				sql = "select store.store_id from store "
					+ "where store.google_id = ?";
				pstmt = con.prepareStatement( sql );
				pstmt.setString(1, market.getGoogle_id());
				
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
				
				return count == 1;
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
			else if ( !req.getGoogle_id().isEmpty() ) { 
				sql = "select s.product_id,p.name,s.quantity from store m, stock s, product p " +
						 "where m.google_id=? and s.product_id in(?) and m.store_id=s.store_id and s.product_id=p.product_id " +
						 "order by p.name";
				pstmt = con.prepareStatement( sql );
				pstmt.setString(1, req.getGoogle_id());
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
	  				.header("Access-Control-Request-Method", "POST")
	  				.header("Access-Control-Request-Headers", "Content-Type,content-type")
	  				.header("Access-Control-Max-Age", "86400")
	  				.build();
	      return response;
	}
	
	/**
	 * URL http://127.0.0.1:8080//Backend/ws/rest/market/manage JSON input {
	 * "operation":"create", "market_id": 1, "name":"REWE", "city":"Bad Nauheim",
	 * "zip":"61231", "street":"Georg-Scheller-Strasse 2-8",
	 * "gps_length":"8.754167", "gps_width":"50.361944" } JSON output { "result":
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
		sql = "UPDATE location SET zip = ?, city = ?, street = ?, gps_length = ?, gps_width = ? WHERE location_id = ?";
		
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, Integer.valueOf(req.getZip()));
		pstmt.setString(2, req.getCity());
		pstmt.setString(3, req.getStreet());
		pstmt.setString(4, req.getGps_length());
		pstmt.setString(5, req.getGps_width());
		pstmt.setInt(6, locationId);

		pstmt.executeUpdate();
		pstmt.close();

		sql = "UPDATE store SET name = ? WHERE store_id = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, req.getName());
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

		String sql = "INSERT INTO location (zip, city, street, gps_length, gps_width) VALUES (?, ?, ?, ?, ?) returning location_id";
		// String sql = "INSERT INTO \"public\".\"location\" (\"zip\", \"city\",
		// \"street\", \"gps_length\", \"gps_width\") VALUES ('61267', 'Neu-Anspach',
		// 'TestStraï¿½e', '123', '456') returning location_id;";
		// TODO zip nicht in string
		PreparedStatement pstmt = con.prepareStatement(sql);
		int zip = 0;
		if ( !req.getZip().isEmpty() ) zip = Integer.valueOf(req.getZip());
		pstmt.setInt(1, zip );
		pstmt.setString(2, req.getCity());
		pstmt.setString(3, req.getStreet());
		pstmt.setString(4, req.getGps_length());
		pstmt.setString(5, req.getGps_width());

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		String data = rs.getString(1);
		locationId = Integer.parseInt(data);
		rs.close();
		pstmt.close();

		sql = "INSERT INTO public.store (name, location_id, google_id) VALUES (?, " + locationId + ", ?) returning store_id";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, req.getName());
		pstmt.setString(2, req.getGoogle_id());
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
	  				.header("Access-Control-Request-Method", "POST")
	  				.header("Access-Control-Request-Headers", "Content-Type,content-type")
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
	  				.header("Access-Control-Request-Method", "POST")
	  				.header("Access-Control-Request-Headers", "Content-Type,content-type")
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
	  				.header("Access-Control-Request-Method", "POST")
	  				.header("Access-Control-Request-Headers", "Content-Type,content-type")
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
	  				.header("Access-Control-Request-Method", "POST")
	  				.header("Access-Control-Request-Headers", "Content-Type,content-type")
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
	                           .header("someHeader", "someHeaderValue")
	                           .build();
	      return response;
	}

	@OPTIONS
	@Path("/product_ean/scrape")
	public Response productEanScrapeOptions(@QueryParam("param1") String param1) {
	      Response response = Response.ok("this body will be ignored")
	  				.header("Access-Control-Allow-Origin", "*")
	  				.header("Access-Control-Request-Method", "POST")
	  				.header("Access-Control-Request-Headers", "Content-Type,content-type")
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
			String sql = "select location_id from location";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String data = rs.getString(1);
				System.out.println(data);
			}
			rs.close();
			pstmt.close();		
			
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
		Response response = Response.ok("this body will be ignored").header("someHeader", "someHeaderValue").build();
		return response;
	}



	private void finallyWs(Connection con) {
		try {
			if (con != null) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
		}
	}	
}
