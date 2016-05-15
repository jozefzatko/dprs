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
	 * Update Node Table Record
	 * 
	 * @param id node id - ipAdress
	 * @param from hash from
	 * @param to hash to
	 * @param firstReplica ip of first replica
	 * @param secondReplica ip of second replica
	 */
	public void updateNode(String id, long from, long to, String firstReplica, String secondReplica) {
		
		try {
			this.table.loadNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(NodeTableRecord i : table.getTable()) {
			
			if (id.equals(i.getId())) {
				
				i.setHashFrom(from);
				i.setHashTo(to);
				i.setFirstReplicaId(firstReplica);
				i.setSecondReplicaId(secondReplica);
				
				break;
			}
		}
		
		try {
			table.writeNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write record if node is the first one
	 * 
	 * @param ip IP address of new node
	 */
	public void addFirstNode(String ip) {
		
		NodeTableRecord record = new NodeTableRecord(ip, 0, 4294967295L, "", "");
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
		
		NodeTableRecord record = new NodeTableRecord(ip, 2147483648L, 4294967295L, "", "");
		this.table.getTable().add(record);
		
		String first = findNodeByHash(0L);
		
		updateNode(first, 0, 2147483647L, "", "");
		
		try {
			this.table.writeNodeTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
