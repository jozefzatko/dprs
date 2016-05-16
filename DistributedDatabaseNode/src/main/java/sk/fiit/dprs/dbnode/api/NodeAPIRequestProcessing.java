package sk.fiit.dprs.dbnode.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableRecord;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.models.DataNode;
import sk.fiit.dprs.dbnode.db.models.Database;

public class NodeAPIRequestProcessing {

	static Logger log = Logger.getLogger(NodeAPIRequestProcessing.class.getName());
	
	public static String getDbNodeData() {
		
		StringBuilder response = new StringBuilder();
		
		try {
			response.append(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		response.append("\n\n");
		response.append(Database.getinstance().getMyData().getData().toString());
		response.append("\n");
		response.append(Database.getinstance().getFirstReplica().getData().toString());
		response.append("\n");
		response.append(Database.getinstance().getSecondReplica().getData().toString());
		
		return response.toString();
	}
	
	public static String getDbNodeData(String replica, String hashFrom, String hashTo) {
		
		DataNode data = getData(replica);
		
		if (hashFrom == null || hashTo == null) {
			
			return data.getData().toString();
		}

		long from = new Long(hashFrom);
		long to = new Long(hashTo);
						
		return data.toString(from, to);
	}
	
	public static String postDbNodeData(String replica, String dataToPost, NodeTableService service) {
		
		getData(replica).seed(dataToPost);
		if(Integer.parseInt(replica) == 1){
			try {
				String myIp = InetAddress.getLocalHost().getHostAddress();
				NodeTableRecord record = service.getRecord(myIp);
				
				new RESTRequestor("POST", "http://" + record.getFirstReplicaId() + ":4567/dbnode/2", dataToPost).request();
				new RESTRequestor("POST", "http://" + record.getSecondReplicaId() + ":4567/dbnode/3", dataToPost).request();
			} catch (IOException e) {
				log.info("FAILED TO REPLICATE DATA");
				e.printStackTrace();
			}
		}
		return "ack";
	}

	public static String deleteDbNodeData(String replica, String hashFrom, String hashTo) {
		
		DataNode data = getData(replica);
		
		if (hashFrom == null || hashTo == null) {
			
			data.getData().clear();
			return "ack";
		}
		
		long from = new Long(hashFrom);
		long to = new Long(hashTo);
		
		data.remove(from, to);
		
		return "ack";
	}
	
	
	private static DataNode getData(String replica) {
		
		DataNode data; 
		
		if ("1".equals(replica)) {
			data = Database.getinstance().getMyData();
		}
		else if ("2".equals(replica)) {
			data = Database.getinstance().getFirstReplica();
		}
		else {
			data = Database.getinstance().getSecondReplica();
		}
		
		return data;
	}

}
