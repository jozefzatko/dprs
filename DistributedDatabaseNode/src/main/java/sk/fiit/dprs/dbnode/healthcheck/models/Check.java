package sk.fiit.dprs.dbnode.healthcheck.models;

/**
 * Model of HealthNode/Check of Consul /v1/health/service/dbnode response
 * 
 * @author Jozef Zatko
 */
public class Check {

	private String checkId;
	private String status;
	private String serviceName;
	
	
	public String getCheckId() {
		return checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
