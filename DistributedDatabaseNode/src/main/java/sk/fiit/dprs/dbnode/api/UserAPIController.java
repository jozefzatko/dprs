package sk.fiit.dprs.dbnode.api;

import static spark.Spark.*;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * User-to-Node API setup and controlling
 * 
 * @author Jozef Zatko
 */
public class UserAPIController {

	static Logger log = Logger.getLogger(UserAPIController.class.getName());
	
	public UserAPIController(String id, String consulIpPort) {

		String logMessage ="[ ]";
		/* 
		 * READ from DB
		 * 
		 * GET http://localhost:4567/data/key123
		 * GET http://localhost:4567/data/key123?quorum=[3,2,2]
		 */
		get("/data/:key", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return UserAPIRequestProcessing.get(request.params(":key"), request.queryParams("quorum"));
		});

		/* 
		 * CREATE or UPDATE
		 * 
		 * POST http://localhost:4567/data/key123/value123
		 * POST http://localhost:4567/data/key123/value123?quorum=[3,2,2]&vclock=[1,2,3]
		 */
		post("/data/:key/:value", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return UserAPIRequestProcessing.createOrUpdate(request.params(":key"), request.params(":value"),
						request.queryParams("quorum"), request.queryParams("vclock"));
		});
		
		/*
		 * DELETE from DB
		 * 
		 * DELETE http://localhost:4567/data/key123
		 * DELETE http://localhost:4567/data/key123?quorum=[3,2,2]&vclock=[1,2,3]
		 */
		delete("/data/:key", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return UserAPIRequestProcessing.delete(request.params(":key"),request.queryParams("quorum"),
						request.queryParams("vclock"));
		});
	
		/*
		 * PING concrete database node through request of other node
		 * 
		 * GET http://localhost:4567/ping/localhost:4568
		 */
		get("/ping/:adress", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return UserAPIRequestProcessing.pingNode(request.params(":adress"));
		});
		
		/*
		 * PING all database nodes through request of other node
		 * 
		 * GET http://localhost:4567/pingall
		 */
		get("/pingall", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return UserAPIRequestProcessing.pingAllNodes(consulIpPort);
		});
		
		/*
		 * PING all healthy database nodes through request of other node
		 * 
		 * GET http://localhost:4567/pinghealthy
		 */
		get("/pinghealthy", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return UserAPIRequestProcessing.pingHealthyNodes(consulIpPort);
		});
		
		/*
		 * PING node directly from user request
		 * 
		 * GET http://localhost:4567/ping
		 */
		get("/ping", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return "Status: OK\n" + new Date().toString();
		});
		
		/*
		 * INFO of node
		 * 
		 * GET http://localhost:4567/info
		 */
		get("/info", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			return UserAPIRequestProcessing.getNodeInfo(id);
		});
		
		/*
		 * Exception handling
		 */
		exception(Exception.class, (e, request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+logMessage);
			log.info("CHYBA exception" + e.getMessage());
			e.printStackTrace();
			response.status(400);
			response.body(e.toString());
		});
		
		
		/*
		 * JSON response to request
		 */
		after((request, response) -> {
			response.type("application/json");
		});
	}
}
