import com.jcraft.jsch.*;
import java.io.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RemoveFirewallEntries{
  public static void main(String[] arg){
    
    try{

//
	File file = new File("/tmp/iprules.save");
        file.delete();
        file.createNewFile();
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
//
      JSch jsch=new JSch();

      boolean firewallchanged = false;
      Session session=jsch.getSession("training", "10.76.190.80", 22);

      java.util.Properties config = new java.util.Properties(); 
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
     
      session.setPassword("vdsi@123");
      session.connect(30000);   // making a connection with timeout.

      Channel channel=session.openChannel("exec");

      channel.setInputStream(null);

      ((ChannelExec)channel).setErrStream(System.err);

      InputStream in=channel.getInputStream();

      ((ChannelExec)channel).setCommand("sudo -S -p '' "+ "iptables-save");
      channel.connect();
      String str = readInputStream(in,channel);
      //System.out.println(str);
      String[] lines = str.split(System.getProperty("line.separator"));
      for (int i=0;i< lines.length;i++)
      {
        if (lines[i].indexOf("--comment \"Allow traffic from defined IP/MAC pairs.\"") != -1 ||
        lines[i].indexOf("--comment \"Drop traffic without an IP/MAC allow rule.") != -1)
        {
            firewallchanged=true;
            continue;
        }
	bw.write(lines[i]);
	bw.write("\n");
      } 
      bw.close();

      if (firewallchanged)
      {
          ((ChannelExec)channel).setCommand("sudo -S -p '' "+ "iptables-restore </tmp/iprules.save");
      channel.connect();
          String strc = readInputStream(in,channel);
          System.out.println("Firewall changed:"+ strc);
      }
      channel.disconnect();
      session.disconnect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  private static String readInputStream(InputStream in, Channel channel) throws IOException
  {
      StringBuffer buf = new StringBuffer();
      byte[] tmp=new byte[1024];
      while(true){
        while(in.available()>0){
          int i=in.read(tmp, 0, 1024);
          if(i<0)break;
          String str = new String(tmp, 0, i);
          buf.append(str);
        }
        if(channel.isClosed()){
          if(in.available()>0) continue;
          //buf.append("exit-status: "+channel.getExitStatus());
          break;
        }
        try{Thread.sleep(1000);}catch(Exception ee){}
      }
      return buf.toString();
  }
}
