package webService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

public class RestBasis extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private final static String DBRESOURCE = "jdbc/wvv2";
	private static Context initCtx = null;
	private static Context envCtx = null;
	private static DataSource dataSource = null;
	
//	private static BasicDataSource dataSource = null;

	protected Connection con = null;

	protected Connection initWS() throws Exception {
		Connection con = null;
		boolean ret = true;
		if (dataSource == null) {
			
			String d = "com.mysql.cj.jdbc.Driver";
			String u = "jdbc:mysql://s217.goserver.host:3306/web157_db2";
			Properties dbcpProperties = new Properties();
			dbcpProperties.put("driverClassName", d);
			dbcpProperties.put("url", u);
			dbcpProperties.put("username", "web157_2" );
			dbcpProperties.put("password", "YocyTB8JkOLZnSMw" );
			dbcpProperties.put("defaultAutoCommit", "FALSE");
			dbcpProperties.put("maxActive", "1");
			dbcpProperties.put("ssl", "true");
			dbcpProperties.put("validationQuery", "SELECT 1");
			dbcpProperties.put("testOnBorrow", "true");

			dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(dbcpProperties);
			/*
			if (envCtx == null) {
				if (initCtx == null) {
					initCtx = new InitialContext();
				}
				envCtx = (Context) initCtx.lookup("java:comp/env");
			}
			dataSource = (DataSource) envCtx.lookup(DBRESOURCE);
*/
			if (dataSource == null) {
				ret = false;
			}
		}

		if (ret) {
			con = getConnection(); // Get always a DB connection
		}
		return con;
	}

	protected Connection getConnection() throws SQLException {
		Connection con = dataSource.getConnection();
		if (con != null) {
			con.setAutoCommit(false);
		}
		return con;
	}
	
	protected void finallyWs(Connection con) {
		try {
			if (con != null) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
		}
	}	

	
}
