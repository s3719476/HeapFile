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
		// Recursively calls insert on nodes
		root.insert(entry);
		
		// An insert may create splits in bottom nodes
		// This may propogate up to the root node
		// Checks the size of the root node if it is larger than the fanout and splits the root
		if (root.getSize() > root.getFanout()) {
			// Splitting the root equals a new level
			++level;
			NodeKeySplit split = root.split();
			
			// Arranges the split nodes
			Vector<Integer> newKeys = new Vector<Integer>();
			newKeys.add(split.getKey());
			Vector<Node> newBranches = new Vector<Node>();
			newBranches.add(root);
			newBranches.add(split.getNode());
			
			// Assigns new root node
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
	
	// Writes the tree
	public void writeTree(int pageSize) {
		btfw.setPageSize(pageSize);	// Initialise organisiation of the organiser for writing tree into binary file
		btfw.insertRootSpace(4);	// Save space for the root node address
		// Write all nodes to the bianry file bottom up as the addresses of the below nodes need to be known in order to write the above node
		root.writeKRid();
		root.writeDataNodes();
		root.writeIndexNodes();
		// Flush and write out remaining page out to the binary file
		btfw.completePages(bc.intToBinaryStringToByteSize(level, 1) + root.getLocation().getBinary());
		// Write all the pages stored out to the binary file
		btfw.writeAllPages();
	}
	
	// Query on the index used by the tree stored in the binary file
	public void queryTree(int pageSize, int query) {
		// Lists to store the record addresses obtained from the binary tree that matches the query
		Vector<Integer> validRecords = new Vector<Integer>();
		Vector<String> validPages = new Vector<String>();
		
		// Reader to get the tree
		reader = new Reader("./tree." + pageSize);
		
		// Gets all the pages and stores them in a vector
		Vector<String> pages = new Vector<String>();
		String page = reader.readToBytes(pageSize);
		while (!page.isEmpty()) {
			pages.add(page);
			page = reader.readToBytes(pageSize);
		}
		
		// Reads the first byte of the page which is the amount of the levels in the tree
		String tempPage = pages.get(0);
		int startOffset = 0;
		int endOffset = 8;
		String tempBinary = tempPage.substring(startOffset, endOffset);
		int levels = bc.binaryToInt(tempBinary);
		
		// The following gets the address of the root
		// Next 1 byte which is the page where the root is stored
		startOffset = endOffset;
		endOffset = startOffset + (1*8);
		tempBinary = tempPage.substring(startOffset, endOffset);
		int nodePage = bc.binaryToInt(tempBinary);
		
		// Next 2 bytes - offset of root from the front of page
		startOffset = endOffset;
		endOffset = startOffset + (2*8);
		tempBinary = tempPage.substring(startOffset, endOffset);
		int nodeOffset = bc.binaryToInt(tempBinary);
		
		// Iterates through index nodes until it reaches the data nodes
		for (int j = 1; j < levels; ++j) {
			// Goes to the start of the index node
			// Gets the first byte of the node - size of the node 
			tempPage = pages.get(nodePage-1);
			startOffset = nodeOffset;
			endOffset = startOffset + (1*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			int nodeSize = bc.binaryToInt(tempBinary);
			
			// Iterates through index node keys
			boolean found = false;
			for (int i = 0; i < nodeSize && found == false; ++i) {
				// Gets the next key in the index node
				startOffset = endOffset + (3*8);
				endOffset = startOffset + (3*8);
				tempBinary = tempPage.substring(startOffset, endOffset);
				int key = bc.binaryToInt(tempBinary);
				// Compares key with the query
				if (query < key) found = true;
			}
			// If the query is more than all the keys then the next node is more than all keys
			// else it is the node which has its address stored 3 bytes before the key
			// then gets the address
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
		// Finish for loop once all index nodes have been traversed through
		// Reached the data nodes
		
		// Gets the node size of the data node
		tempPage = pages.get(nodePage-1);
		startOffset = nodeOffset;
		endOffset = startOffset + (1*8);
		tempBinary = tempPage.substring(startOffset, endOffset);
		int nodeSize = bc.binaryToInt(tempBinary);
		
		// Iterates through the keys in the data node until iterated through all keys or found a match
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
		// If a match is found store the addresses for all matched queries
		if (found == true) {
			// The next 3 bytes will be the address where the linked list of addresses are stores
			startOffset = endOffset;
			endOffset = startOffset + (1*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			nodePage = bc.binaryToInt(tempBinary);
			
			startOffset = endOffset;
			endOffset = startOffset + (2*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			nodeOffset = bc.binaryToInt(tempBinary);
			
			// The linked lists first page is how many entries are in the linked list
			tempPage = pages.get(nodePage-1);
			startOffset = nodeOffset;
			endOffset = startOffset + (1*8);
			tempBinary = tempPage.substring(startOffset, endOffset);
			int size = bc.binaryToInt(tempBinary);
			
			// Iterates through all the address stored by the linked list
			for (int j = 0; j < size; ++j) {
				// Gets the address in the linked list
				startOffset = endOffset;
				endOffset = startOffset + (1*8);
				tempBinary = tempPage.substring(startOffset, endOffset);
				nodePage = bc.binaryToInt(tempBinary);
				
				startOffset = endOffset;
				endOffset = startOffset + (2*8);
				tempBinary = tempPage.substring(startOffset, endOffset);
				nodeOffset = bc.binaryToInt(tempBinary);
				
				// Iterates through pages in the heap file then stores the needed page
				// Stores the offset into the page for the relevant record
				String heapPage = "";
				reader = new Reader("./heap." + pageSize);
				for (int k = 0; k < nodePage; ++k) heapPage = reader.readToBytes(pageSize);
				validPages.add(heapPage);
				validRecords.add(nodeOffset);
			}
		}
		
		// Prints all records matching the query
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
