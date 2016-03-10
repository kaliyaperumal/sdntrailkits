import org.clapper.util.misc.FileHashMap;
import org.clapper.util.misc.ObjectExistsException;
import java.io.IOException;
public class Hash{
  public static void main(String[] arg) throws Exception  {
     FileHashMap<String,String> fhm = new FileHashMap<String,String>("/tmp/mymap",0);
     fhm.put("of:0000c4e9844eda5f","2b5a5bd1-34a8-4187-a4bb-86e42f015a76");
     fhm.close();
  }    
}
