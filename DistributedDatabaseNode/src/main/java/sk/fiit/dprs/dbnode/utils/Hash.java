package sk.fiit.dprs.dbnode.utils;

import java.util.zip.CRC32;

/**
 * Provide calculation of Hash value
 * 
 * @author Jozef Zatko
 */
public class Hash {

	private static CRC32 crc = new CRC32();
	
	/**
	 * CRC32 hash function
	 * 
	 * @param data input
	 * @return hash value
	 */
	public static long get(String data) {
		
		crc.reset();
	    crc.update(data.getBytes());
	    return crc.getValue();
	}
}
