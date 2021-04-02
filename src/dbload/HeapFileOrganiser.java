package dbload;

import java.io.InputStream;
import java.util.Vector;

public class HeapFileOrganiser {
	
	final String csvFilePath = "data/Pedestrian_Counting_System_-_Monthly__counts_per_hour_ - Copy.csv";
	final String dlFilePath = "data/file";
	
	private ByteConverter bc;
	private CSVReader csvr;
	private DataLoader dl;
	private RecordTemplateHelper rth = new RecordTemplateHelper();
	
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
		String line = csvr.readNextLine();
		while (line != null) {
			
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
		for (Page page : pageList) {
			
			String binary = page.getBinary();
			dl.writeData(binary);
		}
	}
	
	public void search(int pageSize, String value, int fieldNum) {
		csvr = new CSVReader(dlFilePath);
		
		Vector<Integer> validRecords = new Vector<Integer>();
		Vector<String> validPages = new Vector<String>();
		
		
		long timeMilliStart = System.currentTimeMillis();
		
		String page = getPage(pageSize);
		while (!page.isEmpty()) {
			int directoryEntryByteSize = bc.getNumberOfBytesToAllowValue(pageSize);
			
			int endSubStr = page.length();
			int startSubStr = endSubStr - (directoryEntryByteSize*8);
			
			String binaryString = page.substring(startSubStr, endSubStr);
			int freeSpaceStartByte = Integer.parseInt(binaryString, 2);
			
			endSubStr = startSubStr;
			startSubStr = endSubStr - (directoryEntryByteSize*8);
			binaryString = page.substring(startSubStr, endSubStr);
			int numRecords = Integer.parseInt(binaryString, 2);
	
			int recordsRead = 0;		
			while (recordsRead < numRecords) {
				endSubStr = startSubStr;
				startSubStr = endSubStr - (directoryEntryByteSize*8);
				binaryString = page.substring(startSubStr, endSubStr);
				int recordStart = bc.binaryToInt(binaryString) * 8;
				
				int offsetStartSubStr = recordStart + (fieldNum * 8);
				int offsetEndSubStr = offsetStartSubStr + 8;
				binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
				int fieldOffsetStart = bc.binaryToInt(binaryString) * 8;
				
				offsetStartSubStr = offsetEndSubStr;
				offsetEndSubStr = offsetStartSubStr + 8;
				binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
				int fieldOffsetStop = bc.binaryToInt(binaryString) * 8;
				
				binaryString = page.substring(fieldOffsetStart + recordStart, fieldOffsetStop + recordStart);
				
				String data = "";
				if (rth.getFieldTypes()[fieldNum].equals("String")) {
					data = bc.binaryToString(binaryString);
					
				} else {
					data = Integer.toString(bc.binaryToInt(binaryString));
				}
				
				if (data.equals(value)) {
					validPages.add(page);
					validRecords.add(recordStart);
				}
	
				++recordsRead;
			}
			
			page = getPage(pageSize);
		}
		
		long timeMilliEnd = System.currentTimeMillis();
		
		System.out.println("Items found:");
		for (int i = 0; i < validRecords.size(); ++i) {
			System.out.println(rth.getFormattedNameValue(getAllFieldsInRecord(validPages.get(i), validRecords.get(i))));
		}
		System.out.println("Time taken in milliseconds = " + (timeMilliEnd - timeMilliStart));
	}
	
	private String[] getAllFieldsInRecord(String page, int recordStart) {
		String[] retValue = new String[rth.getFieldNum()];
		String[] fieldTypes = rth.getFieldTypes();
		
		for (int i = 0; i < retValue.length; ++i) {
			int offsetStartSubStr = recordStart + (i * 8);
			int offsetEndSubStr = offsetStartSubStr + 8;
			String binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
			int fieldOffsetStart = bc.binaryToInt(binaryString) * 8;
			
			offsetStartSubStr = offsetEndSubStr;
			offsetEndSubStr = offsetStartSubStr + 8;
			binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
			int fieldOffsetStop = bc.binaryToInt(binaryString) * 8;
			
			binaryString = page.substring(fieldOffsetStart + recordStart, fieldOffsetStop + recordStart);
			
			if (fieldTypes[i].equals("String")) {
				retValue[i] = bc.binaryToString(binaryString);
			} else {
				retValue[i] = Integer.toString(bc.binaryToInt(binaryString));
			}
			
			
		}
		
		return retValue;
	}
	
	public String getPage(int pageByteSize) {
		return csvr.readToBytes(pageByteSize);
	}
	
	public void closeEverything() {
		csvr.closeFile();
		dl.closeFile();
	}
	
}
