package sk.fiit.dprs.dbnode.exceptions;

/**
 * Exception for invalid vector clock format of API call 
 * 
 * @author Jozef Zatko 
 */
public class InvalidVectorClockFormatException extends InvalidFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5556899137280978951L;
	
	/**
	 * Inherited constructor from InvalidFormatException
	 * 
	 * @param originalInput original vector clock parameter from API call
	 */
	public InvalidVectorClockFormatException(String originalInput) {
		super(originalInput);
	}
}
