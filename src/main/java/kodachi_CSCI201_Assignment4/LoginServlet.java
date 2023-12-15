package kodachi_CSCI201_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
	        
	        JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonBody.toString());
            JSONObject requestData = (JSONObject) obj;
            String username = (String) requestData.get("username");
            String password = (String) requestData.get("password");
			response.setContentType("application/json");
			pw = response.getWriter();
			
			JSONObject o = jdbc.checkUser(username,password);
			String query = o.toString();
			
			// "attach a cookie to a response in a Servlet" prompt (6 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
			Cookie userID = new Cookie("userID", (String) o.get("userID"));
			userID.setMaxAge(30 * 24 * 60 * 60);
			response.addCookie(userID);
			Cookie uname = new Cookie("username", (String) o.get("username"));
			uname.setMaxAge(30 * 24 * 60 * 60);
			response.addCookie(uname);
			
			pw.println(query);
			pw.flush();
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		finally {
			if (pw != null) {
				pw.close();
			}
			if (br != null) {
				br.close();
			}
		}
		
	}

}
