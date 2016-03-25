package sk.fiit.dprs.dbnode.api.services;

/**
 * Handle ping concrete database node
 * 
 * @author Jozef Zatko
 */
public class PingRequestor {
	
	/**
	 * Ping concrete database node
	 * 
	 * @return ping message
	 * @throws Exception
	 */
	public String ping(String adress, String RESTaddr) throws Exception {
		
		double pingTime = getPingTime(adress, RESTaddr);
		
		return "Ping to node http://" + adress + " was successful. Time: " + pingTime + " ms.";
	}
	
	/**
	 * Ping all active database nodes
	 * 
	 * @return ping message
	 * @throws Exception
	 */
	public String pingAllNodes() throws Exception {

		return "Not implemented yet.";
	}
	
	/**
	 * Ping concrete database node
	 * 
	 * @return time in millis
	 * @throws Exception
	 */
	private double getPingTime(String adress, String RESTaddr) throws Exception {
		
		double timeStart = System.currentTimeMillis();
		
		new RESTRequestor("GET", adress + RESTaddr).request();

		return (System.currentTimeMillis() - timeStart) / 2.0;
	}
}
