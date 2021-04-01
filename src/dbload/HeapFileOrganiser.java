package dbload;

import java.io.InputStream;
import java.util.Vector;

public class HeapFileOrganiser {
	
	final String csvFilePath = "data/Pedestrian_Counting_System_-_Monthly__counts_per_hour_ - Copy.csv";
	final String dlFilePath = "data/file";
	
	private ByteConverter bc;
	private CSVReader csvr;
	private DataLoader dl;
	
	private Vector<Record> recordList = new Vector<Record>();
	private Vector<Page> pageList = new Vector<Page>();
	
	public InputStream is;
	
//	private Vector<Pedestrian> pedestrainList = new Vector<Pedestrian>();
//	private Vector<Sensor> sensorList = new Vector<Sensor>();
//	private Vector<Time> timeList = new Vector<Time>();
//	
//	private Vector<Integer> sensorKeys = new Vector<Integer>();
//	private Vector<String> timeKeys = new Vector<String>();
//	private Vector<Integer> pedestrianKeys = new Vector<Integer>();
	
	
	public HeapFileOrganiser() {
		bc = new ByteConverter();
		csvr = new CSVReader(csvFilePath);
		dl = new DataLoader(dlFilePath);
	}
	
	public void loadAllData() {
		int counter = 0;
		
		String line = csvr.readNextLine();
		while (line != null) {
			++counter;
			System.out.println("Loading entry number: " + counter);
			
			storeEntry(line);
			
			line = csvr.readNextLine();
		}
		
	}
	
	private void storeEntry(String data) {
		String[] values = data.split(",");
		
		recordList.add(new Record(
				Integer.parseInt(values[0]),
				values[1],
				Integer.parseInt(values[2]),
				values[3],
				Integer.parseInt(values[4]),
				values[5],
				Integer.parseInt(values[6]),
				Integer.parseInt(values[7]),
				values[8],
				Integer.parseInt(values[9])
						)
				);
	}
	
	public void recordsToPages() {
		int pageByteSize = 2048;
		
		Page page = new Page(pageByteSize);
		for (Record record : recordList) {
			int freeBytes = page.getFreeBytes();
			int requiredBytes = bc.getNumberOfBytes(record.getBinaryWithOffsets()) + page.getDirecteryEntryByteSize();
			
			if (freeBytes < requiredBytes) {
				pageList.add(page);
				page = new Page(pageByteSize);
			}
			
			page.addRecord(record);
		}
		pageList.add(page);
	}
	
	public void writeAllEntries() {
		int counter = 0;
		for (Page page : pageList) {
			++counter;
			System.out.println("Writing page number: " + counter);
			
			String binary = page.getBinary();
			dl.writeData(binary);
		}
	}
	
	public void search() {
		csvr = new CSVReader(dlFilePath);
		
		String page = getPage();
		int directoryEntryByteSize = bc.getNumberOfBytesToAllowValue(2048);
		System.out.println(page);
		
		int endSubStr = page.length();
		int startSubStr = endSubStr - (directoryEntryByteSize*8);
		
		String binaryString = page.substring(startSubStr, endSubStr);
		int startByte = Integer.parseInt(binaryString, 2);
		System.out.println(binaryString);
		System.out.println(startByte);
		
		endSubStr = startSubStr;
		startSubStr = endSubStr - (directoryEntryByteSize*8);
		binaryString = page.substring(startSubStr, endSubStr);
		startByte = Integer.parseInt(binaryString, 2);
		System.out.println(binaryString);
		System.out.println(startByte);
		
		endSubStr = startSubStr;
		startSubStr = endSubStr - (directoryEntryByteSize*8);
		binaryString = page.substring(startSubStr, endSubStr);
		startByte = Integer.parseInt(binaryString, 2);
		System.out.println(binaryString);
		System.out.println(startByte);
	}
	
	public String getPage() {
		return csvr.readToBytes(2048*8);
	}
	
	public void closeEverything() {
		csvr.closeFile();
		dl.closeFile();
	}
	
	
}
