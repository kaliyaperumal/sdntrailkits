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

import java.util.HashMap;


public class GetVNF {

	// http://localhost:8080/RESTfulExample/json/product/post
	public static void main(String[] args) {

	  try {

		URL url = new URL("http://10.76.190.80:8888/v1.0/vnfs/"+ args[1] + ".json");
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
               JSONObject vnfInfo = (JSONObject)parser.parse(buf.toString());
               JSONObject vnf = (JSONObject)vnfInfo.get("vnf");
               System.out.println((String) vnf.get("id"));

               String status = (String) vnf.get("status");
               System.out.println(status);
               

               String mgmt = (String) vnf.get("mgmt_url");
               System.out.println(mgmt);

               String piece[] = mgmt.split("\"");
               System.out.println(piece[3]);
	       conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
         catch (ParseException e) {
                e.printStackTrace();
         }


      }
}
