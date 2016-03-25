package sk.fiit.dprs.dbnode.exceptions;

/**
 * Exception for invalid quorum format of API call 
 * 
 * @author Jozef Zatko 
 */
public class InvalidQuorumFormatException extends InvalidFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2725659318944490031L;
	
	/**
	 * Inherited constructor from InvalidFormatException
	 * 
	 * @param originalInput original quorum parameter from API call
	 */
	public InvalidQuorumFormatException(String originalInput) {
		super(originalInput);
	}
}
