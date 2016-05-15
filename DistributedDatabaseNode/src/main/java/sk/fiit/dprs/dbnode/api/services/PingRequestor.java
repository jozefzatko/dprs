package sk.fiit.dprs.dbnode.api.services;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import sk.fiit.dprs.dbnode.Main;
import sk.fiit.dprs.dbnode.healthcheck.HealthCheckParser;

/**
 * Handle a ping requests
 * 
 * @author Jozef Zatko
 */
public class PingRequestor {
	
	static Logger log = Logger.getLogger(Main.class.getName());
	
	/**
	 * Ping concrete database node
	 * 
	 * @return ping message
	 * @throws Exception
	 */
	public String ping(String adress, String RESTaddr) throws Exception {
		
		double pingTime = getPingTime(adress, RESTaddr);
		log.info("Ping from node " + InetAddress.getLocalHost().getHostAddress() + " to node http://" + adress + " was successful. Time: " + pingTime + " ms.");
		return "Ping from node " + InetAddress.getLocalHost().getHostAddress() + " to node http://" + adress + " was successful. Time: " + pingTime + " ms.";
	}
	
	/**
	 * Ping all active database nodes
	 * 
	 * @return ping message
	 * @throws Exception
	 */
	public String pingAllNodes(String consulURL) throws Exception {
		
		log.info("Request: GET http://" + consulURL + "/v1/catalog/service/dbnode");
		String allNodes = new RESTRequestor("GET", "http://" + consulURL + "/v1/catalog/service/dbnode").request();
		log.info("Response: " + allNodes);
	
		JSONArray jsonArray = new JSONArray(allNodes);
		JSONObject jsonObj;
		String pingResponse = "";
	
		for(int i=0; i<jsonArray.length(); i++) {
				
			jsonObj = jsonArray.getJSONObject(i);
			
			try {
				pingResponse += ping("http://" + jsonObj.getString("ServiceAddress") + ":" + ((int)jsonObj.get("ServicePort")), "/ping") + "\n";
			} catch(Exception e) {
				pingResponse += "Cannot ping node " + jsonObj.getString("ServiceAddress") + ":" + ((int)jsonObj.get("ServicePort"));
			}
		}
		
		return pingResponse;
	}
	
	/**
	 * Ping all healthy database nodes
	 * 
	 * @return ping message
	 * @throws Exception
	 */
	public String pingHealthyNodes(String consulURL) throws Exception {
		
		String pingResponse = "";
		
		for(String s : HealthCheckParser.getHealthyNodes(consulURL)) {
			
			try {
				pingResponse += ping("http://" + s, "/ping") + "\n";
			} catch(Exception e) {
				pingResponse += "Cannot ping node " + s;
			}
		}
		
		if("".equals(pingResponse)) {
			
			pingResponse = "Cannot found any other healthy nodes.\n";
		}
		
		return pingResponse;
	}
	
	/**
	 * Ping concrete database node
	 * 
	 * @return time in milliseconds
	 * @throws Exception
	 */
	private double getPingTime(String adress, String RESTaddr) throws Exception {
		
		double timeStart = System.currentTimeMillis();		
		new RESTRequestor("GET", adress + RESTaddr).request();
		return (System.currentTimeMillis() - timeStart) / 2.0;
	}
}
