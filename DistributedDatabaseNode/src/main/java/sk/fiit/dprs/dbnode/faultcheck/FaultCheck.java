package sk.fiit.dprs.dbnode.faultcheck;

import java.io.IOException;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableRecord;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.models.Database;
import sk.fiit.dprs.dbnode.healthcheck.HealthCheckParser;
import sk.fiit.dprs.dbnode.healthcheck.HeartBeat;

public class FaultCheck implements Runnable{
static Logger log = Logger.getLogger(HeartBeat.class.getName());
	
	private String myIp;
	private int failCounter = 0;
	private NodeTableService service;
	
	public FaultCheck(String myIp, NodeTableService service) {
		this.service = service;
		this.myIp = myIp;
	}
	
	public void run() {
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
		while(true) {
			String replicatedNode = "";
			if(service.getCountofNodes()>3){
				try {
					
					replicatedNode = service.getNext(myIp);
					log.info("Found out ID of next node for fail check: " + replicatedNode);
					
					new RESTRequestor("GET", "http://" +replicatedNode+ "/ping").request();
					
					failCounter = 0;
				} catch (IOException e1) {
					failCounter++;
				}
				if(failCounter >= 3){
					if(!replicatedNode.equals("")){
						try {
						NodeTableRecord record = service.getRecord(replicatedNode);
						NodeTableRecord myRecord = service.getRecord(myIp);
						String nextNodeIp = service.getNext(replicatedNode); // + 1
						String nextNodeIp2 = service.getNext(nextNodeIp); // + 2
						
						String data1 = new RESTRequestor("GET", "http://"+nextNodeIp+":4567/dbnode/1").request();
						String data2 = new RESTRequestor("GET", "http://"+nextNodeIp2+":4567/dbnode/1").request();
											
						Database.getinstance().getMyData().seed(Database.getinstance().getFirstReplica().getData().toString());
						Database.getinstance().setMyDataHashTo(record.getHashTo());
						new RESTRequestor("DELETE", "http://"+myRecord.getFirstReplicaId()+":4567/dbnode/3");
						new RESTRequestor("POST", "http://"+myRecord.getFirstReplicaId()+":4567/dbnode/3", data1);
						new RESTRequestor("POST", "http://"+myRecord.getFirstReplicaId()+":4567/dbnode/2", Database.getinstance().getFirstReplica().getData().toString());
						new RESTRequestor("POST", "http://"+myRecord.getSecondReplicaId()+":4567/dbnode/3", Database.getinstance().getFirstReplica().getData().toString());
						Database.getinstance().getFirstReplica().clear();
						Database.getinstance().getFirstReplica().seed(data1);
						Database.getinstance().getSecondReplica().clear();
						Database.getinstance().getSecondReplica().seed(data2);
						service.updateNode(nextNodeIp2, null, null, null, myIp, null);
						service.updateNode(nextNodeIp, null, null, myIp, null, null);
						service.updateNode(myIp, null, Database.getinstance().getMyDataHashTo(), null, null, null);
						service.deleteNode(replicatedNode);
						failCounter = 0;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else{
					try {
						if(failCounter==0){
							Thread.sleep(8000);
						}else{
							Thread.sleep(1000);
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}
			
			try {
					Thread.sleep(8000);
					
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			

		}
	}

}
