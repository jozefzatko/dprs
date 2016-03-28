package sk.fiit.dprs.dbnode;

import static spark.Spark.*;

import java.util.UUID;

import sk.fiit.dprs.dbnode.api.NodeAPIController;
import sk.fiit.dprs.dbnode.api.UserAPIController;

/**
 * Application entry point
 * Start API listening on port 4567
 * 
 * @author Jozef Zatko
 */
public class Main {
	
	public static void main(String[] args) {
		
		port(4568);
		
		String id = UUID.randomUUID().toString();
		
		new NodeAPIController(id);
		new UserAPIController(id);
	}
}
