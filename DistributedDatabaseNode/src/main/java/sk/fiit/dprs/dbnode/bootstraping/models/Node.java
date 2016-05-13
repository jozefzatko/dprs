package sk.fiit.dprs.dbnode.bootstraping.models;

/**
 * Model of CatalogEntry/Node of Consul /v1/catalog/register request
 * 
 * @author Jozef Zatko
 */
public class Node {

	private String Node;
	private String Address;
	
	public Node(String address) {
		
		this.Node = "consul";
		this.Address = address;
	}

	public String getNode() {
		return Node;
	}

	public void setNode(String node) {
		Node = node;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}
	
}
