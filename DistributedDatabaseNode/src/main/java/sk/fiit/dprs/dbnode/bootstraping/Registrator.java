package sk.fiit.dprs.dbnode.bootstraping;

import java.io.IOException;

import com.google.gson.Gson;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.bootstraping.models.CatalogEntry;
import sk.fiit.dprs.dbnode.bootstraping.models.Node;
import sk.fiit.dprs.dbnode.bootstraping.models.Service;

/**
 * Register this node in Consul service discovery
 * 
 * @author Jozef
 *
 */
public class Registrator {

	private String consulIp;
	
	public Registrator(String consulIp) {
		
		this.consulIp = consulIp;
	}
	
	/**
	 * Register this node in Consul via REST /v1/catalog/register request
	 */
	public void register(String id) {
		
		String registerAddress = "http://" + this.consulIp + "/v1/catalog/register";
		
		Node node = new Node(this.consulIp.split(":")[0]);
		Service service = new Service(id);
		
		String entry = new Gson().toJson(new CatalogEntry(node, service));
		
		try {
			new RESTRequestor("POST", registerAddress, entry).request();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
