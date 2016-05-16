package sk.fiit.dprs.dbnode.api;

import java.io.IOException;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.models.Database;

public class NodeAPIRequestProcessing {

	
	public static String getDbNodeData(String replica, String hashFrom, String hashTo) {
		
		return null;
	}
	
	public static String postDbNodeData(String replica, String hashFrom, String hashTo) {
		
		return null;
	}

	public static String putDbNodeData(String replica, String hashFrom, String hashTo) {
		
		return null;
	}

	public static String deleteDbNodeData(String replica, String hashFrom, String hashTo) {
		
		return null;
	}
	
	public static boolean registerReplica(String id, String adress, int replicaNumber, NodeTableService service){
		try {
			new RESTRequestor("PUT", "http://" + adress + ":4567/dbnode/"+(replicaNumber+1), Database.getinstance().getMyData().getData().toString()).request();
		} catch (IOException e) {
			return false;
		}
		if(replicaNumber == 1){
			service.updateNode(id, null, null, String.valueOf(replicaNumber), null, null);
		}else{
			if(replicaNumber == 2){
				service.updateNode(id, null, null, null, String.valueOf(replicaNumber), null);
			}
		}
		
		return true;
	}

}
