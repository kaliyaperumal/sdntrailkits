import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public class TempFile {

	public static void main(String[] args) throws Exception {
		File tmpFile = File.createTempFile("VzVM","x");
                String name = tmpFile.getName();
                tmpFile.delete();
	}

}
