package sk.fiit.dprs.dbnode.consulkv;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;

/**
 * Represent table of records about db nodes 
 * 
 * @author Richard Pastorek
 * @author Jozef Zatko
 */
public class NodesTable {
	
	private Logger log = Logger.getLogger(NodesTable.class.getName());
	
	private String consulURL;
	private ArrayList<NodeTableRecord> table;
	
	public NodesTable(String consulIP){
		
		this.consulURL = consulIP;
		this.table = new ArrayList<NodeTableRecord>();
	}
	
	/**
	 * Load node table from Consul
	 * 
	 * @return array list of nodes
	 * @throws Exception
	 */
	public void loadNodeTable() throws Exception {
		
		log.info("Trying to get node table from consul.");
		String table = new RESTRequestor("GET", "http://" + consulURL + "/v1/kv/nodetable").request();
				
		// Load table in the form of JSON + decoding from Base64
		JsonParser jsonParser = new JsonParser();
        JsonArray jsonArr = (JsonArray)jsonParser.parse(table);
		JsonObject obj = (JsonObject)jsonArr.get(0);
		JsonElement elem = obj.get("Value");
		byte[] valueDecoded = Base64.getDecoder().decode(elem.toString().substring(1, elem.toString().length()-1).getBytes());

		// From JSON to ArrayList
		Type listType = new TypeToken<ArrayList<NodeTableRecord>>(){}.getType();
		ArrayList<NodeTableRecord> list = new Gson().fromJson(new String(valueDecoded, "UTF-8"), listType);
		
		this.table = list;
	}
	
	/**
	 * Send node table to Consul
	 * 
	 * @param table array list of nodes 
	 * @throws Exception
	 */
	public void writeNodeTable() throws Exception {
		
		log.info("Trying to put node table to consul");

		String arrayListToJson = new GsonBuilder().create().toJson(this.table);
		
		new RESTRequestor("PUT", "http://" + consulURL + "/v1/kv/nodetable", arrayListToJson).request();
	}

	public ArrayList<NodeTableRecord> getTable() {
		return table;
	}
	
}
