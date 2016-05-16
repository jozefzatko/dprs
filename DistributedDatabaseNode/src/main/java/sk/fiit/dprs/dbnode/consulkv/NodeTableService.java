package sk.fiit.dprs.dbnode.consulkv;

/**
 * Provide functions of NodesTable
 * 
 * @author Jozef Zatko
 */
public class NodeTableService {

	private NodesTable table;
	
	public NodeTableService(String consulIp) {
		
		this.table = new NodesTable(consulIp);
	}
	
	/**
	 * Get record by ID
	 * 
	 * @param id identifier
	 * @return node record
	 */
	public NodeTableRecord getRecord(String id) {
		
		try {
			this.table.loadNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(NodeTableRecord i : table.getTable()) {
			
			if (id.equals(i.getId())) {
				
				return i;
			}
		}
		
		return null;
	}
	
	/**
	 * Return count of DB nodes in cluster
	 * 
	 * @return count of nodes
	 */
	public int getCountofNodes() {
		
		try {
			this.table.loadNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return table.getTable().size();
	}
	
	/**
	 * Find IP address of node according to data hash
	 * 
	 * @param hash data hash value
	 * @return IP address of found node
	 */
	public String findNodeByHash(long hash) {
		
		try {
			this.table.loadNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(NodeTableRecord i : table.getTable()) {
			
			if (hash >= i.getHashFrom() && hash <= i.getHashTo()) {
				
				return i.getId();
			}
		}
		
		return null;
	}
	
	/**
	 * Return id of previous node in order
	 * 
	 * @param id node identifier
	 * @return identifier of previous node
	 */
	public String getPrevious(String id) {
				
		try {
			this.table.loadNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long hashValue = -1L;
		
		for(NodeTableRecord i : table.getTable()) {
			
			if (id.equals(i.getId())) {
				
				hashValue = i.getHashFrom();
				break;
			}
		}
		
		if (hashValue == 0) {
			hashValue = 4294967295L;
		} else {
			hashValue -= 1;
		}
				
		for(NodeTableRecord i : table.getTable()) {
			
			if (hashValue == i.getHashTo()) {
				
				return i.getId();
			}
		}
		
		return null;
	}
	
	/**
	 * Return id of next node in order
	 * 
	 * @param id node identifier
	 * @return identifier of next node
	 */
	public String getNext(String id) {
				
		try {
			this.table.loadNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long hashValue = -1L;
		
		for(NodeTableRecord i : table.getTable()) {
			
			if (id.equals(i.getId())) {
				
				hashValue = i.getHashTo();
				break;
			}
		}
		
		if (hashValue == 4294967295L) {
			hashValue = 0;
		} else {
			hashValue += 1;
		}
				
		for(NodeTableRecord i : table.getTable()) {
			
			if (hashValue == i.getHashFrom()) {
				
				return i.getId();
			}
		}
		
		return null;
	}
	
	/**
	 * Update Node Table Record
	 * 
	 * @param id node id - ipAdress
	 * @param from hash from
	 * @param to hash to
	 * @param firstReplica IP of first replica
	 * @param secondReplica IP of second replica
	 * @param status status of node
	 */
	public void updateNode(String id, Long from, Long to, String firstReplica, String secondReplica, String status) {
		
		try {
			this.table.loadNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(NodeTableRecord i : table.getTable()) {
			
			if (id.equals(i.getId())) {
				
				if (from != null) {
					i.setHashFrom(from);
				}
				if (to != null) {
					i.setHashTo(to);
				}
				if (firstReplica != null) {
					i.setFirstReplicaId(firstReplica);
				}
				if (secondReplica != null) {
					i.setSecondReplicaId(secondReplica);
				}
				if (status != null) {
					i.setStatus(status);
				}
				
				break;
			}
		}
		
		try {
			table.writeNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteNode(String id){
		NodeTableRecord record = getRecord(id);
		
		table.getTable().remove(record);
	}
	
	/**
	 * Write record if node is the first one
	 * 
	 * @param ip IP address of new node
	 */
	public void addFirstNode(String ip) {
		
		NodeTableRecord record = new NodeTableRecord(ip, 0L, 1431655763L, "", "");
		this.table.getTable().add(record);
				
		try {
			this.table.writeNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write record if node is the second one
	 * 
	 * @param ip IP address of new node
	 */
	public void addSecondNode(String ip) {
		
		NodeTableRecord record = new NodeTableRecord(ip, 1431655764L, 2863311529L, "", "");
		this.table.getTable().add(record);
		
		try {
			this.table.writeNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write record if node is the third one
	 * 
	 * @param ip IP address of new node
	 */
	public void addThirdNode(String ip) {
		
		NodeTableRecord record = new NodeTableRecord(ip, 2863311530L, 4294967295L, "", "");
		this.table.getTable().add(record);
		
		try {
			this.table.writeNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addNextNode(String ip, long hashFrom, long hashTo, String replica1, String replica2) {
			
			NodeTableRecord record = new NodeTableRecord(ip, hashFrom, hashTo, replica1, replica2);
			this.table.getTable().add(record);
			
			try {
				this.table.writeNodeTable();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	/**
	 * Initialize table in Consul if needed
	 */
	public void initIfNeeded() {
		this.table.initIfNeeded();
	}

}
