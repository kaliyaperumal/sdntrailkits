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


public class GetVNFServerIP {

	public static void main(String[] args) {

	  try {

		URL url = new URL("http://10.76.190.80:5000/v2.0/tenants");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("X-Auth-Token", args[0]);

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
                StringBuffer buf = new StringBuffer();
		while ((output = br.readLine()) != null) {
			buf.append(output);
		}
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(buf.toString());
                JSONObject object = (JSONObject)((JSONArray)obj.get("tenants")).get(0);
                String serviceTenantId = (String)object.get("id");
                System.out.println("serviceTenantId:"+ serviceTenantId);
		conn.disconnect();

                url = new URL ("http://10.76.190.80:8774/v2.1/"+ serviceTenantId + "/servers");
                conn = (HttpURLConnection)url.openConnection();
                                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Auth-Token", args[0]);

                if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                        + conn.getResponseCode());
                }

                br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                buf = new StringBuffer();
                while ((output = br.readLine()) != null) {
                        buf.append(output);
                }
                JSONObject servers = (JSONObject)parser.parse(buf.toString());
                JSONObject firstserver = (JSONObject)((JSONArray)servers.get("servers")).get(0);
                JSONObject firstLink = (JSONObject)((JSONArray)firstserver.get("links")).get(0);
                String urlofinstance = (String)firstLink.get("href");
                System.out.println(urlofinstance);
                conn.disconnect();

                url = new URL (urlofinstance);
                conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Auth-Token", args[0]);

                if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                        + conn.getResponseCode());
                }

                br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                buf = new StringBuffer();
                while ((output = br.readLine()) != null) {
                        buf.append(output);
                }
                JSONObject server = (JSONObject)parser.parse(buf.toString());
                server = (JSONObject)server.get("server");
                JSONObject addresses = (JSONObject)server.get("addresses");
                JSONArray netmgmt = (JSONArray)addresses.get("net_mgmt");
                JSONObject fnet_mgmt = (JSONObject) netmgmt.get(0);
                System.out.println(fnet_mgmt.get("addr"));
             
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
