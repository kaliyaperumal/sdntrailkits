
public class NetworkMetric {

        private int siteId;
	private int packetdrops;
	private int portutilization;

	public NetworkMetric(int siteId, int packetdrops,  int portutilization) {
		super();
                this.siteId = siteId;
		this.packetdrops = packetdrops;
		this.portutilization = portutilization;
	}

        public int getSiteId()
        {
               return(siteId);
        }

        public void setSiteId( int siteId )
        {
               this.siteId = siteId;
        }

	public int getPacketdrops() {
		return packetdrops;
	}

	public void setPacketdrops(int packetdrops) {
		this.packetdrops = packetdrops;
	}


	public int getPortutilization() {
		return portutilization;
	}

	public void setPortutilization(int portutilization) {
		this.portutilization = portutilization;
	}
}
