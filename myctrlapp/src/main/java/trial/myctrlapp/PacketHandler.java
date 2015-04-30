package trial.myctrlapp;
 
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.sal.packet.IPv4;
import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.PacketResult;
import org.opendaylight.controller.sal.packet.RawPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class PacketHandler implements IListenDataPacket {
 
 private static final Logger log = LoggerFactory.getLogger(PacketHandler.class);
    private IDataPacketService dataPacketService;
 
    static private InetAddress intToInetAddress(int i) {
        byte b[] = new byte[] { (byte) ((i>>24)&0xff), (byte) ((i>>16)&0xff), (byte) ((i>>8)&0xff), (byte) (i&0xff) };
       InetAddress addr;
        try {
            addr = InetAddress.getByAddress(b);
        } catch (UnknownHostException e) {
            return null;
       }
 
        return addr;
    }
 
    /*
     * Sets a reference to the requested DataPacketService
     * See Activator.configureInstance(...):
     * c.add(createContainerServiceDependency(containerName).setService(
     * IDataPacketService.class).setCallbacks(
    * "setDataPacketService", "unsetDataPacketService")
     * .setRequired(true));
     */
    void setDataPacketService(IDataPacketService s) {
        log.trace("Set DataPacketService.");
	System.out.println("Set DataPacketService.");

        dataPacketService = s;
    }
 
    /*
     * Unsets DataPacketService
     * See Activator.configureInstance(...):
     * c.add(createContainerServiceDependency(containerName).setService(
     * IDataPacketService.class).setCallbacks(
     * "setDataPacketService", "unsetDataPacketService")
     * .setRequired(true));
     */
    void unsetDataPacketService(IDataPacketService s) {
        log.trace("Removed DataPacketService.");
	System.out.println("Removed DataPacketService.");
 
       if (dataPacketService == s) {
            dataPacketService = null;
        }
    }
 
   @Override
    public PacketResult receiveDataPacket(RawPacket inPkt) {
        log.trace("Received data packet.");
 
        // The connector, the packet came from ("port")
        NodeConnector ingressConnector = inPkt.getIncomingNodeConnector();
        // The node that received the packet ("switch")
        Node node = ingressConnector.getNode();
	System.out.println("Packet is recvd");
 
        // Use DataPacketService to decode the packet.
        Packet l2pkt = dataPacketService.decodeDataPacket(inPkt);
 
        if (l2pkt instanceof Ethernet) {
            Object l3Pkt = l2pkt.getPayload();
            if (l3Pkt instanceof IPv4) {
               IPv4 ipv4Pkt = (IPv4) l3Pkt;
                int dstAddr = ipv4Pkt.getDestinationAddress();
                InetAddress addr = intToInetAddress(dstAddr);
                System.out.println("Pkt. to " + addr.toString() + " received by node " + node.getNodeIDString() + " on connector " + ingressConnector.getNodeConnectorIDString());
                return PacketResult.KEEP_PROCESSING;
           }
        }
        // We did not process the packet -> let someone else do the job.
        return PacketResult.IGNORED;
    }
}
