package sk.fiit.dprs.dbnode.bootstraping.models;

/**
 * Model of Check of for service registration 
 * 
 * @author Jozef Zatko
 */
public class Check {

	private String Status;
	private String TTL;
	
	public Check() {
		
		this.Status = "warning";
		this.TTL = "30s";
	}
	
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}

	public String getTTL() {
		return TTL;
	}
	public void setTTL(String ttl) {
		TTL = ttl;
	}
	
}
