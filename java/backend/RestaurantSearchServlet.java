package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet("/RestaurantSearchServlet")
public class RestaurantSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStreamReader is1 = null;
		BufferedReader br1 = null;
		BufferedReader br2 = null;
        try {
        	// "parse a json POST request" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
        	br1 = new BufferedReader(new InputStreamReader(request.getInputStream()));
    		StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = br1.readLine()) != null) {
                jsonBody.append(line);
            }
            
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonBody.toString());
            JSONObject requestData = (JSONObject) obj;
            String url = (String) requestData.get("url");
            String key = (String) requestData.get("key");
            System.out.println("url in searchservlet: "+url);
            
            // "connection me to the yelp api to make a call" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
			// "Getting Started with the Yelp Fusion API" (5 lines) Yelp Fusion API, 26 Nov. 2023, https://docs.developer.yelp.com/docs/fusion-intro
            // used combination of above prompt and documentation to connect to Yelp API
            URL URL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
			String auth = "Bearer " + key;
            conn.setRequestProperty("Authorization", auth);
			conn.setRequestMethod("GET");
			
			is1 = new InputStreamReader(conn.getInputStream());
			br2 = new BufferedReader(is1);
			String resp = br2.readLine();
			JSONParser respParser= new JSONParser();
			Object apiCall = respParser.parse(resp);
			JSONObject jo = (JSONObject) apiCall;
            JSONArray restaurants = (JSONArray) jo.get("businesses");
            
			JSONObject rest = new JSONObject();
	        rest.put("restaurants", restaurants);
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        PrintWriter pw = response.getWriter();
			pw.print(rest.toJSONString());
			pw.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (is1 != null) {
				is1.close();
			}
			if (br1 != null) {
				br1.close();
			}
			if (br2 != null) {
				br2.close();
			}
		}
	}
}
