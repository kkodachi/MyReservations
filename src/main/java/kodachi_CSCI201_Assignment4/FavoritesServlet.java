package kodachi_CSCI201_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.tomcat.util.http.parser.Cookie;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet("/FavoritesServlet")
public class FavoritesServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// adding new restaurant to users favorites
		PrintWriter pw = null;
		JDBCConnector jdbc = new JDBCConnector();
		BufferedReader br = null;
		try {
			// "parse a json POST request" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuilder jsonBody = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null) {
	            jsonBody.append(line);
	        }
	        
	        String userID = "";
	        String username = "";
	        // "get cookie in Java" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
	        javax.servlet.http.Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	        	for (javax.servlet.http.Cookie cookie: cookies) {
	        		if (cookie.getName().equals("userID")) {
	                    userID = cookie.getValue();
	                }
	        		if (cookie.getName().equals("username")) {
	                    username = cookie.getValue();
	                }
	        	}
	        }
	        
	        JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonBody.toString());
            JSONObject requestData = (JSONObject) obj;
            String rName = (String) requestData.get("rName");
            String addr = (String) requestData.get("addr");
            String link = (String) requestData.get("link");
            String r = String.valueOf(requestData.get("rating"));
            String ph = (String) requestData.get("phone");
            String pr = (String) requestData.get("price");
            String c = (String) requestData.get("cuisine");
            String i = (String) requestData.get("image");
            
            String action = (String) requestData.get("action");
            Restaurant rest = new Restaurant(rName,addr,link,Double.valueOf(r),ph,pr,c,i);
            System.out.println("action: " + action);
            System.out.println("servlet rName: "+rName);
            System.out.println("servlet link: "+link);
            
            response.setContentType("application/json");
			pw = response.getWriter();
			JSONObject o = null;
			if (action.equals("add")) {
				o = jdbc.addFavorite(userID,rest);
			} else if (action.equals("del")) {
				o = jdbc.delFavorite(userID,rest);
			} else if (action.equals("check")) {
				o = jdbc.checkFavorite(userID,rest);
			}
			if (o != null) {
				String query = o.toString();
				pw.println(query);
				pw.flush();
			} else {
				pw.println("JSONObject was null");
				System.out.println("JSONObject was null");
				pw.flush();
			}
			
		}  catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (br != null) {
				br.close();
			}
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter pw = null;
		try {
	        String userID = "";
	        String username = "";
	        // "get cookie in Java" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
	        javax.servlet.http.Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	        	for (javax.servlet.http.Cookie cookie: cookies) {
	        		if (cookie.getName().equals("userID")) {
	                    userID = cookie.getValue();
	                }
	        		if (cookie.getName().equals("username")) {
	                    username = cookie.getValue();
	                }
	        	}
	        }
	        System.out.println("userID: " + userID);
	        System.out.println("username: " + username);
            response.setContentType("application/json");
			pw = response.getWriter();
			String action = request.getParameter("action");
			
			JDBCConnector jdbc = new JDBCConnector();
			String json = jdbc.getFavorites(userID, action);
			
			pw.write(json);
			pw.flush();
		}  catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		
	}
}
