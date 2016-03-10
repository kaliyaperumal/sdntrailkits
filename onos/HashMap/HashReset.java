import org.clapper.util.misc.FileHashMap;
public class HashReset{
  public static void main(String[] arg) throws Exception  {
     FileHashMap<String,String> fhm = new FileHashMap<String,String>("/home/training/ONOS/apache-karaf-3.0.5/vcpe",0);
     fhm.clear();
     fhm.save();
     fhm.close();
  }    
}
