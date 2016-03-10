import org.clapper.util.misc.FileHashMap;
import java.util.StringTokenizer;
public class HashRead{
  public static void main(String[] arg) throws Exception  {
     FileHashMap<String,String> fhm = new FileHashMap<String,String>("/home/training/ONOS/apache-karaf-3.0.5/vcpe",0);
     System.out.println(fhm.get("of:0000c4e9844eda5f"));
     String vnfData = fhm.get("of:0000c4e9844eda5f");
     StringTokenizer stz = new StringTokenizer(vnfData,"|");
     System.out.println(stz.countTokens());
     fhm.close();
  }    
}
