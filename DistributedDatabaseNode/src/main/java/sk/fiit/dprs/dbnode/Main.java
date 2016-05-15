package sk.fiit.dprs.dbnode;

import static spark.Spark.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.api.NodeAPIController;
import sk.fiit.dprs.dbnode.api.UserAPIController;
import sk.fiit.dprs.dbnode.bootstraping.Registrator;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.consulkv.NodesTable;
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
			
			new Registrator(args[0]).register(id);
			
			new NodeAPIController(id, args[0]);
			new UserAPIController(id, args[0]);

			//NodeTableService service = new NodeTableService(args[0]);
			
			HeartBeat heartBeat = new HeartBeat(args[0]);
			new Thread(heartBeat).start();
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.fatal("Cannot initialize dbnode, exiting...");
			return;
		}
	}
	
}
