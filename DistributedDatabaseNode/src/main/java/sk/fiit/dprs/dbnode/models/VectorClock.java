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
	
	public void setOriginalValue(int originalValue) {
		this.originalValue = originalValue;
	}

	public void setFirstReplica(int firstReplica) {
		this.firstReplica = firstReplica;
	}

	public void setSecondReplica(int secondReplica) {
		this.secondReplica = secondReplica;
	}
	
	public boolean isNewerOrSame(VectorClock oldVC, VectorClock newVC){
		if(oldVC.getOriginalValue()>newVC.getOriginalValue())
			return false;
		if(oldVC.getFirstReplica()>newVC.getFirstReplica())
			return false;
		if(oldVC.getSecondReplica()>newVC.getSecondReplica())
			return false;
		return true;
	}

	@Override
	public String toString() {
		
		String vClockDef= "["+originalValue+","+firstReplica+","+secondReplica+"]";
		return vClockDef;
	}
}
