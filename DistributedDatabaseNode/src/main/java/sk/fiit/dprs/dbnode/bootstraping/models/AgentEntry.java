package sk.fiit.dprs.dbnode.bootstraping.models;

/**
 * Model of AgentEntry of Consul /v1/catalog/register request
 * 
 * @author Jozef Zatko
 */
public class AgentEntry {

	private String ID;
	private String Name;
	private String Address;
	private int Port;
	private Check Check;
	
	public AgentEntry(String nodeIP, Check check) {
		
		this.ID = nodeIP;
		this.Name = "dbnode";
		this.Address = nodeIP;
		this.Port = 4567;
		this.Check = check;
	}

	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}

	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}

	public int getPort() {
		return Port;
	}
	public void setPort(int port) {
		Port = port;
	}

	public Check getCheck() {
		return Check;
	}
	public void setCheck(Check check) {
		Check = check;
	}
	
}
