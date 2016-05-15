package sk.fiit.dprs.dbnode;

import static spark.Spark.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.api.NodeAPIController;
import sk.fiit.dprs.dbnode.api.UserAPIController;
import sk.fiit.dprs.dbnode.bootstraping.NodeInicializer;
import sk.fiit.dprs.dbnode.bootstraping.NodeRegistrator;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.healthcheck.HeartBeat;

/**
 * Application entry point
 * Start API listening on port 4567
 * 
 * @author Jozef Zatko
 */
public class Main {
	
	static Logger logger = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		port(4567);
		
		String id;
		try {
			id = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			
			id = UUID.randomUUID().toString();
			e1.printStackTrace();
		}
		
		try {
			logger.info("Starting dbnode on port 4567 with CONSUL_URL " + args[0]);
			
			String consulIpPort = args[0];
			String supportedNode;
			
			if (args.length > 1) {
				supportedNode = args[1];
			} else {
				supportedNode = null;
			}
			
			
			NodeTableService service = new NodeTableService(consulIpPort);
			
			new NodeInicializer(service, supportedNode).init();
			new NodeRegistrator(consulIpPort).register();
			
			new NodeAPIController(id, consulIpPort);
			new UserAPIController(id, consulIpPort);
			
			HeartBeat heartBeat = new HeartBeat(consulIpPort);
			new Thread(heartBeat).start();
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.fatal("Cannot initialize dbnode, exiting...");
			return;
		}
	}
	
}
