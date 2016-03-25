package sk.fiit.dprs.dbnode.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sk.fiit.dprs.dbnode.exceptions.InvalidQuorumFormatException;

/**
 * Model of DB sloppy quorum
 * 
 * @author Jozef Zatko
 */
public class Quorum {

	private static final String REGEX_PATTERN = "^\\[[1-3],[1-3],[1-3]\\]$";
	
	private int countOfReplics;
	private int readQuorum;
	private int writeQuorum;
	
	private String quorumDefinition;
	
	public Quorum(String quorumDefinition) throws InvalidQuorumFormatException {

		this.quorumDefinition = quorumDefinition;
		
		Pattern pattern = Pattern.compile(REGEX_PATTERN);
        Matcher matcher = pattern.matcher(quorumDefinition);

        if(matcher.matches()) {
        	try {
        		countOfReplics = Integer.parseInt(quorumDefinition.substring(1,2));
            	readQuorum = Integer.parseInt(quorumDefinition.substring(3,4));
            	writeQuorum = Integer.parseInt(quorumDefinition.substring(5,6));
        	} catch (Exception e) {
        		throw new InvalidQuorumFormatException(quorumDefinition);
        	}
        } else {
        	throw new InvalidQuorumFormatException(quorumDefinition);
        }
        
        if(countOfReplics < readQuorum || countOfReplics < writeQuorum) {
        	throw new InvalidQuorumFormatException(quorumDefinition);
        }
	}
	
	public int getCountOfReplics() {
		return countOfReplics;
	}

	public int getReadQuorum() {
		return readQuorum;
	}

	public int getWriteQuorum() {
		return writeQuorum;
	}

	public String getQuorumDefinition() {
		return quorumDefinition;
	}
}
