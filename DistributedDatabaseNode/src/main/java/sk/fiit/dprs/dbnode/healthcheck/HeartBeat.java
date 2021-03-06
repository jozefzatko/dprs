package sk.fiit.dprs.dbnode.healthcheck;

import java.io.IOException;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;

/**
 * Holder of hearthbeat for Consul
 * 
 * @author Jozef Zatko
 */
public class HeartBeat implements Runnable {

	static Logger log = Logger.getLogger(HeartBeat.class.getName());
	
	private String consulIpPort;
	private String checkId;
	private RESTRequestor healthRequest;
	
	public HeartBeat(String consulIpPort) {
		
		this.consulIpPort = consulIpPort;
	}
	
	public void run() {
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.checkId = HealthCheckParser.getCheckId(this.consulIpPort);
		log.info("Found out CheckID: " + this.checkId);
		
		log.info("GET http://" + this.consulIpPort + "/v1/agent/check/pass/" + this.checkId);
		this.healthRequest = new RESTRequestor("GET", "http://" + this.consulIpPort + "/v1/agent/check/pass/" + this.checkId);
		
		while(true) {
			
			try {
				this.healthRequest.request();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
}
