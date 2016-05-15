package sk.fiit.dprs.dbnode.healthcheck;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.healthcheck.models.Check;
import sk.fiit.dprs.dbnode.healthcheck.models.HealthNode;

/**
 * Parse Consul's /v1/health/service/dbnode response
 * 
 * @author Jozef Zatko
 */
public class HealthCheckParser {
	
	private static String str = "[ {\"Node\":{ \"Node\":\"consul\", \"Address\":\"172.17.0.2\"},\"Service\":{ \"ID\":\"b2b289fe8558:dbnode1:4567\", \"Service\":\"dbnode\", \"Tags\":null, \"Address\":\"10.0.9.3\", \"Port\":4567},\"Checks\":[ {\"Node\":\"consul\",\"CheckID\":\"service:b2b289fe8558:dbnode1:4567\",\"Name\":\"Service 'dbnode' check\",\"Status\":\"critical\",\"Notes\":\"\",\"Output\":\"TTL expired\",\"ServiceID\":\"b2b289fe8558:dbnode1:4567\",\"ServiceName\":\"dbnode\" }, {\"Node\":\"consul\",\"CheckID\":\"serfHealth\",\"Name\":\"Serf Health Status\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"Agent alive and reachable\",\"ServiceID\":\"\",\"ServiceName\":\"\" }] }, {\"Node\":{ \"Node\":\"consul\", \"Address\":\"172.17.0.2\"},\"Service\":{ \"ID\":\"1e6c308793c1:dbnode3:4567\", \"Service\":\"dbnode\", \"Tags\":null, \"Address\":\"10.0.9.7\", \"Port\":4567},\"Checks\":[ {\"Node\":\"consul\",\"CheckID\":\"service:1e6c308793c1:dbnode3:4567\",\"Name\":\"Service 'dbnode' check\",\"Status\":\"critical\",\"Notes\":\"\",\"Output\":\"\",\"ServiceID\":\"1e6c308793c1:dbnode3:4567\",\"ServiceName\":\"dbnode\" }, {\"Node\":\"consul\",\"CheckID\":\"serfHealth\",\"Name\":\"Serf Health Status\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"Agent alive and reachable\",\"ServiceID\":\"\",\"ServiceName\":\"\" }] }, {\"Node\":{ \"Node\":\"consul\", \"Address\":\"172.17.0.2\"},\"Service\":{ \"ID\":\"b2b289fe8558:dbnode2:4567\", \"Service\":\"dbnode\", \"Tags\":null, \"Address\":\"10.0.9.5\", \"Port\":4567},\"Checks\":[ {\"Node\":\"consul\",\"CheckID\":\"service:b2b289fe8558:dbnode2:4567\",\"Name\":\"Service 'dbnode' check\",\"Status\":\"critical\",\"Notes\":\"\",\"Output\":\"TTL expired\",\"ServiceID\":\"b2b289fe8558:dbnode2:4567\",\"ServiceName\":\"dbnode\" }, {\"Node\":\"consul\",\"CheckID\":\"serfHealth\",\"Name\":\"Serf Health Status\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"Agent alive and reachable\",\"ServiceID\":\"\",\"ServiceName\":\"\" }] }, {\"Node\":{ \"Node\":\"consul\", \"Address\":\"172.17.0.2\"},\"Service\":{ \"ID\":\"1e6c308793c1:dbnode4:4567\", \"Service\":\"dbnode\", \"Tags\":null, \"Address\":\"10.0.9.8\", \"Port\":4567},\"Checks\":[ {\"Node\":\"consul\",\"CheckID\":\"service:1e6c308793c1:dbnode4:4567\",\"Name\":\"Service 'dbnode' check\",\"Status\":\"critical\",\"Notes\":\"\",\"Output\":\"\",\"ServiceID\":\"1e6c308793c1:dbnode4:4567\",\"ServiceName\":\"dbnode\" }, {\"Node\":\"consul\",\"CheckID\":\"serfHealth\",\"Name\":\"Serf Health Status\",\"Status\":\"passing\",\"Notes\":\"\",\"Output\":\"Agent alive and reachable\",\"ServiceID\":\"\",\"ServiceName\":\"\" }] }]";

	static Logger log = Logger.getLogger(HealthCheckParser.class.getName());
	
	public static String getCheckId(String consulIp) {
		
		try {
			str = new RESTRequestor("GET", "http://" + consulIp + "/v1/health/service/dbnode").request();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		log.info(str);
		
		ArrayList<HealthNode> data = parse();
		ArrayList<Check> checks = null;
		
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//ip = "10.0.9.8";
		
		for(HealthNode i : data) {
			
			if(i.getServiceAddress().equals(ip)) {
				checks = i.getChecks();
				break;
			}
		}
		
		for(Check i : checks) {
			if("dbnode".equals(i.getServiceName())) {
				return i.getCheckId();
			}
		}
		
		return "";
	}
	
	/**
	 * Get array of all healthy DB nodes
	 *
	 * @param consulIp IP address of Consul 
	 * @return array
	 */
	public static ArrayList<String> getHealthyNodes(String consulIp) {
		
		return getAccordingToHealth(consulIp, "passing");
	}
	
	/**
	 * Get array of all dead DB nodes
	 *
	 * @param consulIp IP address of Consul 
	 * @return array
	 */
	public static ArrayList<String> getDeadNodes(String consulIp) {
		
		return getAccordingToHealth(consulIp, "critical");
	}
	
	private static ArrayList<String> getAccordingToHealth(String consulIp, String status) {
		
		try {
			str = new RESTRequestor("GET", "http://" + consulIp + "/v1/health/service/dbnode").request();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ArrayList<HealthNode> data = parse();
		ArrayList<Check> checks = null;
		
		ArrayList<String> result = new ArrayList<String>();
		
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		for(HealthNode i : data) {
			
			if(i.getServiceAddress().equals(ip) == false) {
				
				checks = i.getChecks();
				
				for(Check j : checks) {
					
					if(status.equals(j.getStatus()) && "dbnode".equals(j.getServiceName())) {
						result.add((i.getServiceAddress() + ":" + i.getPort()));
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	private static ArrayList<HealthNode> parse() {
		
		ArrayList<HealthNode> result = new ArrayList<HealthNode>();
		
		JSONArray jsonArray = new JSONArray(str);
		
		JSONObject nodeObj;
		JSONObject serviceObj;
		JSONArray checksArray;
		JSONObject checkObj;
		
		for(int i=0; i<jsonArray.length(); i++) {
			
			nodeObj = jsonArray.getJSONObject(i).getJSONObject("Node");
			serviceObj = jsonArray.getJSONObject(i).getJSONObject("Service");
			
			checksArray = jsonArray.getJSONObject(i).getJSONArray("Checks");
			
			HealthNode node = new HealthNode();
			
			node.setNode((String) nodeObj.get("Node"));
			node.setNodeAddress((String) nodeObj.get("Address"));
			
			node.setId((String) serviceObj.get("ID"));
			node.setService((String) serviceObj.get("Service"));
			node.setServiceAddress((String) serviceObj.get("Address"));
			node.setPort((int) serviceObj.getInt("Port"));
			
			ArrayList<Check> checks = new ArrayList<Check>();
			
			for(int j=0; j<checksArray.length(); j++) {
				
				checkObj = checksArray.getJSONObject(j);
				
				Check check = new Check();
				
				check.setCheckId((String) checkObj.get("CheckID"));
				check.setStatus((String) checkObj.get("Status"));
				check.setServiceName((String) checkObj.get("ServiceName"));
				
				checks.add(check);
			}
			
			node.setChecks(checks);
				
			result.add(node);
		}
		
		return result;
	}
}
