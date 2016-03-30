package sk.fiit.dprs.dbnode.healthcheck.models;

import java.util.ArrayList;

/**
 * Model of HealthNode of Consul /v1/health/service/dbnode response
 * 
 * @author Jozef Zatko
 */
public class HealthNode {

	private String node;
	private String nodeAddress;
	
	private String id;
	private String service;
	private String serviceAddress;
	private int port;
	
	private ArrayList<Check> checks;
	
	
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getNodeAddress() {
		return nodeAddress;
	}
	public void setNodeAddress(String nodeAddress) {
		this.nodeAddress = nodeAddress;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String address) {
		this.serviceAddress = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public ArrayList<Check> getChecks() {
		return checks;
	}
	public void setChecks(ArrayList<Check> checks) {
		this.checks = checks;
	}
}
