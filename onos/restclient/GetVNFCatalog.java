import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class GetVNFCatalog {

	// http://localhost:8080/RESTfulExample/json/product/post
	public static void main(String[] args) {

	  try {

		//URL url = new URL("http://172.16.197.132:8888/v1.0/vnfds.json");
		URL url = new URL("http://10.76.190.80:8888/v1.0/vnfds.json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("User-Agent", "python-tackerclient");
		conn.setRequestProperty("X-Auth-Token", 
                       args[0]);

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
                StringBuffer buf = new StringBuffer();
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			buf.append(output);
		}
                JSONParser parser = new JSONParser();
                JSONObject vfnd = (JSONObject)parser.parse(buf.toString());
                JSONArray vfnds = (JSONArray)vfnd.get("vnfds");
                System.out.println(buf.toString());
                for (int i=0; i< vfnds.size();i++)
                {
                   JSONObject vfnd1 = (JSONObject)vfnds.get(i);
                   String vnfid = (String) vfnd1.get("id");
                   String vnfname = (String) vfnd1.get("name");
                   System.out.println(vnfname);
                }

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } 
          catch (IOException e) {
		e.printStackTrace();
	  } catch (ParseException e) {
		e.printStackTrace();
	  }

      }
}
