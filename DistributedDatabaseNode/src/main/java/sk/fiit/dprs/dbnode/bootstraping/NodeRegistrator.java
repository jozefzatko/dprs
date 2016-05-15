package sk.fiit.dprs.dbnode.bootstraping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.bootstraping.models.AgentEntry;
import sk.fiit.dprs.dbnode.bootstraping.models.Check;

/**
 * Register this node in Consul service discovery
 * 
 * @author Jozef Zatko
 *
 */
public class NodeRegistrator {

	private String consulIpPort;
	
	public NodeRegistrator(String consulIpPort) {
		
		this.consulIpPort = consulIpPort;
	}
	
	/**
	 * Register this node in Consul via REST /v1/catalog/register request
	 */
	public void register() {
		
		String registerAddress = "http://" + this.consulIpPort + "/v1/agent/service/register";
		String myIP = "";
		try {
			myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		Check check = new Check();
		String entry = new Gson().toJson(new AgentEntry(myIP, check));
		
		try {
			new RESTRequestor("PUT", registerAddress, entry).request();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
