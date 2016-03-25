package sk.fiit.dprs.dbnode.tests;

import org.junit.Test;

import sk.fiit.dprs.dbnode.exceptions.InvalidFormatException;
import sk.fiit.dprs.dbnode.models.VectorClock;

/**
 * Test for VectorClock class constructor
 * 
 * @author Jozef Zatko
 */
public class VectorClockTest {

	@Test
	public void newInstanceTest_0() throws InvalidFormatException {
		
		new VectorClock("[0,0,0]");
		new VectorClock("[5,0,0]");
		new VectorClock("[0,5,0]");
		new VectorClock("[0,0,5]");
		
		new VectorClock("[0,0,500]");
		new VectorClock("[20,50,5]");
		new VectorClock("[7200,11800,500]");
		new VectorClock("[100,1000,10000]");
		
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_1() throws InvalidFormatException {
		
			new VectorClock("");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_2() throws InvalidFormatException {
		
			new VectorClock("asd");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_3() throws InvalidFormatException {
		
			new VectorClock("[3,2]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_4() throws InvalidFormatException {
		
			new VectorClock("[3,2,2,2]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_5() throws InvalidFormatException {
		
			new VectorClock("3,2,2");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_6() throws InvalidFormatException {
		
			new VectorClock("[3,2,2]\\");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_7() throws InvalidFormatException {
		
			new VectorClock("f[3,2,2]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_8() throws InvalidFormatException {
		
			new VectorClock("[0.5,1,3]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_9() throws InvalidFormatException {
		
			new VectorClock("[3,-4,1]");
	}
	
	@Test (expected = InvalidFormatException.class)
	public void newInstanceTest_10() throws InvalidFormatException {
		
			new VectorClock("[3,1,a]");
	}
}