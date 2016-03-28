package sk.fiit.dprs.dbnode;

import static spark.Spark.*;

import java.util.UUID;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.api.NodeAPIController;
import sk.fiit.dprs.dbnode.api.UserAPIController;

/**
 * Application entry point
 * Start API listening on port 4567
 * 
 * @author Jozef Zatko
 */
public class Main {
	
	static Logger log = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		port(4567);
		String id = UUID.randomUUID().toString();
		
		try {
			
			log.info("Starting dbnode on port 4567 with CONSUL_URL " + args[0]);
			
			new NodeAPIController(id, args[0]);
			new UserAPIController(id, args[0]);
		
		} catch(Exception e) {
			e.printStackTrace();
			
			new NodeAPIController(id, "");
			new UserAPIController(id, "");
		}
	}
}
