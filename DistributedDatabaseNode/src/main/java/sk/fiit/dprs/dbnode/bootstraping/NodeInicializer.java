package sk.fiit.dprs.dbnode.bootstraping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.Main;
import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableRecord;
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
		
		initializePrevious();
		initializeNext();
	}
	
	private void initializePrevious() {
	
		logger.info("Node was started with IP of the supported Node: "+supportedNodeIp);
		String nextNode1 = service.getNext(supportedNodeIp);
		String nextNode2 = service.getNext(nextNode1);
		try {
			new RESTRequestor("GET", "http://"+nextNode1+":4567/control/registerreplica/1").request();
			new RESTRequestor("GET", "http://"+nextNode2+":4567/control/registerreplica/2").request();
		} catch (IOException e) {
			logger.info("FAILED TO REGISTER AS REPLICA");
		}		
		logger.info("Data should be copied from nodes where i am acting as replica");
	}
		
	private void initializeNext() {
		
		String data;
		
		try {
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/3").request();
			data = new RESTRequestor("GET", "http://" + supportedNodeIp + ":4567/dbnode/2").request();
			new RESTRequestor("POST", "http://" + supportedNodeIp + ":4567/dbnode/3", data).request();
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/2").request();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		NodeTableRecord record = service.getRecord(supportedNodeIp);
		
		long from = record.getHashFrom();
		long to = record.getHashTo();
		
		long from1 = from;
		long to1 = from + (to - from) / 2;
		long from2 = to1 + 1;
		long to2 = to;
		
		try {
			String secondHalf = new RESTRequestor("GET", "http://" + supportedNodeIp + ":4567/dbnode/2?from=" + from2 + "&to=" + to2).request();
			new RESTRequestor("POST", "http://" + supportedNodeIp + ":4567/dbnode/2", secondHalf).request();
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/1?from=" + from2 + "&to=" + to2).request();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		service.updateNode(supportedNodeIp, from1, to1, null, null, null);
	}
}
