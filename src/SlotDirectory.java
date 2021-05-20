import java.util.Vector;

// Class to temporarily hold the slot directory values
public class SlotDirectory {
	
	private int directoryByteSize;	// How much space the slot directory is currently taking up in bytes
	private int numEntries;	// Amount of slot directory entries
	private int bytesTaken;	// How many bytes the pages records are taking up also where the free space starts
	private int entryByteSize;	// The amount of bytes each entry in the slot directory takes up
	private Vector<Integer> slotPointers;
	
	private ByteConverter bc = ByteConverter.getInstance();
	
	public SlotDirectory(int pageByteSize) {
		this.directoryByteSize = 0;
		this.numEntries = 0;
		this.bytesTaken = 0;
		this.entryByteSize = bc.getNumberOfBytesToAllowValue(pageByteSize);
		this.slotPointers = new Vector<Integer>();
		
		// Adds the the size of two entries to the slot directory
		this.directoryByteSize += getEntryByteSize();
		this.directoryByteSize += getEntryByteSize();
		
	}
	
	// Called when adding an entry into the page so that an entry is added to the directory to find location of record
	// Records are added to start of free space so its address is where the free space starts
	public void addSlotOffset(int slotOffset) {
		// As the slot directory is back to front, we enter the older records at the start
		slotPointers.insertElementAt(bytesTaken, 0);
		
		// Adds how big the record is to the bytes taken
		bytesTaken += slotOffset;
		directoryByteSize += entryByteSize;
		++numEntries;
	}
	
	// Gets the binary form of the slot directory
	public String getBinary() {
		String binary = "";
		
		// Gets the binary of each entry in the slot directory required to access each record
		for (Integer entry : slotPointers) {
			binary += bc.intToBinaryStringToByteSize(entry, entryByteSize);
		}
		
		// Adds extra data on the amount of entries to know how much to read into the directory offset
		// Adds extra data on where the free space starts for the directory
		binary += bc.intToBinaryStringToByteSize(numEntries, entryByteSize);
		binary += bc.intToBinaryStringToByteSize(bytesTaken, entryByteSize);

		return binary;
	}
	
	public int getDirectoryByteSize() {
		return directoryByteSize;
	}
	
	public int getEntryByteSize() {
		return entryByteSize;
	}
}
