package sk.fiit.dprs.dbnode.bootstraping;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.Main;
import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;

/**
 * Initialize this DB node before being registered
 * 
 * @author Jozef Zatko
 */
public class NodeInicializer {
	
	static Logger logger = Logger.getLogger(NodeInicializer.class.getName());

	private NodeTableService service;
	
	private String myIp;
	private String supportedNodeIp;
	
	public NodeInicializer(NodeTableService service, String supportedNodeIp) throws UnknownHostException {
		
		this.service = service;
		this.myIp = InetAddress.getLocalHost().getHostAddress();
		this.supportedNodeIp = supportedNodeIp;
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
		
		if (this.supportedNodeIp == null) {
			logger.info("Node was started without IP of the supported Node! Exitting program!");
			System.exit(-1);
		}
		logger.info("Node was started with IP of the supported Node: "+supportedNodeIp);
		String nextNode1 = service.getNext(supportedNodeIp);
		String nextNode2 = service.getNext(nextNode1);
		new RESTRequestor("GET", "http://"+nextNode1+":4567/control/registerreplica/1");
		new RESTRequestor("GET", "http://"+nextNode1+":4567/control/registerreplica/2");
		logger.info("Data should be copied from nodes where i am acting as replica");
		// TODO: magic
	}
}
