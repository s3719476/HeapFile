import java.util.Vector;

public class DataNode extends Node {
	private DataNode prevNode = null;
	private DataNode nextNode = null;
	private Vector<KRid> entries = new Vector<KRid>();
	private BinaryTreeFileWriter btfw = BinaryTreeFileWriter.getInstance();
	
	public DataNode(Vector<KRid> splitEntries) {
		this.entries = splitEntries;
		setSize(0);
	}
	
	public DataNode() {}
	
	public void insert(KRid entry) {
		// Iterates through the data node finding where the entry should belong
		// This determines whether an exact match is found
		// or if just its position is found meaning the data has a key that is not stored yet
		boolean foundPosition = false;
		boolean foundMatch = false;
		int currEntryIdx = 0;
		while (foundPosition == false && currEntryIdx < entries.size()) {
			int insertKey = entry.getKey();
			int compareKey = entries.get(currEntryIdx).getKey();
			if (insertKey == compareKey) {
				foundPosition = true;
				foundMatch = true;
			}
			else if (insertKey < compareKey) foundPosition = true;
			else ++currEntryIdx;
		}
		
		// If an exact match is not found then add the new entry and key to the data node
		// but if an exact match is found then add the new entry to the pre existing key
		if (foundMatch == false) entries.add(currEntryIdx, entry);
		else entries.get(currEntryIdx).addAddress(entry.getDataAddress());
		setSize(getSize() + 1);
		
	}
	
	public NodeKeySplit split() {
		// Iterate through the keys to find the the appropriate point to split the node
		boolean foundSplitPoint = false;
		int splitPoint = (int)Math.ceil(getFanout()/2);
		
		while (foundSplitPoint == false && splitPoint > 0) {
			if (entries.get(splitPoint).getKey() != entries.get(splitPoint-1).getKey()) foundSplitPoint = true;
			else --splitPoint;
		}
		if (foundSplitPoint == false) splitPoint = (int)Math.ceil(getFanout()/2);
		
		// Splits this nodes vector into two vectors using the point to split
		Vector<KRid> splitEntries = new Vector<KRid>();
		for (int i = entries.size()-1; i >= splitPoint; --i) {
			splitEntries.add(0, entries.get(i));
			entries.remove(entries.size()-1);
			setSize(getSize() - 1);
		}
		
		// Creates a new node from the new split vector then assigns the prev and next nodes to maintain doubly linked list usage
		DataNode newNode = new DataNode(splitEntries);
		
		if (nextNode != null) {
			nextNode.setPrevNode(newNode);
		}
		newNode.setPrevNode(this);
		nextNode = newNode;
		
		// Returns the node and key needed for the above index node to use to point to the new split node
		return new NodeKeySplit(newNode, splitEntries.get(0).getKey());
	}
	
	public void print(int level) {
		System.out.print("--DATA: ");
		for(KRid entry: entries) {
			System.out.print("|" + entry.getKey());
			int addressCount = 1;
			Address address = entry.getDataAddress();
			while (address.getNextAddress() != null) {
				++addressCount;
				address = address.getNextAddress();
			}
			
			System.out.print("(" + addressCount + ")|");
		}
		System.out.println();
	}
	
	public String getBinary() {
		String binary = "";
		
		binary += bc.intToBinaryStringToByteSize(getSize(), 1);
		
		for (KRid entry : entries) {
			binary += bc.intToBinaryStringToByteSize(entry.getKey(), 3);
			binary += entry.getLocation().getBinary();
		}
		
		if (prevNode == null) binary += new Address(0, 0).getBinary();
		else binary += prevNode.getLocation().getBinary();
		
		if (nextNode == null) binary += new Address(0, 0).getBinary();
		else binary += new Address(btfw.getPageAmount(), (btfw.getFrontFreeSpaceLocation() + binary.length() + (3*8))).getBinary();
		
		return binary;
	}
	
	public int getSize() {
		return entries.size();
	}
	
	public void writeKRid() {
		for (KRid entry : entries) entry.writeKRid();
	}
	
	public void writeDataNodes() {
		setLocation(btfw.insertFront(getBinary()));
	}
	
	public void writeIndexNodes() {};
	
	public int getBytesSize() {
		int bytesSize = 
				1 +
				(entries.size() * 3) +
				(entries.size() * 3) +
				3 +
				3;
		return bytesSize;
	}
	
	public DataNode getPrevNode() {
		return prevNode;
	}
	
	public DataNode getNextNode() {
		return nextNode;
	}
	
	public void setPrevNode(DataNode prevNode) {
		this.prevNode = prevNode;
	}
	
	public void setNextNode(DataNode nextNode) {
		this.nextNode = nextNode;
	}
}
