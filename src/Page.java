import java.util.Vector;

// Class to represent a page in the heap
public class Page {
	
	private ByteConverter bc = ByteConverter.getInstance();
	private Vector<Record> records;	// Bunch of records the page holds
	private SlotDirectory directory;	// The slot directory to navigate the page
	private int pageByteSize;	// How big the page is in bytes
	private int freeBytes;	// How much space is left on the page in bytes
	
	public Page(int pageSizeByte) {
		this.pageByteSize = pageSizeByte;
		this.freeBytes = pageSizeByte;
		this.records = new Vector<Record>();
		this.directory = new SlotDirectory(this.pageByteSize);
		
		// Header data for slot directory free space location and amount of entries to deduct from amount of free bytes
		this.freeBytes -= directory.getEntryByteSize();
		this.freeBytes -= directory.getEntryByteSize();
	}
	
	// Adds a record
	// Assumes that the record can fit
	public void addRecord(Record record) {
		records.add(record);
		
		// Gets the sizes of the record and adds it equivalent entry in the directory slots
		int entrySize = bc.getNumberOfBytes(record.getBinaryWithOffsets());
		directory.addSlotOffset(entrySize);
		
		// Reduces the amount of free bytes
		freeBytes -= entrySize;
		freeBytes -= directory.getEntryByteSize();
	}
	
	
	// Gets the binary of the page
	public String getBinary() {
		String binary = "";
		
		// Gets the binary of all the records
		for (Record record : records) {
			binary += record.getBinaryWithOffsets();
		}
		
		// Gets the filler data representing free space
		binary += new String(new char[freeBytes*8]).replace("\0", "0");
		
		// Adds the slot directory on the end
		binary += directory.getBinary();
		
		return binary;
	}
	
	public int getPageSizeByte() {
		return pageByteSize;
	}
	
	public int getFreeBytes() {
		return freeBytes;
	}
	
	public int getDirecteryEntryByteSize() {
		return directory.getEntryByteSize();
	}
}
