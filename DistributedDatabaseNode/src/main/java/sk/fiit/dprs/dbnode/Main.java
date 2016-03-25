package sk.fiit.dprs.dbnode;

import static spark.Spark.*;

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
		
		port(4567);
		
		new NodeAPIController();
		new UserAPIController();
	}
}
