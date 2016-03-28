package sk.fiit.dprs.dbnode.api;

import static spark.Spark.*;

import java.util.Date;

/**
 * User-to-Node API setup and controlling
 * 
 * @author Jozef Zatko
 */
public class UserAPIController {

	public UserAPIController(String id, String consulURL) {

		/*
		 * READ from DB
		 * 
		 * GET http://localhost:4567/data/key123
		 * GET http://localhost:4567/data/key123?quorum=[3,2,2]
		 */
		get("/data/:key", (request, response) -> {
			
			return UserAPIRequestProcessing.get(request.params(":key"), request.queryParams("quorum"));
		});

		/*
		 * CREATE or UPDATE
		 * 
		 * POST http://localhost:4567/data/key123/value123
		 * POST http://localhost:4567/data/key123/value123?quorum=[3,2,2]&vclock=[1,2,3]
		 */
		post("/data/:key/:value", (request, response) -> {
			
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
			
			return UserAPIRequestProcessing.delete(request.params(":key"),request.queryParams("quorum"),
						request.queryParams("vclock"));
		});
	
		/*
		 * PING concrete database node through request of other node
		 * 
		 * GET http://localhost:4567/ping/localhost:4568
		 */
		get("/ping/:adress", (request, response) -> {
			
			return UserAPIRequestProcessing.pingNode(request.params(":adress"));
		});
		
		/*
		 * PING all database nodes through request of other node
		 * 
		 * GET http://localhost:4567/ping
		 */
		get("/pingall", (request, response) -> {
			
			return UserAPIRequestProcessing.pingAllNodes(consulURL);
		});
		
		/*
		 * PING node directly from user request
		 * 
		 * GET http://localhost:4567/ping
		 */
		get("/ping", (request, response) -> {
						
			return "Status: OK\n" + new Date().toString();
		});
		
		/*
		 * INFO of node
		 * 
		 * GET http://localhost:4567/info
		 */
		get("/info", (request, response) -> {
						
			return UserAPIRequestProcessing.getNodeInfo(id);
		});
		
		/*
		 * Exception handling
		 */
		exception(Exception.class, (e, request, response) -> {
			
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
