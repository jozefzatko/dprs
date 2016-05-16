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

	static Logger log = Logger.getLogger(UserAPIController.class.getName());
	
	public NodeAPIController(String id, String consulURL, NodeTableService nodeTableService) {
		
		String logMessage ="[ ]";
		

		
		get("/control/registerreplica/:value", (request, response) -> {
			String ip1replica = request.ip();
			String replica = request.params(":value");
			int replicaNumber = Integer.parseInt(replica);
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+"[Node: " + ip1replica + "requested to register as "+ replicaNumber +" replica for node: "+id+"]");
			if(NodeAPIRequestProcessing.registerReplica(id, ip1replica, replicaNumber, nodeTableService)){
				log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+"[Node: " + ip1replica + " successfully registered as "+ replicaNumber +" replica for node: "+id+"]");
				return ("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+"[Node: " + ip1replica + " successfully registered as "+ replicaNumber +" replica for node: "+id+"]");
			}else{
				log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+"[Node: " + ip1replica + " failed to register as "+ replicaNumber +" replica for node: "+id+"]");
				return ("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " "+"[Node: " + ip1replica + " failed to register as "+ replicaNumber +" replica for node: "+id+"]");
			}
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
			
			return NodeAPIRequestProcessing.postDbNodeData(request.params(":replica"), request.queryParams("from"), request.queryParams("to"));
		});
		
		/* 
		 * PUT data to DB node
		 * 
		 * PUT http://localhost:4567/dbnode/1
		 * PUT http://localhost:4567/dbnode/1?from=12345&to=54321
		 */
		put("/dbnode/:replica", (request, response) -> {
			
			String requestID = request.headers("X-Request-Id");
			log.info("requestID: "+requestID+" method: "+request.requestMethod() + " " + request.url() + " " + logMessage);
			
			return NodeAPIRequestProcessing.putDbNodeData(request.params(":replica"), request.queryParams("from"), request.queryParams("to"));
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
			
			return NodeAPIRequestProcessing.deleteDbNodeData(request.params(":replica"), request.queryParams("from"), request.queryParams("to"));
		});
	}
}
