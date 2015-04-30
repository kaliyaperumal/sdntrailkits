package lbtrial.lbrouting;
import java.util.Dictionary;
import java.util.Hashtable;
 
import org.apache.felix.dm.Component;
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
//import org.opendaylight.controller.sal.packet.IDataPacketService;
//iimport org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.sal.routing.IListenRoutingUpdates;
import org.opendaylight.controller.sal.routing.IRouting;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.sal.reader.IReadService;
import org.opendaylight.controller.topologymanager.ITopologyManager;
import org.opendaylight.controller.topologymanager.ITopologyManagerAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class Activator extends ComponentActivatorAbstractBase {
 
    private static final Logger log = LoggerFactory.getLogger(LbRoutingImplementation.class);
    public Object[] getImplementations() {
        log.trace("Getting Implementations");
 
        Object[] res = { LbRoutingImplementation.class };
        return res;
    }
 
    public void configureInstance(Component c, Object imp, String containerName) {
        log.trace("Configuring instance");

        if (imp.equals(LbRoutingImplementation.class)) {
 
            // Define exported and used services for LbRoutingImplementation component.
 
          Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("topoListenerName", "routing.loadbalancing");
 
            // Export IListenDataPacket interface to receive packet-in events.
            //c.setInterface(new String[] {IListenDataPacket.class.getName()}, props);
            c.setInterface(new String[] {ITopologyManagerAware.class.getName(), IRouting.class.getName() }, props);
            // Need the DataPacketService for encoding, decoding, sending data packets
            c.add(createContainerServiceDependency(containerName).setService(IListenRoutingUpdates.class).setCallbacks("setListenRoutingUpdates", "unsetListenRoutingUpdates").setRequired(false));
	     c.add(createContainerServiceDependency(containerName).setService(ISwitchManager.class).setCallbacks("setSwitchManager", "unsetSwitchManager").setRequired(true));
	     c.add(createContainerServiceDependency(containerName).setService(ITopologyManager.class).setCallbacks("setTopologyManager", "unsetTopologyManager").setRequired(true));
	     c.add(createContainerServiceDependency(containerName).setService(IReadService.class).setCallbacks("setReadService", "unsetReadService").setRequired(true));
 
        }
  }
}
