package sk.fiit.dprs.dbnode.api.services;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Handle a ping requests
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
	public String pingAllNodes(String consulURL) throws Exception {

		//String allNodes = "[{\"ServiceAddress\":\"127.0.0.1\",\"ServicePort\":\"5001\"},{\"ServiceAddress\":\"127.0.0.1\",\"ServicePort\":\"5002\"}]";
		String allNodes = new RESTRequestor("GET", consulURL + "/v1/catalog/service/dbnode").request();
		
		JSONArray jsonArray = new JSONArray(allNodes);
		
		String pingResponse = "";
		JSONObject jsonObj;
	
		for(int i=0; i<jsonArray.length(); i++) {
				
			jsonObj = (JSONObject) jsonArray.get(i);
			pingResponse += ping("http://" + jsonObj.getString("ServiceAddress") + ":" + jsonObj.getString("ServicePort"), "/ping") + "\n";
		}
		
		return pingResponse;
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
