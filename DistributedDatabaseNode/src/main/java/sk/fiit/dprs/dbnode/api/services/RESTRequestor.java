package sk.fiit.dprs.dbnode.api.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Handle a REST request
 * 
 * @author Jozef Zatko
 */
public class RESTRequestor {

	private static Logger logger = Logger.getLogger(RESTRequestor.class.getName());
	
	private String requestMethod;
	private String requestUrl;
	private String params = "";
	
	public RESTRequestor(String requestMethod, String requestUrl) {
		
		this.requestMethod = requestMethod;
		this.requestUrl = requestUrl;
	}
	
	public RESTRequestor(String requestMethod, String requestUrl, String params) {
		
		this.requestMethod = requestMethod;
		this.requestUrl = requestUrl;
		this.params = params;
	}
	
	/**
	 * Make a REST request according to request method and request URL
	 * 
	 * @return response message
	 * @throws IOException
	 */
	public String request() throws IOException {
		
		URL url = new URL(this.requestUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(this.requestMethod);
		conn.setRequestProperty("Accept", "application/json");
		conn.setConnectTimeout(5000);
			
		logger.info("REST: " + this.requestMethod + " " + this.requestUrl + " " + this.params);
		
		if ("PUT".equals(this.requestMethod) || "POST".equals(this.requestMethod)) {
			
			if ("{}".equals(params)) {
				
				params = "";
			}
			
			if(conn  == null){
				logger.info("CONN JE NULL A RISO MAL PRAVDU");
			}
			logger.info("VBEHOL SOM DO IFU");
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			if(wr  == null){
				logger.info("WR JE NULL A RISO MAL PRAVDU");
			}
			logger.info("SOM V STREDE IFU");
			wr.writeBytes(this.params);
			wr.flush();
			wr.close();
			logger.info("PREBEHOL SOM IF");
		}
		
		if (conn.getResponseCode() != 200) {
			logger.error(conn.getResponseMessage());
			throw new RuntimeException("Cannot initialize connection to " + this.requestUrl + " : " + conn.getResponseCode());
		}
			
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
		String responseMsg = "";
		String output;
		while ((output = br.readLine()) != null) {
			responseMsg += output;
		}
			
		conn.disconnect();
		
		return responseMsg;
	}
}
