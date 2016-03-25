package sk.fiit.dprs.dbnode.exceptions;

/** 
 * Exception occurs when key is not in DB
 * 
 * @author Jozef Zatko 
 */
public class MissingKeyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624090846426172379L;
	
	private String key;
	
	/**
	 * Custom constructor
	 * 
	 * @param key original key from API call
	 */
	public MissingKeyException(String key) {
		
		super();
		this.key = key;
	}
	
	/**
	 * Return complete exception information
	 */
	public String toString() {
		
		return this.getClass().getName() + "\n\nWrong key: " + this.key;
	}

	public String getKey() {	
		return this.key;
	}
}
