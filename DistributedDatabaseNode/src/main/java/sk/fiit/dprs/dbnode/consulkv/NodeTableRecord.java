package sk.fiit.dprs.dbnode.consulkv;

/**
 * Model of NodeTable record
 * 
 * @author Jozef Zatko
 *
 */
public class NodeTableRecord {

	private String id;
	private long hashFrom;
	private long hashTo;
	private String firstReplicaId;
	private String secondReplicaId;
	private String status;
	
	
	public NodeTableRecord(String id, long hashFrom, long hashTo, String firstReplicaId, String secondReplicaId) {
		
		this.id = id;
		this.hashFrom = hashFrom;
		this.hashTo = hashTo;
		this.firstReplicaId = firstReplicaId;
		this.secondReplicaId = secondReplicaId;
		this.status = "not ready";
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public long getHashFrom() {
		return hashFrom;
	}
	public void setHashFrom(long hashFrom) {
		this.hashFrom = hashFrom;
	}
	
	public long getHashTo() {
		return hashTo;
	}

	public void setHashTo(long hashTo) {
		this.hashTo = hashTo;
	}
	
	public String getFirstReplicaId() {
		return firstReplicaId;
	}
	public void setFirstReplicaId(String firstReplicaId) {
		this.firstReplicaId = firstReplicaId;
	}
	
	public String getSecondReplicaId() {
		return secondReplicaId;
	}
	public void setSecondReplicaId(String secondReplicaId) {
		this.secondReplicaId = secondReplicaId;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}