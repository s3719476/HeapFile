import java.util.Vector;

public class BPTree {
	private Node root = new DataNode();
	private Reader reader;
	private int level;
	private ByteConverter bc = ByteConverter.getInstance();
	private RecordTemplateHelper rth = new RecordTemplateHelper();
	private BinaryTreeFileWriter btfw = BinaryTreeFileWriter.getInstance();
	
	public BPTree() {
	}
	
	public void insert(KRid entry) {		
		root.insert(entry);
		
		if (root.getSize() > root.getFanout()) {
			++level;
			NodeKeySplit split = root.split();
			
			Vector<Integer> newKeys = new Vector<Integer>();
			newKeys.add(split.getKey());
			Vector<Node> newBranches = new Vector<Node>();
			newBranches.add(root);
			newBranches.add(split.getNode());
			
			root =  new IndexNode(newKeys, newBranches);
		}
	}
	
	public void bulkInsert(int pageSize, int idx, int fanout) {
		root.setFanout(fanout);
		level = 1;
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
				
				insert(new KRid(data, new Address(pageCounter, recordStart)));
				
				++recordsRead;
//				System.out.println("Inserting " + data);

//				printTree();
			}
			page = reader.readToBytes(pageSize);
		}
//		printTree();
	}
	
	public void writeTree(int pageSize) {
		btfw.setPageSize(pageSize);
		btfw.insertRootSpace(4);
		root.writeKRid();
		root.writeDataNodes();
		root.writeIndexNodes();
		btfw.completePages(bc.intToBinaryStringToByteSize(level, 1) + root.getLocation().getBinary());
		btfw.writeAllPages();
	}
	
	public void queryTree(int pageSize, int query) {
		Vector<Integer> validRecords = new Vector<Integer>();
		Vector<String> validPages = new Vector<String>();
		
		reader = new Reader("./tree." + pageSize);
		
		// Gets all the pages and stores them in a vector
		Vector<String> pages = new Vector<String>();
		String page = reader.readToBytes(pageSize);
		while (!page.isEmpty()) {
			pages.add(page);
			page = reader.readToBytes(pageSize);
		}
		
		String tempPage = pages.get(0);
		int startOffset = 0;
		int endOffset = 8;
		String tempBinary = tempPage.substring(startOffset, endOffset);
		int levels = bc.binaryToInt(tempBinary);
		
		startOffset = endOffset;
		endOffset = startOffset + (1*8);
		tempBinary = tempPage.substring(startOffset, endOffset);
		int nodePage = bc.binaryToInt(tempBinary);
		
		startOffset = endOffset;
		endOffset = startOffset + (2*8);
		tempBinary = tempPage.substring(startOffset, endOffset);
		int nodeOffset = bc.binaryToInt(tempBinary);
		
		for (int j = 1; j < levels; ++j) {
			tempPage = pages.get(nodePage-1);
			startOffset = nodeOffset;
			endOffset = startOffset + (1*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			int nodeSize = bc.binaryToInt(tempBinary);
			
			boolean found = false;
			for (int i = 0; i < nodeSize && found == false; ++i) {
				startOffset = endOffset + (3*8);
				endOffset = startOffset + (3*8);
				tempBinary = tempPage.substring(startOffset, endOffset);
				int key = bc.binaryToInt(tempBinary);
				if (query < key) found = true;
			}
			if (found == false) startOffset = endOffset;
			else startOffset -= (3*8);
			endOffset = startOffset + (1*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			nodePage = bc.binaryToInt(tempBinary);
			
			startOffset = endOffset;
			endOffset = startOffset + (2*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			nodeOffset = bc.binaryToInt(tempBinary);
		}
		
		tempPage = pages.get(nodePage-1);
		startOffset = nodeOffset;
		endOffset = startOffset + (1*8);
		tempBinary = tempPage.substring(startOffset, endOffset);
		int nodeSize = bc.binaryToInt(tempBinary);
		
		startOffset = endOffset;
		endOffset = startOffset + (3*8);
		tempBinary = tempPage.substring(startOffset, endOffset);
		int key = bc.binaryToInt(tempBinary);
		boolean found = false;
		for (int i = 0; i < nodeSize && found == false; ++i) {
			if (key == query) found = true;
			else {
				startOffset = endOffset + (3*8);
				endOffset = startOffset + (3*8);
				tempBinary = tempPage.substring(startOffset, endOffset);
				key = bc.binaryToInt(tempBinary);
			}
		}
		if (found == true) {
			startOffset = endOffset;
			endOffset = startOffset + (1*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			nodePage = bc.binaryToInt(tempBinary);
			
			startOffset = endOffset;
			endOffset = startOffset + (2*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			nodeOffset = bc.binaryToInt(tempBinary);
			
			tempPage = pages.get(nodePage-1);
			startOffset = nodeOffset;
			endOffset = startOffset + (1*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			int size = bc.binaryToInt(tempBinary);
			
			for (int j = 0; j < size; ++j) {
				startOffset = endOffset;
				endOffset = startOffset + (1*8);
				tempBinary = tempPage.substring(startOffset, endOffset);
				nodePage = bc.binaryToInt(tempBinary);
				
				startOffset = endOffset;
				endOffset = startOffset + (2*8);
				tempBinary = tempPage.substring(startOffset, endOffset);
				nodeOffset = bc.binaryToInt(tempBinary);
				
				String heapPage = "";
				reader = new Reader("./heap." + pageSize);
				for (int k = 0; k < nodePage; ++k) heapPage = reader.readToBytes(pageSize);
				validPages.add(heapPage);
				validRecords.add(nodeOffset);
			}
		}
		
		System.out.println("Items found:");
		for (int i = 0; i < validRecords.size(); ++i) {
			System.out.println(rth.getFormattedNameValue(getAllFieldsInRecord(validPages.get(i), validRecords.get(i))));
		}
		
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
	
	public void printTree() {
		root.print(0);
	}
	
}
