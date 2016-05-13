package sk.fiit.dprs.dbnode.bootstraping.models;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Model of CatalogEntry/Service of Consul /v1/catalog/register request
 * 
 * @author Jozef Zatko
 */
public class Service {
  
     private String ID;
     private String Service;
     private String Tags;
     private String Address;
     private int Port;
     
	public Service(String id) {

		this.ID = id;
		this.Service = "dbnode";
		this.Tags = null;
		
		try {
			this.Address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		this.Port = 4567;
	}
	
	public String getID() {
		return ID;
	}

	public void setID(String id) {
		ID = id;
	}

	public String getService() {
		return Service;
	}

	public void setService(String service) {
		Service = service;
	}

	public String getTags() {
		return Tags;
	}

	public void setTags(String tags) {
		Tags = tags;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		Port = port;
	}
	
}
