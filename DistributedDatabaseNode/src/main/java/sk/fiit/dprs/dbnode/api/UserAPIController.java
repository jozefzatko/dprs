package sk.fiit.dprs.dbnode.api;

import static spark.Spark.*;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * User-to-Node API setup and controlling
 * 
 * @author Jozef Zatko
 */
public class UserAPIController {

	static Logger log = Logger.getLogger(UserAPIController.class.getName());
	
	public UserAPIController(String id, String consulIpPort) {

		/*
		 * READ from DB
		 * 
		 * GET http://localhost:4567/data/key123
		 * GET http://localhost:4567/data/key123?quorum=[3,2,2]
		 */
		get("/data/:key", (request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
			return UserAPIRequestProcessing.get(request.params(":key"), request.queryParams("quorum"));
		});

		/*
		 * CREATE or UPDATE
		 * 
		 * POST http://localhost:4567/data/key123/value123
		 * POST http://localhost:4567/data/key123/value123?quorum=[3,2,2]&vclock=[1,2,3]
		 */
		post("/data/:key/:value", (request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
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
			
			log.info(request.requestMethod() + " " + request.url());
			return UserAPIRequestProcessing.delete(request.params(":key"),request.queryParams("quorum"),
						request.queryParams("vclock"));
		});
	
		/*
		 * PING concrete database node through request of other node
		 * 
		 * GET http://localhost:4567/ping/localhost:4568
		 */
		get("/ping/:adress", (request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
			return UserAPIRequestProcessing.pingNode(request.params(":adress"));
		});
		
		/*
		 * PING all database nodes through request of other node
		 * 
		 * GET http://localhost:4567/pingall
		 */
		get("/pingall", (request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
			return UserAPIRequestProcessing.pingAllNodes(consulIpPort);
		});
		
		/*
		 * PING all healthy database nodes through request of other node
		 * 
		 * GET http://localhost:4567/pinghealthy
		 */
		get("/pinghealthy", (request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
			return UserAPIRequestProcessing.pingHealthyNodes(consulIpPort);
		});
		
		/*
		 * PING node directly from user request
		 * 
		 * GET http://localhost:4567/ping
		 */
		get("/ping", (request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
			return "Status: OK\n" + new Date().toString();
		});
		
		/*
		 * INFO of node
		 * 
		 * GET http://localhost:4567/info
		 */
		get("/info", (request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
			return UserAPIRequestProcessing.getNodeInfo(id);
		});
		
		/*
		 * Exception handling
		 */
		exception(Exception.class, (e, request, response) -> {
			
			log.info(request.requestMethod() + " " + request.url());
			
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
