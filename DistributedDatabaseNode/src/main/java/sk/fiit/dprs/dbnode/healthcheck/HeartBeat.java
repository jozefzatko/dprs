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
	
	private String consulIp;
	private String checkId;
	private RESTRequestor healthRequest;
	
	public HeartBeat(String consulIp) {
		
		this.consulIp = consulIp;
	}
	
	public void run() {
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.checkId = HealthCheckParser.getCheckId(this.consulIp);
		log.info("Found out CheckID: " + this.checkId);
		
		log.info("GET http://" + this.consulIp + "/v1/agent/check/pass/" + this.checkId);
		this.healthRequest = new RESTRequestor("GET", "http://" + this.consulIp + "/v1/agent/check/pass/" + this.checkId);
		
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
