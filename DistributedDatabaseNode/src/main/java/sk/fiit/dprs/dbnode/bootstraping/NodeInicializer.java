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
	private String myIp;
	
	public NodeInicializer(NodeTableService service) throws UnknownHostException {
		
		this.service = service;
		this.myIp = InetAddress.getLocalHost().getHostAddress();
	}
	
	/**
	 * Start of initialization process of new DB node
	 */
	public void init() {
		
		service.initIfNeeded();
		
		switch (service.getCountofNodes()) {
		
			case 0: service.addFirstNode(myIp);
			break;
			
			case 1: service.addSecondNode(myIp);
			break;
			
			case 2: initAsThirdNode();
			break;
			
			default: initAsCasualNode();
			break;
		}
	}
	
	/**
	 * Write table record after third node is added
	 */
	private void initAsThirdNode() {
		
		service.addThirdNode(myIp);
		
		String third = myIp;
		String second = service.getPrevious(third);
		String first = service.getPrevious(second);
		
		service.updateNode(first,  null, null, second, third,  "ok");
		service.updateNode(second, null, null, third,  first,  "ok");
		service.updateNode(third,  null, null, first,  second, "ok");
	}
	
	/**
	 * Write table record after node is added
	 */
	private void initAsCasualNode() {
		
		// TODO: magic
	}
}
