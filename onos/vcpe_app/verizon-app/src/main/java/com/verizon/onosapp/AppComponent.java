/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.verizon.onosapp;

//import org.apache.felix.scr.annotations.*;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.felix.scr.annotations.*;
import org.clapper.util.misc.FileHashMap;
import org.json.simple.JSONArray;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.device.*;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


import java.net.MalformedURLException;


import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {
    

    FileHashMap<String,String> fhm = null;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private DeviceListener deviceListener;
    private  HostListener hostListener;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

     @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceAdminService adminService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceProviderRegistry providerRegistry;


    private HashMap<String,String> appConfig = new HashMap<String, String>();
    @Activate
    protected void activate() {

        deviceListener = new InnerDeviceListener();
        deviceService.addListener(deviceListener);

        hostListener= new InnerHostListener();
        hostService.addListener(hostListener);

        appConfig.put("keystone.url", "http://10.76.190.80:5000/v2.0/tokens");
        appConfig.put("tacker.url", "http://10.76.190.80:8888/v1.0");
        appConfig.put("tacker.domain", "demo");
        appConfig.put("tacker.username","admin");
        appConfig.put("tacker.password","devstack");
        appConfig.put("tacker.vnfdname","openwrt-cc-router-custom-2-vnfd");



        appConfig.put("keystone.url.tenants", "http://10.76.190.80:5000/v2.0/tenants");
        appConfig.put("service.username", "user1");
        appConfig.put("service.password", "devstack");
        appConfig.put("service.domain", "service");

        appConfig.put("compute.url", "http://10.76.190.80:8774/v2.1/");

        appConfig.put("openwrt.user", "root");
        appConfig.put("openwrt.password", "root");


        log.info("Reading config file");
        readAppConfig();

        try {
            fhm = new FileHashMap<String,String>("vcpe",0);
            fhm.put("Hello","Hello");
            fhm.save();
        }
        catch (Exception ex)
        {
            log.error("Exception during opening FileHashMap",ex);
        }
        getToken("tacker");
        getVNFDS();
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        deviceService.removeListener(deviceListener);
        try {
            fhm.close();
        }
        catch (Exception ex){log.error("Error during FileHashMap closing",ex);}

        log.info("Stopped");
    }

    // Triggers driver setup when a device is (re)detected.
    private class InnerDeviceListener implements DeviceListener {
        @Override
        public void event(DeviceEvent event) {
            log.info(event.toString());
            switch (event.type()) {
                case DEVICE_ADDED:
 /*                   log.info("DEVICE Added, getting Tacker Token");
                    log.debug("Connecting to:" + appConfig.get("keystone.url"));
                    getToken("tacker");
                    getVNFDS();
                    createVNF();
*/
                    break;
                case DEVICE_AVAILABILITY_CHANGED:
                case PORT_STATS_UPDATED:


                    Iterable<Device> list = deviceService.getAvailableDevices();
                    log.info("Listing all devices");
                    for (Device d : list)
                    {
                        log.info(d.toString());
                        log.info("Device MAC address"+ d.id().toString());
                        if (fhm != null && fhm.get(d.id().toString()) == null)
                        {
                            log.info("Connecting to:" + appConfig.get("keystone.url"));
                            getToken("tacker");
                            //getVNFDS();
                            log.info("Creating Device");
                            String vnfid = createVNF();
                            fhm.put(d.id().toString(),vnfid);
                            log.info("Created Device with vnfid:"+ vnfid);
                        }
                        else
                        {
                            log.info("Found Device  Hash table:"+ fhm.get(d.id().toString()));
                            String vnfData = fhm.get(d.id().toString());
                            StringTokenizer stz = new StringTokenizer(vnfData,"|");
                            if (stz.countTokens() == 1) // We don't have the IP address of the VNF
                            {
                                log.info("Looking for management ip for "+ vnfData);
                                getToken("tacker");
                                String ip = getVNFMgmtIP(vnfData);
                                if (ip != null)
                                {
                                    log.info ("Found mgmt IP for vnfid:" + vnfData + " mgmt ip:" + ip);
                                    fhm.put(d.id().toString(),vnfData+'|'+ ip);
                                }
                            }
                        }
                    }
                    if (fhm != null) {
                        try {
                            fhm.save();
                        } catch (Exception ex) {
                            log.error("Exception during Closing FileHashMap", ex);
                            break;
                        }
                    }

                    break;
                // TODO other cases
                case DEVICE_UPDATED:
   //                 log.info("DEVICE Update, getting Tacker Token");
                    //getToken("tacker");
                    //getVNFDS();
                   // createVNF();
                    break;
                case DEVICE_REMOVED:
                    break;
                case DEVICE_SUSPENDED:
                    break;
                case PORT_ADDED:
                    break;
                case PORT_UPDATED:
                    break;
                case PORT_REMOVED:
                    break;
                default:
                    break;
            }
        }
    }
    private void getVNFDS()
    {
        try {

            URL url = new URL(appConfig.get("tacker.url")+"/vnfds.json");
            log.info("Connecting to:" + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "python-tackerclient");
            conn.setRequestProperty("X-Auth-Token", (String) appConfig.get("tacker.token"));

            if (conn.getResponseCode() != 200) {
                log.error("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuffer buf = new StringBuffer();
            while ((output = br.readLine()) != null) {
                buf.append(output);
            }
            log.info(buf.toString());
            JSONParser parser = new JSONParser();
            JSONObject vfnd = (JSONObject)parser.parse(buf.toString());
            JSONArray vfnds = (JSONArray)vfnd.get("vnfds");
            String vnfid = null;
            for (int i=0; i< vfnds.size();i++)
            {
                JSONObject vfnd1 = (JSONObject)vfnds.get(i);
                String vnfname = (String) vfnd1.get("name");
                if (vnfname.equals(appConfig.get("tacker.vnfdname"))) {
                    vnfid = (String) vfnd1.get("id");
                }
             }
            if (vnfid == null)
            {
                log.error("No vnf id found for:"+ appConfig.get("tacker.vnfdname"));
            }
            appConfig.put("vnfid",vnfid);
            log.info("VNF ID:" + vnfid);
            conn.disconnect();

        } catch (MalformedURLException e) {

            log.error("Malformed", e);

        } catch (IOException e) {

            log.error ("IOException", e);
        }
        catch (ParseException e) {
            log.error ("ParseException", e);
        }

    }
    private String createVNF()
    {
        String vnfid = null;
        try {
            URL url = new URL(appConfig.get("tacker.url")+"/vnfs.json");
            log.info("Connecting to:" + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "python-tackerclient");
            conn.setRequestProperty("X-Auth-Token", (String) appConfig.get("tacker.token"));

            File tmpFile = File.createTempFile("VzVM","x");
            String vmname = tmpFile.getName();
            tmpFile.delete();


            String input = "{\"vnf\": {\"attributes\": {\"config\": null, \"param_values\": null}, \"vnfd_id\": \"" + appConfig.get("vnfid") + "\", \"name\": \"" + vmname + "\"}}";
            log.info(input);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuffer buf = new StringBuffer();
            while ((output = br.readLine()) != null) {
                buf.append(output);
            }
            JSONParser parser = new JSONParser();
            JSONObject vnfInfo = (JSONObject)parser.parse(buf.toString());
            JSONObject vnf = (JSONObject)vnfInfo.get("vnf");
            vnfid = (String)vnf.get("id");
            log.info(buf.toString());

            conn.disconnect();

        } catch (MalformedURLException e) {

            log.error("Malformed", e);

        } catch (IOException e) {

            log.error ("IOException", e);
        }
        catch (ParseException e) {
            log.error ("ParseException", e);
        }
        return vnfid;
    }

    private String getVNFMgmtIP(String vnfId)
    {
        String ipAddress = null;
        try {

            log.info("URL:"+ appConfig.get("tacker.url")+ "/vnfs/"+ vnfId + ".json");
            URL url = new URL(appConfig.get("tacker.url")+ "/vnfs/"+ vnfId + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "python-tackerclient");
            conn.setRequestProperty("X-Auth-Token",
                    (String) appConfig.get("tacker.token"));
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuffer buf = new StringBuffer();

            while ((output = br.readLine()) != null) {
                buf.append(output);
            }

            JSONParser parser = new JSONParser();
            JSONObject vnfInfo = (JSONObject)parser.parse(buf.toString());
            JSONObject vnf = (JSONObject)vnfInfo.get("vnf");

            String status = (String) vnf.get("status");
            String mgmt = (String) vnf.get("mgmt_url");
            if (status.equals("ACTIVE") && mgmt != null) {

                String piece[] = mgmt.split("\"");
                if (piece.length > 4) {
                    ipAddress = piece[3];
                }
            }
            conn.disconnect();
            } catch (MalformedURLException e) {

                log.error("Malformed", e);

            } catch (IOException e) {

                log.error ("IOException", e);
            }
            catch (ParseException e) {
                log.error ("ParseException", e);
            }

        return ipAddress;
    }


    private class InnerHostListener implements HostListener {
        @Override
        public void event(HostEvent event) {
            log.debug("Host Event: time = {} type = {} event = {}",
                    event.time(), event.type(), event);

        }
    }
    private void readAppConfig()
    {
         try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("tackerconfig.properties")));
            String line;
             log.info("Reading input file");
            while ((line = reader.readLine()) != null)
            {
                log.info(line);
                StringTokenizer st = new StringTokenizer(line,"=");
                appConfig.put(st.nextToken(),st.nextToken());
            }
            reader.close();

        }
        catch (Exception ex) { log.error("Error reading file",ex);}
    }
    private String  getCreatedInstanceIP()
    {
              getToken("service");
        String ipAddress = null;
          try {

                URL url = new URL("http://10.76.190.80:5000/v2.0/tenants");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Auth-Token", appConfig.get("service.token"));

                if (conn.getResponseCode() != 200) {
                log.error("Failed : HTTP error code : "
                        + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                StringBuffer buf = new StringBuffer();
                while ((output = br.readLine()) != null) {
                        buf.append(output);
                }
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(buf.toString());
                JSONObject object = (JSONObject)((JSONArray)obj.get("tenants")).get(0);
                String serviceTenantId = (String)object.get("id");
                log.info("serviceTenantId:"+ serviceTenantId);
                conn.disconnect();

               url = new URL (appConfig.get("compute.url")+ serviceTenantId + "/servers");
                conn = (HttpURLConnection)url.openConnection();
                                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Auth-Token", appConfig.get("service.token"));

                if (conn.getResponseCode() != 200) {
                       log.error("Failed : HTTP error code : "
                        + conn.getResponseCode());
                }

                br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                buf = new StringBuffer();
                while ((output = br.readLine()) != null) {
                        buf.append(output);
                }
                JSONObject servers = (JSONObject)parser.parse(buf.toString());
                JSONObject firstserver = (JSONObject)((JSONArray)servers.get("servers")).get(0);
                JSONObject firstLink = (JSONObject)((JSONArray)firstserver.get("links")).get(0);
                String urlofinstance = (String)firstLink.get("href");
                log.info(urlofinstance);
                conn.disconnect();
                url = new URL (urlofinstance);

                conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Auth-Token", appConfig.get("service.token"));

                if (conn.getResponseCode() != 200) {
                       log.error("Failed : HTTP error code : "
                        + conn.getResponseCode());
                }

                br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                buf = new StringBuffer();
                while ((output = br.readLine()) != null) {
                        buf.append(output);
                }
                JSONObject server = (JSONObject)parser.parse(buf.toString());
                server = (JSONObject)server.get("server");
                JSONObject addresses = (JSONObject)server.get("addresses");
                JSONArray netmgmt = (JSONArray)addresses.get("net_mgmt");
                JSONObject fnet_mgmt = (JSONObject) netmgmt.get(0);
                ipAddress = (String)fnet_mgmt.get("addr");
                log.info("ipAddress=" + ipAddress);
          } catch (MalformedURLException e) {
                e.printStackTrace();
          } catch (IOException e) {
                e.printStackTrace();
          }
         catch (ParseException e) {
                e.printStackTrace();
         }
        return (ipAddress);
      }
      private  void  getToken(String tokenType)
      {
            try {

                //URL url = new URL("http://172.16.197.132:5000/v2.0/tokens");
                log.info("Connecting to:" + appConfig.get("keystone.url"));
                URL url = new URL(appConfig.get("keystone.url"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String input =
                        "{\n"+
                                "    \"auth\": {\n" +
                                "       \"passwordCredentials\": {\n" +
                                "           \"password\": \""+ appConfig.get(tokenType+".password")+"\",\n" +
                                "           \"username\": \""+appConfig.get(tokenType+".username")+"\"\n" +
                                "       },\n" +
                                "       \"tenantName\": \""+appConfig.get(tokenType+".domain")+"\"\n" +
                                "   }\n" +
                                "}";
                log.info(input);
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                StringBuffer buf = new StringBuffer();
                while ((output = br.readLine()) != null) {
                    buf.append(output);
                }
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(buf.toString());
                HashMap map = (HashMap)((HashMap)obj.get("access")).get("token");
                appConfig.put(tokenType+".token",(String)map.get("id"));

                log.info((String) map.get("id"));
                conn.disconnect();

            } catch (MalformedURLException e) {

                log.error("Malformed", e);

            } catch (IOException e) {

                log.error ("IOException", e);
            }
            catch (ParseException e) {
                log.error ("ParseException", e);
            }
      }
    private  void readInputStream(InputStream in, Channel channel) throws IOException
    {
        byte[] tmp=new byte[1024];
        while(true){
            while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                log.info(new String(tmp, 0, i));
            }
            if(channel.isClosed()){
                if(in.available()>0) continue;
                log.info("exit-status: "+channel.getExitStatus());
                break;
            }
            try{Thread.sleep(1000);}catch(Exception ee){}
        }
    }
    public  void execSSHonRouter(String userid, String password, String ipAddress){

        try{
            JSch jsch=new JSch();


            Session session=jsch.getSession(userid, ipAddress, 22);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.setPassword(password);
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
            log.error("execSSHonRouter", e);
        }
    }


}
