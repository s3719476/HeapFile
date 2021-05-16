import java.util.Vector;

public class BPTree {
	private Node root = new DataNode();
	private Reader reader;
	private ByteConverter bc = new ByteConverter();
	private RecordTemplateHelper rth = new RecordTemplateHelper();
	
	public BPTree(int fanout) {
		root.setFanout(fanout);
	}
	
	public void insert(KRid entry) {		
		root.insert(entry);
		
		if (root.getSize() > root.getFanout()) {
			NodeKeySplit split = root.split();
			
			Vector<Integer> newKeys = new Vector<Integer>();
			newKeys.add(split.getKey());
			Vector<Node> newBranches = new Vector<Node>();
			newBranches.add(root);
			newBranches.add(split.getNode());
			
			root =  new IndexNode(newKeys, newBranches);
		}
	}
	
	public void bulkInsert(int pageSize, int idx) {
		reader = new Reader("./heap." + pageSize);
		
		// Gets the first page and reads the file in given pages
		int pageCounter = 0;
		String page = reader.readToBytes(pageSize);
		while (!page.isEmpty()) {
			++pageCounter;
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
				int offsetStartSubStr = recordStart + (idx * 8);
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
				
				// Converts the data to understandable values depending if type string or int
				// Compares to the search value
				int data = bc.binaryToInt(binaryString);
				
				++recordsRead;
				System.out.println("Inserting " + data);
				insert(new KRid(data, new Address(pageCounter, recordsRead)));

				printTree();
			}
			page = reader.readToBytes(pageSize);
		}
	}
	
	public void printTree() {
		root.print(0);
	}
	
}
