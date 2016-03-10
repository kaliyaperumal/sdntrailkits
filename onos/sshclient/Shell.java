import com.jcraft.jsch.*;

public class Shell{
  public static void main(String[] arg){
    
    try{
      JSch jsch=new JSch();


      Session session=jsch.getSession("root", "192.168.120.10", 22);

      java.util.Properties config = new java.util.Properties(); 
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
     
      session.setPassword("root");
      session.connect(30000);   // making a connection with timeout.
      Channel channel=session.openChannel("shell");
      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
      channel.connect(3*1000);
      
    }
    catch(Exception e){
      System.out.println(e);
    }
  }
}
