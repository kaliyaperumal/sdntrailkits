import java.util.StringTokenizer;


public class ExtractField {

        /**
         * @param args
         */
        public static String brIPAddress(String inputStr) {

                 StringTokenizer tokenizer = new StringTokenizer(inputStr, ":");
                 while (tokenizer.hasMoreTokens())
                 {
                         String token = tokenizer.nextToken().trim();
                         if (token.startsWith("Exit"))
                         {
                                 int br_ip = token.indexOf("BR-IP");
                                 int br_ip_start = token.indexOf('=',br_ip);
                                 int br_ip_end = token.indexOf(',',br_ip);
                                 return(token.substring(br_ip_start+1, br_ip_end));
                         }
                 }
                 return(null);
        }

}

