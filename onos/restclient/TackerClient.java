import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.util.Properties;
import java.util.HashMap;

class Token {

	Properties prop;
	String tokenId = new String();
	// http://localhost:8080/RESTfulExample/json/product/post
	
	Token(Properties p) {
		prop = new Properties(p);
	}

	public void fetchToken()
	{
		try {

			//URL url = new URL("http://10.76.190.80:5000/v2.0/tokens");
			URL url = new URL(prop.getProperty("keystone.url"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			String input = 
				"{\n"+ 
				"    \"auth\": {\n" +
				"       \"passwordCredentials\": {\n" +
				"           \"password\": \"" + prop.getProperty("tacker.password") + "\",\n" +
				"           \"username\": \"" + prop.getProperty("tacker.username")  + "\"\n" +
				"       },\n" +
				"       \"tenantName\": \"" + prop.getProperty("tacker.domain") + "\"\n" +
				"   }\n" +
				"}";
			System.out.println(input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			/*
			   if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
			   throw new RuntimeException("Failed : HTTP error code : "
			   + conn.getResponseCode());
			   }
			   */
			BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

			String output;
			StringBuffer buf = new StringBuffer();
			while ((output = br.readLine()) != null) {
				buf.append(output);
			}
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(buf.toString());
			HashMap map = (HashMap)((HashMap)obj.get("access")).get("token");
			tokenId = (String) map.get("id");
			System.out.println(tokenId);

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		return;
	}

	public String getToken() {
		return tokenId;
	}
}


class VNFCatalog {

	public VNFCatalog()
	{
	}

	// http://localhost:8080/RESTfulExample/json/product/post
	public String  getVnfCatalog(Properties prop, String token) {

		String strOutput = "";

		try {

			//URL url = new URL("http://10.76.190.80:8888/v1.0/vnfds.json");
			URL url = new URL(prop.getProperty("tacker.url") + "/vnfds.json");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("User-Agent", "python-tackerclient");
			conn.setRequestProperty("X-Auth-Token", token);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

			String s;
			System.out.println("Output from Server .... \n");
			while ((s = br.readLine()) != null) {
				strOutput = strOutput + "\n" + s;
				System.out.println(s);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return strOutput;
	}
}

class TackerClient {

	public static void main(String[] args) throws Exception{
		Properties prop = new Properties();

		String fileName = "tacker.config";
		try {
			InputStream is = new FileInputStream(fileName);
			prop.load(is);
		}
		catch(FileNotFoundException e) {
		}
		catch(IOException e) {
		}

		System.out.println(prop.getProperty("tacker.url"));
		System.out.println(prop.getProperty("tacker.username"));
		System.out.println(prop.getProperty("tacker.domain"));

		Token tk = new Token(prop);
		tk.fetchToken();

		System.out.println("Token = " + tk.getToken());

		String vnfdList = new VNFCatalog().getVnfCatalog(prop, tk.getToken());
		System.out.println("================= " + vnfdList);
		System.out.println("=================");

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(vnfdList.toString());
			//HashMap map = (HashMap)((HashMap)obj.get("vnfds")).get("id");
			JSONArray arr = (JSONArray)obj.get("vnfds");
			//String s1 = (String) arr.get(0);
			System.out.println((arr.get(0)).get("id"));
	}
}
