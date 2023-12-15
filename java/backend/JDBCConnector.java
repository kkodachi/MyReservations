package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JDBCConnector {
	
	public JDBCConnector() {
	}
	
	@SuppressWarnings("resource")
	protected JSONObject addUser(String u, String p, String e) throws ClassNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		JSONObject o = new JSONObject();
		o.put("userID", "");
		o.put("username", "");
		o.put("pWord", "");
		o.put("email", "");
		o.put("eStatus", "valid");
		o.put("uStatus", "valid");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
//			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?user=root&password="+GlobalVariables.MySQLPassword);
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			ps = conn.prepareStatement("SELECT * FROM Users WHERE email = ?;");
			ps.setString(1, e);
			rs1 = ps.executeQuery();
			boolean unused = true;
			if (rs1.next()) {
				System.out.println("Email in already use");
				o.remove("eStatus");
				o.put("eStatus", "invalid");
				unused = false;
			}
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username = ?;");
			ps.setString(1, u);
			rs2 = ps.executeQuery();
			if (rs2.next()) {
				System.out.println("Username in already use");
				o.remove("uStatus");
				o.put("uStatus", "invalid");
				unused = false;
			}
			if (!unused) {
				return o;
			} else {
//				ps = conn.prepareStatement("INSERT INTO Users(username,pWord,email)"
//						+ " VALUES('"+u+"','"+p+"','"+e+"');");
				ps = conn.prepareStatement("INSERT INTO Users(username,pWord,email) VALUES(?,?,?);");
				ps.setString(1, u);
				ps.setString(2, p);
				ps.setString(3, e);
//				int result = ps.executeUpdate();
				if (ps.executeUpdate() > 0) {
//					ps = conn.prepareStatement("SELECT * FROM Users WHERE email = '"+e+"';");
					ps = conn.prepareStatement("SELECT * FROM Users WHERE email = ?;");
					ps.setString(1,e);
					rs3 = ps.executeQuery();
					if (rs3.next()) {
						o.put("userID", rs3.getString("userID"));
						o.put("username", rs3.getString("username"));
						o.put("pWord", rs3.getString("pWord"));
						o.put("email", rs3.getString("email"));
					}
				} else {
					System.out.println("non 1 rows changes");
				}
				return o;
			}
			
		} catch (SQLException sqle) {
			System.out.println("SQLException in addUser():");
			System.out.println(sqle.getMessage());
			sqle.printStackTrace();
		} catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException in insertUser(), MySQL Driver not found");
		}
		finally {
			try {
				if (rs3 != null) {
					rs3.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
			
		}
		return o;
	}
	
	protected JSONObject checkUser(String u, String p) throws ClassNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		JSONObject o = new JSONObject();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
//			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?user=root&password="+GlobalVariables.MySQLPassword);
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username = ? AND pWord = ?;");
			ps.setString(1,u);
			ps.setString(2,p);
			rs1 = ps.executeQuery();
			if (rs1.next()) {
				o.put("userID", rs1.getString("userID"));
				o.put("username", rs1.getString("username"));
				o.put("pWord", rs1.getString("pWord"));
				o.put("email", rs1.getString("email"));
				o.put("status", "valid");
			} else {
				o.put("userID", "");
				o.put("username", "");
				o.put("pWord", "");
				o.put("email", "");
				o.put("status", "invalid");
			}
			return o;
		} catch (SQLException sqle) {
			System.out.println("SQLException in checkUser():");
			System.out.println(sqle.getMessage());
			sqle.printStackTrace();
		} catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException in checkUser(), MySQL Driver not found");
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return o;
	}
	
	private String getRestaurant(Restaurant r) {
		String restID = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet key = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			ps = conn.prepareStatement("SELECT restID FROM Restaurants WHERE rName = ? AND addr = ?;");
			ps.setString(1,r.getN());
			ps.setString(2, r.getA());
			rs1 = ps.executeQuery();
			if (rs1.next()) {
				restID = rs1.getString("restID");
			} else {
				ps = conn.prepareStatement("INSERT INTO Restaurants(rName,addr,phone,cuisine,price,rating,link,image) "
						+ "VALUES(?,?,?,?,?,?,?,?);",java.sql.Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, r.getN());
				ps.setString(2, r.getA());
				ps.setString(3, r.getPh());
				ps.setString(4, r.getC());
				ps.setString(5, r.getPr());
				ps.setString(6, Double.toString(r.getR()));
				ps.setString(7, r.getL());
				System.out.println("getRest() link: "+r.getL());
				ps.setString(8, r.getI());
				if (ps.executeUpdate() > 0) {
					key = ps.getGeneratedKeys();
					if (key.next()) {
						restID = String.valueOf(key.getLong(1));
					}
				}
				System.out.println("restID: " + restID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (key != null) {
					key.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return restID;
	}
	
	protected JSONObject addFavorite(String userID, Restaurant r) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		JSONObject o = new JSONObject();
		o.put("favstatus", "unsuccessful");
		String restID = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			restID = getRestaurant(r);
			ps = conn.prepareStatement("SELECT * FROM Favorites WHERE userID = ? AND restID = ?;");
			ps.setString(1,userID);
			ps.setString(2,restID);
			rs1 = ps.executeQuery();
			if (rs1.next()) {
				o.remove("favstatus");
				o.put("favstatus", "exists");
			} else {
				ps = conn.prepareStatement("INSERT INTO Favorites(userID,restID) VALUES(?,?);");
				ps.setString(1,userID);
				ps.setString(2,restID);
				if (ps.executeUpdate() > 0) {
					o.remove("favstatus");
					o.put("favstatus", "added");
				}
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException in checkUser():");
			System.out.println(sqle.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return o;
	}
	
	protected JSONObject delFavorite(String userID, Restaurant r) {
		Connection conn = null;
		PreparedStatement ps = null;
		JSONObject o = new JSONObject();
		o.put("fav", "before");
		String restID = "";
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			restID = getRestaurant(r);
			ps = conn.prepareStatement("DELETE FROM Favorites WHERE userID = ? AND restID = ?");
			ps.setString(1,userID);
			ps.setString(2,restID);
			if (ps.executeUpdate() == 0) {
				o.remove("fav");
				o.put("fav", "empty");
			} else {
				o.remove("fav");
				o.put("fav", "removed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return o;
	}
	
	protected JSONObject addReservation(String userID, Restaurant r, String d, String t) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		JSONObject o = new JSONObject();
		o.put("resstatus", "unsuccessful");
		String restID = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			restID = getRestaurant(r);
			Timestamp dt = Timestamp.valueOf(d + " " + t + ":00");
				ps = conn.prepareStatement("INSERT INTO Reservations(userID,restID,dt) VALUES(?,?,?);");
				ps.setString(1,userID);
				ps.setString(2,restID);
				ps.setTimestamp(3,dt);
				if (ps.executeUpdate() > 0) {
					o.remove("resstatus");
					o.put("resstatus", "added");
				}
//			}
		} catch (SQLException sqle) {
			System.out.println("SQLException in checkUser():");
			System.out.println(sqle.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return o;
	}
	
	
	protected String getFavorites(String userID, String action) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		JSONArray json = new JSONArray();
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			
			String query = "SELECT rest.*, f.favID FROM Restaurants rest JOIN Favorites f ON "
					+ "rest.restID = f.restID WHERE f.userID = ?;";
			ps = conn.prepareStatement(query);
			ps.setString(1,userID);
			rs1 = ps.executeQuery();
			ArrayList<Restaurant> r = new ArrayList<>();
			while (rs1.next()) {
				Restaurant rest = new Restaurant();
				rest.setN(rs1.getString("rName"));
				rest.setA(rs1.getString("addr"));
				rest.setPh(rs1.getString("phone"));
				rest.setC(rs1.getString("cuisine"));
				rest.setPr(rs1.getString("price"));
				rest.setR(Double.valueOf(rs1.getString("rating")));
				rest.setL(rs1.getString("link"));
				rest.setI(rs1.getString("image"));
				rest.setInd(rs1.getInt("favID"));
				r.add(rest);
			}
			// "Comparator Interface in Java with Examples" (referenced, 0 lines copied) GeeksForGeeks, 26 Nov,https://www.geeksforgeeks.org/comparator-interface-java/
			if (action.equals("AZ")) {
				Collections.sort(r,Comparator.comparing(Restaurant::getN));
			} else if (action.equals("ZA")) {
				Collections.sort(r,Comparator.comparing(Restaurant::getN).reversed());
			} else if (action.equals("hrating")) {
				Collections.sort(r,Comparator.comparing(Restaurant::getR).reversed());
			} else if (action.equals("lrating")) {
				Collections.sort(r,Comparator.comparing(Restaurant::getR));
			} else if (action.equals("mrecent")) {
				Collections.sort(r,Comparator.comparing(Restaurant::getInd).reversed());
			} else if (action.equals("lrecent")) {
				Collections.sort(r,Comparator.comparing(Restaurant::getInd));
			}
			for (Restaurant rest: r) {
				JSONObject jo = new JSONObject();
				jo.put("rName", rest.getN());
				jo.put("addr", rest.getA());
				jo.put("phone", rest.getPh());
				jo.put("cuisine", rest.getC());
				jo.put("price", rest.getPr());
				jo.put("rating", Double.toString(rest.getR()));
				jo.put("link", rest.getL());
				jo.put("image", rest.getI());
				json.add(jo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		JSONObject temp = new JSONObject();
		temp.put("restaurants", json);
		return temp.toJSONString();
	}
	
	protected String getReservations(String userID, String action) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		JSONArray json = new JSONArray();
		String query = "";
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			query = "SELECT rest.*, res.dt FROM Restaurants rest JOIN Reservations res ON "
					+ "rest.restID = res.restID WHERE res.userID = ?;";
			ps = conn.prepareStatement(query);
			ps.setString(1,userID);
			rs1 = ps.executeQuery();
			ArrayList<Restaurant> r = new ArrayList<>();
			while (rs1.next()) {
				Restaurant rest = new Restaurant();
				rest.setN(rs1.getString("rName"));
				rest.setA(rs1.getString("addr"));
				rest.setPh(rs1.getString("phone"));
				rest.setC(rs1.getString("cuisine"));
				rest.setPr(rs1.getString("price"));
				rest.setR(Double.valueOf(rs1.getString("rating")));
				rest.setL(rs1.getString("link"));
				rest.setI(rs1.getString("image"));
				rest.setDT(rs1.getTimestamp("dt"));
				r.add(rest);
			}
			// "Comparator Interface in Java with Examples" (referenced, 0 lines copied) GeeksForGeeks, 26 Nov,https://www.geeksforgeeks.org/comparator-interface-java
			if (action.equals("mrecent")) { // furthest to soonest
				r.sort((r1, r2) -> r1.getDT().compareTo(r2.getDT()));
				Collections.reverse(r);
			} else if (action.equals("lrecent")) { // soonest to furthest
				r.sort((r1, r2) -> r1.getDT().compareTo(r2.getDT()));
			}
			for (Restaurant rest: r) {
				JSONObject jo = new JSONObject();
				jo.put("rName", rest.getN());
				jo.put("addr", rest.getA());
				jo.put("phone", rest.getPh());
				jo.put("cuisine", rest.getC());
				jo.put("price", rest.getPr());
				jo.put("rating", Double.toString(rest.getR()));
				jo.put("link", rest.getL());
				jo.put("image", rest.getI());
				String[] temp = rest.getDT().toString().split(" ");
				String d = temp[0];
				String t = temp[1].substring(0, 5);
				System.out.println("getrest() d: "+d);
				System.out.println("getrest() t: "+t);
				jo.put("date", d);
				jo.put("time", t);
				json.add(jo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		JSONObject temp = new JSONObject();
		temp.put("restaurants", json);
		return temp.toJSONString();
	}
	
	protected JSONObject checkFavorite(String userID, Restaurant r) {
		Connection conn = null;
		PreparedStatement ps = null;
		JSONObject o = new JSONObject();
		ResultSet rs1 = null;
		o.put("fav", "before");
		String restID = "";
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			restID = getRestaurant(r);
			ps = conn.prepareStatement("SELECT * FROM Favorites WHERE userID = ? AND restID = ?");
			ps.setString(1,userID);
			ps.setString(2,restID);
			rs1 = ps.executeQuery();
			if (rs1.next()) {
				o.remove("fav");
				o.put("fav", "exists");
			} else {
				o.remove("fav");
				o.put("fav", "DNE");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return o;
	}
	// unused
	/*
	protected JSONObject delReservation(String userID, Restaurant r, String d, String t) {
		Connection conn = null;
		PreparedStatement ps = null;
		JSONObject o = new JSONObject();
		o.put("res", "before");
		String restID = "";
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JoesTables?" , "root" , GlobalVariables.MySQLPassword);
			restID = getRestaurant(r);
			ps = conn.prepareStatement("DELETE FROM Reservations WHERE userID = ? "
					+ "AND restID = ? AND res_date = ? AND res_time = ?");
			ps.setString(1,userID);
			ps.setString(2,restID);
			ps.setString(3,d);
			ps.setString(4,t);
			if (ps.executeUpdate() == 0) {
				o.remove("res");
				o.put("res", "empty");
			} else {
				o.remove("res");
				o.put("res", "removed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return o;
	}
	*/
}
