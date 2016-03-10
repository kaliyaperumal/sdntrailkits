import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import java.util.HashMap;

public class GetToken1 {

	// http://localhost:8080/RESTfulExample/json/product/post
	public static void main(String[] args) {

	  try {

		//URL url = new URL("http://172.16.197.132:5000/v2.0/tokens");
		URL url = new URL("http://10.76.190.80:5000/v2.0/tokens");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		String input = 
"{\n"+ 
"    \"auth\": {\n" +
"       \"passwordCredentials\": {\n" +
"           \"password\": \"devstack\",\n" +
"           \"username\": \"admin\"\n" +
"       },\n" +
"       \"tenantName\": \"demo\"\n" +
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
                System.out.println(buf.toString());

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();
	 }

	}

}
