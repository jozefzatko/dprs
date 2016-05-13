package sk.fiit.dprs.dbnode.bootstraping.models;

/**
 * Model of CatalogEntry of Consul /v1/catalog/register request
 * 
 * @author Jozef Zatko
 */
public class CatalogEntry {

	private Node Node;
	private Service Service;
	
	public CatalogEntry(Node node, Service service) {
		
		this.Node = node;
		this.Service = service;
	}
	
	public Node getNode() {
		return Node;
	}
	public void setNode(Node node) {
		Node = node;
	}
	public Service getService() {
		return Service;
	}
	public void setService(Service service) {
		Service = service;
	}
	
}
