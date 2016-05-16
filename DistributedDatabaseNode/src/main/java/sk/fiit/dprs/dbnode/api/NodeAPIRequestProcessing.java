package sk.fiit.dprs.dbnode.api;

import java.io.IOException;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.models.DataNode;
import sk.fiit.dprs.dbnode.db.models.Database;

public class NodeAPIRequestProcessing {

	
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
	
	public static boolean registerReplica(String id, String adress, int replicaNumber, NodeTableService service) {
		
		try {
			new RESTRequestor("POST", "http://" + adress + ":4567/dbnode/"+(replicaNumber+1), Database.getinstance().getMyData().getData().toString()).request();
		} catch (IOException e) {
			return false;
		}
		
		if (replicaNumber == 1) {
			service.updateNode(id, null, null, String.valueOf(replicaNumber), null, null);
		} else {
			if (replicaNumber == 2) {
				service.updateNode(id, null, null, null, String.valueOf(replicaNumber), null);
			}
		}
		
		return true;
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
