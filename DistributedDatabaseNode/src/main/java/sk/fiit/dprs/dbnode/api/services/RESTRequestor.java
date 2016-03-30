package sk.fiit.dprs.dbnode.api.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handle a REST request
 * 
 * @author Jozef Zatko
 */
public class RESTRequestor {

	private String requestMethod;
	private String requestUrl;
	
	public RESTRequestor(String requestMethod, String requestUrl) {
		
		this.requestMethod = requestMethod;
		this.requestUrl = requestUrl;
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
			
		if (conn.getResponseCode() != 200) {
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
