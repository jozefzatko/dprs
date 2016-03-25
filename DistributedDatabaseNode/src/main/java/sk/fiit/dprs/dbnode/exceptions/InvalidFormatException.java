package sk.fiit.dprs.dbnode.exceptions;

/**
 * Exception for invalid data format of API call 
 * 
 * @author Jozef Zatko 
 */
public class InvalidFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7719181138302787379L;

	private String originalInput;
	
	/**
	 * Custom constructor
	 * 
	 * @param originalInput original parameter from API call
	 */
	public InvalidFormatException(String originalInput) {
		
		super();
		this.originalInput = originalInput;
	}
	
	/**
	 * Return complete exception information
	 */
	public String toString() {
		
		return this.getClass().getName() + "\n\nWrong format: " + this.originalInput;
	}
	
	public String getOriginalInput() {
		return this.originalInput;
	}
}
