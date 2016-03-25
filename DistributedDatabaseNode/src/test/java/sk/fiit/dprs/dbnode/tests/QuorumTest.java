package sk.fiit.dprs.dbnode.tests;

import org.junit.Test;

import sk.fiit.dprs.dbnode.exceptions.InvalidFormatException;
import sk.fiit.dprs.dbnode.models.Quorum;

/**
 * Test for Quorum class constructor
 * 
 * @author Jozef Zatko
 */
public class QuorumTest {

	@Test
	public void newInstanceTest0() throws InvalidFormatException {

		new Quorum("[3,3,1]");
		new Quorum("[3,1,3]");
		new Quorum("[3,3,1]");
		new Quorum("[3,3,3]");
	}

	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_1() throws InvalidFormatException {
		
			new Quorum("");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_2() throws InvalidFormatException {
		
			new Quorum("[]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_3() throws InvalidFormatException {
		
			new Quorum("[0,0,0]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_4() throws InvalidFormatException {
		
			new Quorum("[0,1,3]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_5() throws InvalidFormatException {
		
			new Quorum("[0,3,3]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_6() throws InvalidFormatException {
		
			new Quorum("[0,0,3]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_7() throws InvalidFormatException {
		
			new Quorum("[5,5,5]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_8() throws InvalidFormatException {
		
			new Quorum("[1,3,3]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_9() throws InvalidFormatException {
		
			new Quorum("[4,2,2]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_10() throws InvalidFormatException {
		
			new Quorum(" [3,2,2]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_11() throws InvalidFormatException {
		
			new Quorum("[3,2,2] ");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_12() throws InvalidFormatException {
		
			new Quorum("[3.0,2,2] ");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_13() throws InvalidFormatException {
		
			new Quorum("[-3,2,2] ");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_14() throws InvalidFormatException {
		
			new Quorum("[-3,-2,-2] ");
	}
}
