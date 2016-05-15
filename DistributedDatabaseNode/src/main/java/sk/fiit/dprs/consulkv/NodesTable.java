package sk.fiit.dprs.consulkv;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;

public class NodesTable {
	
	static Logger log = Logger.getLogger(NodesTable.class.getName());
	private String consulURL;
	
	public NodesTable(String consulIP){
		this.consulURL = consulIP;
		NodeTableRecord ntr1 = new NodeTableRecord("10.0.9.2", 0, 255, "10.0.9.3", "10.0.9.4");
		NodeTableRecord ntr2 = new NodeTableRecord("10.0.9.3", 256, 511, "10.0.9.4", "10.0.9.2");
		NodeTableRecord ntr3 = new NodeTableRecord("10.0.9.4", 512, 767, "10.0.9.2", "10.0.9.3");
		ArrayList<NodeTableRecord> list = new ArrayList<>();
		list.add(ntr1);
		list.add(ntr2);
		list.add(ntr3);
		try {
			setNodeTable(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			getNodeTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<NodeTableRecord> getNodeTable() throws Exception{
		log.info("Trying to get node table from consul.");
		String table = new RESTRequestor("GET", "http://" + consulURL + "/v1/kv/nodetable").request();
		log.info("Node table returned this result: " + table);
		
		Gson googleJson = new Gson();
		ArrayList<NodeTableRecord> tabulka = (ArrayList<NodeTableRecord>) googleJson.fromJson(table, ArrayList.class);

		
		return tabulka;
	}
	
	public void setNodeTable(Object o) throws Exception{
		log.info("Trying to put node table to consul");
		new RESTRequestor("PUT", "http://" + consulURL + "/v1/kv/nodetable", new Gson().toJson(o).toString()).request();
	}
	
}
