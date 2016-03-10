import com.jcraft.jsch.*;
import java.io.*;

public class Exec{
  public static void main(String[] arg){
    
    try{
      JSch jsch=new JSch();


      Session session=jsch.getSession("root", "192.168.120.10", 22);

      java.util.Properties config = new java.util.Properties(); 
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
     
      session.setPassword("root");
      session.connect(30000);   // making a connection with timeout.

      Channel channel=session.openChannel("exec");

      channel.setInputStream(null);

      ((ChannelExec)channel).setErrStream(System.err);

      InputStream in=channel.getInputStream();

      ((ChannelExec)channel).setCommand("ls -l > exec.txt");
      channel.connect();
      readInputStream(in,channel);

      ((ChannelExec)channel).setCommand("netstat -n > netstat.txt");
      channel.connect();
      readInputStream(in,channel);

      channel.disconnect();
      session.disconnect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  private static void readInputStream(InputStream in, Channel channel) throws IOException
  {
      byte[] tmp=new byte[1024];
      while(true){
        while(in.available()>0){
          int i=in.read(tmp, 0, 1024);
          if(i<0)break;
          System.out.print(new String(tmp, 0, i));
        }
        if(channel.isClosed()){
          if(in.available()>0) continue;
          System.out.println("exit-status: "+channel.getExitStatus());
          break;
        }
        try{Thread.sleep(1000);}catch(Exception ee){}
      }
  }
}
