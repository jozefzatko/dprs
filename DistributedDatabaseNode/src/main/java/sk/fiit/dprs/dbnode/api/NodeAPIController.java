package sk.fiit.dprs.dbnode.api;

import static spark.Spark.*;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.consulkv.NodeTableService;

/**
 * Node-to-Node API setup and controlling
 * 
 * @author Jozef Zatko
 */
public class NodeAPIController {

	static Logger log = Logger.getLogger(NodeAPIController.class.getName());
	
	public NodeAPIController(String id, String consulURL, NodeTableService nodeTableService) {
		
		String logMessage ="[ ]";
		
		/* 
		 * READ all data from DB node
		 * 
		 * GET http://localhost:4567/dbnode
		 */
		get("/dbnode", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " " + logMessage);
			
			return NodeAPIRequestProcessing.getDbNodeData();
		});
		
		/* 
		 * READ data from DB node
		 * 
		 * GET http://localhost:4567/dbnode/1
		 * GET http://localhost:4567/dbnode/1?from=12345&to=54321
		 */
		get("/dbnode/:replica", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " " + logMessage);
			
			return NodeAPIRequestProcessing.getDbNodeData(request.params(":replica"), request.queryParams("from"), request.queryParams("to"));
		});
		
		/* 
		 * WRITE data to DB node
		 * 
		 * POST http://localhost:4567/dbnode/1
		 * POST http://localhost:4567/dbnode/1?from=12345&to=54321
		 */
		post("/dbnode/:replica", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " " + logMessage);
			
			return NodeAPIRequestProcessing.postDbNodeData(request.params(":replica"), request.body(), nodeTableService);
		});
		
		/* 
		 * DELETE data from DB node
		 * 
		 * DELETE http://localhost:4567/dbnode/1
		 * DELETE http://localhost:4567/dbnode/1?from=12345&to=54321
		 */
		delete("/dbnode/:replica", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " " + logMessage);
			
			return NodeAPIRequestProcessing.deleteDbNodeData(request.params(":replica"), request.queryParams("from"), request.queryParams("to"), nodeTableService);
		});
	}
}
