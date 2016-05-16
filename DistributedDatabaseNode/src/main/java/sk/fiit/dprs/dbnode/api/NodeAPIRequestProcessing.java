package sk.fiit.dprs.dbnode.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

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
	
	public static String postDbNodeData(String replica, String dataToPost) {
		
		getData(replica).seed(dataToPost);
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
