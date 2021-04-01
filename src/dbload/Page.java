package dbload;

import java.util.Vector;

public class Page {
	
	private ByteConverter bc = new ByteConverter();
	private Vector<Record> records;
	private SlotDirectory directory;
	private int pageByteSize;
	private int freeBytes;
	
	public Page(int pageSizeByte) {
		this.pageByteSize = pageSizeByte;
		this.freeBytes = pageSizeByte;
		this.records = new Vector<Record>();
		this.directory = new SlotDirectory(this.pageByteSize);
		
		this.freeBytes -= directory.getEntryByteSize();
		this.freeBytes -= directory.getEntryByteSize();
	}
	
	public void addRecord(Record record) {
		records.add(record);
		
		int entrySize = bc.getNumberOfBytes(record.getBinaryWithOffsets());
		directory.addSlotOffset(entrySize);
		
		freeBytes -= entrySize;
		freeBytes -= directory.getEntryByteSize();
	}
	
	public String getBinary() {
		String binary = "";
		
		for (Record record : records) {
			binary += record.getBinaryWithOffsets();
		}
		
		binary += new String(new char[freeBytes*8]).replace("\0", "0");

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
