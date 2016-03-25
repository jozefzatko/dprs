package sk.fiit.dprs.dbnode.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sk.fiit.dprs.dbnode.exceptions.InvalidVectorClockFormatException;

/**
 * Model of DB vector clock
 * 
 * @author Jozef Zatko
 */
public class VectorClock {

	private static final String REGEX_PATTERN = "^\\[(?<![-.])\\b[0-9]+\\b(?!\\.[0-9]),(?<![-.])\\b[0-9]+\\b(?!\\.[0-9]),(?<![-.])\\b[0-9]+\\b(?!\\.[0-9])\\]$";
	
	private int originalValue;
	private int firstReplica;
	private int secondReplica;
	
	private String vClockDefinition;
	
	public VectorClock(String vClockDefinition) throws InvalidVectorClockFormatException {

		this.vClockDefinition = vClockDefinition;
		
		Pattern pattern = Pattern.compile(REGEX_PATTERN);
        Matcher matcher = pattern.matcher(vClockDefinition);

        if(matcher.matches()) {
        	try {
        		String[] values = vClockDefinition.replaceAll("^\\[", "").replaceAll("\\]$", "").split(",");
            	originalValue = Integer.parseInt(values[0]);
            	firstReplica = Integer.parseInt(values[1]);
            	secondReplica = Integer.parseInt(values[2]);
        	} catch (Exception e) {
        		throw new InvalidVectorClockFormatException(vClockDefinition);
        	}
        } else {
        	throw new InvalidVectorClockFormatException(vClockDefinition);
        }
	}
	
	public int getOriginalValue() {
		return originalValue;
	}

	public int getFirstReplica() {
		return firstReplica;
	}

	public int getSecondReplica() {
		return secondReplica;
	}

	public String getvClockDefinition() {
		return vClockDefinition;
	}
}
