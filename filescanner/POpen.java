import java.io.IOException;

public class POpen {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		java.lang.Process process = Runtime.getRuntime().exec("tail -0f /tmp/raman.txt");
		
		byte buf[] = new byte[1024];
		
		while (process.getInputStream().read(buf) != -1)
		{
			String str = new String(buf);
			if (str.contains("DOMAIN-5-TCA"))
			{
			   System.out.print(str);
			}
		}
	}

}
