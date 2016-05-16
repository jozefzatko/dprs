package sk.fiit.dprs.dbnode.bootstraping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.crypto.Data;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.Main;
import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableRecord;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.models.Database;

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
		
		service.updateNode(first,  null, null, third, second,  "ok");
		service.updateNode(second, null, null, first,  third,  "ok");
		service.updateNode(third,  null, null, second,  first, "ok");
	}
	
	/**
	 * Write table record after node is added
	 */
	private void initAsCasualNode() {
		
		if (this.supportedNodeIp == null) {
			logger.info("Node was started without IP of the supported Node! Exitting program!");
			System.exit(-1);
		}
		service.addNextNode(myIp, -1, -1, "", "");
		initializeNext();
		initializePrevious();
		
	}
	
	private void initializeNext() {
	
		logger.info("Node was started with IP of the supported Node: "+supportedNodeIp);
		String nextNode1 = service.getNext(supportedNodeIp);
		String nextNode2 = service.getNext(nextNode1);
		try {
			logger.info("INITIALIZING: "+myIp+" requesting data from "+nextNode1+" to act as 1st replica.");
			String data = new RESTRequestor("GET", "http://"+nextNode1+":4567/dbnode/1").request();
			Database.getinstance().getFirstReplica().clear();
			Database.getinstance().getFirstReplica().seed(data);
			logger.info("INITIALIZING: Updating node table to change 1st replica of node "+nextNode1+" to "+myIp);
			service.updateNode(nextNode1, null, null, myIp, null, null);
			logger.info("INITIALIZING: "+myIp+" Successfully initialized itself to act as 1st replica for node "+nextNode1);
			logger.info("INITIALIZING: "+myIp+" requesting data from "+nextNode2+" to act as 2nd replica.");
			data = new RESTRequestor("GET", "http://"+nextNode2+":4567/dbnode/1").request();
			Database.getinstance().getSecondReplica().clear();
			Database.getinstance().getSecondReplica().seed(data);
			logger.info("INITIALIZING: Updating node table to change 2nd replica of node "+nextNode2+" to "+myIp);
			service.updateNode(nextNode2, null, null, null, myIp, null);
			logger.info("INITIALIZING: "+myIp+" Successfully initialized itself to act as 2nd replica for node "+nextNode2);
		} catch (IOException e) {
			logger.info(e.getMessage());
			logger.info("FAILED TO REGISTER AS REPLICA for 2 next nodes");
		}		
		
		logger.info("Data should be copied from nodes where i am acting as replica");
	}
	
	
	private void initializePrevious() {
		
		NodeTableRecord record = service.getRecord(supportedNodeIp);
		
		long from = record.getHashFrom();
		long to = record.getHashTo();
		
		long from1 = from;
		long to1 = from + (to - from) / 2;
		long from2 = to1 + 1;
		long to2 = to;
		
		
		String data;
		
		try {
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/3").request();
			
			data = new RESTRequestor("GET", "http://" + supportedNodeIp + ":4567/dbnode/2").request();
			new RESTRequestor("POST", "http://" + supportedNodeIp + ":4567/dbnode/3", data).request();
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/2").request();
			
			String secondHalf = new RESTRequestor("GET", "http://" + supportedNodeIp + ":4567/dbnode/1?from=" + from2 + "&to=" + to2).request();
			new RESTRequestor("POST", "http://" + supportedNodeIp + ":4567/dbnode/2", secondHalf).request();
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/1?from=" + from2 + "&to=" + to2).request();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		service.updateNode(supportedNodeIp, from1, to1, null, null, null);
		
		
		String previousNodeIp = service.getPrevious(supportedNodeIp);
		try {
			new RESTRequestor("DELETE", "http://" + previousNodeIp + ":4567/dbnode/3").request();
			String secondHalf = new RESTRequestor("GET", "http://" + supportedNodeIp + ":4567/dbnode/2?from=" + from2 + "&to=" + to2).request();
			new RESTRequestor("POST", "http://" + supportedNodeIp + ":4567/dbnode/3", secondHalf).request();
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/2?from=" + from2 + "&to=" + to2).request();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		previousNodeIp = service.getPrevious(previousNodeIp);
		try {
			new RESTRequestor("DELETE", "http://" + supportedNodeIp + ":4567/dbnode/3?from=" + from2 + "&to=" + to2).request();
		} catch (IOException e) {
			e.printStackTrace();
		}
		service.updateNode(myIp, Database.getinstance().getMyDataHashFrom(), Database.getinstance().getMyDataHashTo(), supportedNodeIp, previousNodeIp, "ok");
	}
}
