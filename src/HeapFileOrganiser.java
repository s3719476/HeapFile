import java.util.Vector;

// Central class to manage all other classes
public class HeapFileOrganiser {
	private ByteConverter bc = ByteConverter.getInstance();
	private Reader reader;
	private Writer writer;
	private RecordTemplateHelper rth = new RecordTemplateHelper();
	
	private Vector<Record> recordList = new Vector<Record>();
	private Vector<Page> pageList = new Vector<Page>();
	
	// Initialises all tools required
	public HeapFileOrganiser() {
	}
	
	public void loadDataToHeap(int pageSize, String csvFile) {
		long timeMilliStart = System.currentTimeMillis();
		
		reader = new Reader(csvFile);
//		System.out.println("Loading Data");
		loadAllData();
		recordsToPages(pageSize);
		
		writer = new Writer("heap." + pageSize);
//		System.out.println("Writing Data");
		writeAllEntries();
		closeEverything();
		
		long timeMilliEnd = System.currentTimeMillis();
		
		System.out.println("Amount of records: " + recordList.size());
		System.out.println("Amount of pages: " + pageList.size());
		System.out.println("Time taken in milliseconds = " + (timeMilliEnd - timeMilliStart));
	}
	
	// Not enough heap space when running on linux machine
	// Instead of reading and storing then writing to a heap file
	// It reads until the page is full or no more records and then writes the page
	public void loadDataToHeapV2(int pageSize, String csvFile) {
		// Start up readers and writers
		reader = new Reader(csvFile);
		writer = new Writer("heap." + pageSize);
		
		// Start timing
		long timeMilliStart = System.currentTimeMillis();
		
		// Skip header line and read the first data line
		String line = reader.readNextLine();
		line = reader.readNextLine();
		
		// Initialises variables to count how many pages and records
		// Whenever a new record or page is created its respective variable increments
		int recordAmount = 0;
		int pageAmount = 1;
		Page page = new Page(pageSize);
		
		// Loops until all the data in the csv is read
		while (line != null) {
			++recordAmount;
			Record record = createRecord(line);
			
//			System.out.println("Converting to Heap Record: " + recordAmount);
			
			// Gets the free bytes left in the page and bytes required to store the record
			int freeBytes = page.getFreeBytes();
			int requiredBytes = bc.getNumberOfBytes(record.getBinaryWithOffsets()) + page.getDirecteryEntryByteSize();
			
			// If theres not enough bytes write the page and get a new page
			if (freeBytes < requiredBytes) {
				writer.writeData(page.getBinary());
				
				++pageAmount;
				page = new Page(pageSize);
			}
			
			// Add the record to the page
			page.addRecord(record);
			
			// Read the next line and prepare for next iteration
			line = reader.readNextLine();
		}
		// After all data is read then write the last page to the heap file
		writer.writeData(page.getBinary());
		
		// End timing
		long timeMilliEnd = System.currentTimeMillis();
		
		// Close the read and write files
		closeEverything();
		
		// Print relevant data
		System.out.println("Amount of records: " + recordAmount);
		System.out.println("Amount of pages: " + pageAmount);
		System.out.println("Time taken in milliseconds = " + (timeMilliEnd - timeMilliStart));
	}
	
	// Reads all the data and stores the records in objects
	private void loadAllData() {		
		// Reads header data then goes to first entry
		String line = reader.readNextLine();
		line = reader.readNextLine();
		int counter = 0;
		while (line != null) {
			counter++;
//			System.out.println("Reading Record: " + counter);
			
			line = reader.readNextLine();
		}
		
	}
	
	// Adds single record data into object stored on the recordList
	private void storeEntry(String data) {
		recordList.add(createRecord(data));
	}
	
	private Record createRecord(String data) {
		String[] values = data.split(",");
		
		Record record = new Record(
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
						);
		
		return record;
	}
	
	// Places all the records stored in the list into page objects
	// Used to set up the layout of a heap file
	private void recordsToPages(int pageSize) {
		int pageByteSize = pageSize;
		int counter = 0;
		Page page = new Page(pageByteSize);
		for (Record record : recordList) {
			counter++;
//			System.out.println("Placing into Page: " + counter);
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
	
	// Places all entries in an external file
	private void writeAllEntries() {
		int pageAmount = pageList.size();
		int counter = 0;
		for (Page page : pageList) {
			counter ++;
//			System.out.println("Writing Page: " + counter + "/" + pageAmount);
			
			String binary = page.getBinary();
			writer.writeData(binary);
		}
	}
	
	
	// Searches a given value for a given field from a file of given size
	public void search(int pageSize, String value, int fieldNum) {
		reader = new Reader("heap." + pageSize);
		
		// Stores all records and pages for later prints to get more accurate timing
		Vector<Integer> validRecords = new Vector<Integer>();
		Vector<String> validPages = new Vector<String>();
		
		// Start timing
		long timeMilliStart = System.currentTimeMillis();
		
		// Gets the first page and reads the file in given pages
		String page = getPage(pageSize);
		while (!page.isEmpty()) {
			int directoryEntryByteSize = bc.getNumberOfBytesToAllowValue(pageSize);
			
			// Gets the first binary of the directory slot which is the location of the free space
			int endSubStr = page.length();
			int startSubStr = endSubStr - (directoryEntryByteSize*8);
			String binaryString = page.substring(startSubStr, endSubStr);
			int freeSpaceStartByte = Integer.parseInt(binaryString, 2);
			
			// Gets next binary in slot directory which is the amount of entries
			endSubStr = startSubStr;
			startSubStr = endSubStr - (directoryEntryByteSize*8);
			binaryString = page.substring(startSubStr, endSubStr);
			int numRecords = Integer.parseInt(binaryString, 2);
			
			// Iterates through the directory offsets determined by amount of entries to get the location of each records
			int recordsRead = 0;		
			while (recordsRead < numRecords) {
				// Gets the next entry which is the offset from the start of the page to get to the respective record
				endSubStr = startSubStr;
				startSubStr = endSubStr - (directoryEntryByteSize*8);
				binaryString = page.substring(startSubStr, endSubStr);
				int recordStart = bc.binaryToInt(binaryString) * 8;
				
				// Goes to the byte related to the field offset in the record
				// Gets the location to the start of the desired field
				int offsetStartSubStr = recordStart + (fieldNum * 8);
				int offsetEndSubStr = offsetStartSubStr + 8;
				binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
				int fieldOffsetStart = bc.binaryToInt(binaryString) * 8;
				
				// Gets the offset of the next field to find where the binary stops relating to the desired field
				offsetStartSubStr = offsetEndSubStr;
				offsetEndSubStr = offsetStartSubStr + 8;
				binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
				int fieldOffsetStop = bc.binaryToInt(binaryString) * 8;
				
				// Gets the binary of relating to the desired field
				binaryString = page.substring(fieldOffsetStart + recordStart, fieldOffsetStop + recordStart);
//				System.out.println("Start:" + (fieldOffsetStart + recordStart) + "End:" + (fieldOffsetStop + recordStart));
				// Converts the data to understandable values depending if type string or int
				// Compares to the search value
				String data = "";
				if (rth.getFieldTypes()[fieldNum].equals("String")) {
					data = bc.binaryToString(binaryString);
					
				} else {
					data = Integer.toString(bc.binaryToInt(binaryString));
				}
				
				// If the entry matches then it stores the record start location and page for later prints
				if (data.contains(value)) {
					validPages.add(page);
					validRecords.add(recordStart);
				}
	
				++recordsRead;
			}
			page = getPage(pageSize);
		}
		
		// Stops timing
		long timeMilliEnd = System.currentTimeMillis();
		
		// Prints all records that match and time it took to search all the files
		System.out.println("Items found:");
		for (int i = 0; i < validRecords.size(); ++i) {
			System.out.println(rth.getFormattedNameValue(getAllFieldsInRecord(validPages.get(i), validRecords.get(i))));
		}
		System.out.println("Time taken in milliseconds = " + (timeMilliEnd - timeMilliStart));
	}
	
	// Returns an array of all the values for a given record in a page
	private String[] getAllFieldsInRecord(String page, int recordStart) {
		String[] retValue = new String[rth.getFieldNum()];
		String[] fieldTypes = rth.getFieldTypes();
		
		// Iterates through each value in the fields
		for (int i = 0; i < retValue.length; ++i) {
			// Gets the the next offset location for the next field
			int offsetStartSubStr = recordStart + (i * 8);
			int offsetEndSubStr = offsetStartSubStr + 8;
			String binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
			int fieldOffsetStart = bc.binaryToInt(binaryString) * 8;
			
			// Gets the following offset location to find the end of the field
			offsetStartSubStr = offsetEndSubStr;
			offsetEndSubStr = offsetStartSubStr + 8;
			binaryString = page.substring(offsetStartSubStr, offsetEndSubStr);
			int fieldOffsetStop = bc.binaryToInt(binaryString) * 8;
			
			// Gets the binary value and converts it into true values and then to Strings to append of the array
			binaryString = page.substring(fieldOffsetStart + recordStart, fieldOffsetStop + recordStart);
			
			if (fieldTypes[i].equals("String")) {
				retValue[i] = bc.binaryToString(binaryString);
			} else {
				retValue[i] = Integer.toString(bc.binaryToInt(binaryString));
			}
			
			
		}
		
		return retValue;
	}
	
	private String getPage(int pageByteSize) {
		return reader.readToBytes(pageByteSize);
	}
	
	public void closeEverything() {
		reader.closeFile();
		writer.closeFile();
	}
	
}
