package sk.fiit.dprs.dbnode.exceptions;

/** 
 * Exception when cannot ping DB node for any reason
 * 
 * @author Jozef Zatko 
 */
public class CannotPingNodeException extends Exception {

	public CannotPingNodeException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5405121432078620663L;

}
