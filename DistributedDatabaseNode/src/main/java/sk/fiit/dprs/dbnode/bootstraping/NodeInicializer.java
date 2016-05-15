package sk.fiit.dprs.dbnode.bootstraping;

import java.net.InetAddress;
import java.net.UnknownHostException;

import sk.fiit.dprs.dbnode.consulkv.NodeTableService;

/**
 * Initialize this DB node before being registered
 * 
 * @author Jozef Zatko
 */
public class NodeInicializer {

	private NodeTableService service;
	private String id;
	private String consulIpPort;
	
	private String myIp;
	
	public NodeInicializer(NodeTableService service, String id, String consulIpPort) throws UnknownHostException {
		
		this.service = service;
		this.id = id;
		this.consulIpPort = consulIpPort;
		
		this.myIp = InetAddress.getLocalHost().getHostAddress();
	}
	
	/**
	 * Start of initialization process of new DB node
	 */
	public void init() {
		
		service.initIfNeeded();
		
		switch (service.getCountofNodes()) {
		
			case 1:  service.addFirstNode(myIp);
			break;
			
			case 2:  initAsSecondNode();
			break;
			
			case 3:  initAsThirdNode();
			break;
			
			default:  initAsCasualNode();
			break;
		}
	}
	
	
	private void initAsSecondNode() {
		
		service.addSecondNode(myIp);
		
		// TODO: presunutie polovice dat z 1 na 2
	}
	
	private void initAsThirdNode() {
		
		// TODO: pridanie 3. uzla
		// TODO: uprava 1. a 2. uzla
		// TODO: replikacia dat
		// TODO: nastavenie stavu na ok
	}
	
	private void initAsCasualNode() {
		
		// TODO: magic
	}
}
