package dbload;

import java.util.Vector;

public class SlotDirectory {
	
	private int directoryByteSize;
	private int numEntries;
	private int bytesTaken;
	private int entryByteSize;
	private Vector<Integer> slotPointers;
	
	private ByteConverter bc = new ByteConverter();
	
	public SlotDirectory(int pageByteSize) {
		this.directoryByteSize = 0;
		this.numEntries = 0;
		this.bytesTaken = 0;
		this.entryByteSize = bc.getNumberOfBytesToAllowValue(pageByteSize);
		this.slotPointers = new Vector<Integer>();
		
		this.directoryByteSize += getEntryByteSize();
		this.directoryByteSize += getEntryByteSize();
		
	}
	
	public void addSlotOffset(int slotOffset) {
		slotPointers.insertElementAt(bytesTaken, 0);
		bytesTaken += slotOffset;
		directoryByteSize += entryByteSize;
		++numEntries;
	}
	
	public String getBinary() {
		String binary = "";
		
		for (Integer entry : slotPointers) {
			binary += bc.intToBinaryStringToByteSize(entry, entryByteSize);
		}
		
		binary += bc.intToBinaryStringToByteSize(numEntries, entryByteSize);
		binary += bc.intToBinaryStringToByteSize(bytesTaken, entryByteSize);
//		System.out.println(binary);
		return binary;
	}
	
	public int getDirectoryByteSize() {
		return directoryByteSize;
	}
	
	public int getEntryByteSize() {
		return entryByteSize;
	}
}
