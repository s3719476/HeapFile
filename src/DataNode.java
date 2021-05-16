import java.util.Vector;

public class DataNode extends Node {
	private DataNode prevNode = null;
	private DataNode nextNode = null;
	private Vector<KRid> entries = new Vector<KRid>();
	
	public DataNode(Vector<KRid> splitEntries) {
		this.entries = splitEntries;
	}
	
	public DataNode() {}
	
	public void insert(KRid entry) {
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
		
		if (foundMatch == false) entries.add(currEntryIdx, entry);
		else entries.get(currEntryIdx).addAddress(entry.getAddress());
	}
	
	public NodeKeySplit split() {
		boolean foundSplitPoint = false;
		int splitPoint = (int)Math.ceil(getFanout()/2);
		
		while (foundSplitPoint == false && splitPoint > 0) {
			if (entries.get(splitPoint).getKey() != entries.get(splitPoint-1).getKey()) foundSplitPoint = true;
			else --splitPoint;
		}
		if (foundSplitPoint == false) splitPoint = (int)Math.ceil(getFanout()/2);
		
		Vector<KRid> splitEntries = new Vector<KRid>();
		for (int i = entries.size()-1; i >= splitPoint; --i) {
			splitEntries.add(0, entries.get(i));
			entries.remove(entries.size()-1);
		}
		
		DataNode newNode = new DataNode(splitEntries);
		
		if (nextNode != null) {
			nextNode.setPrevNode(newNode);
		}
		newNode.setPrevNode(this);
		nextNode = newNode;
		
		return new NodeKeySplit(newNode, splitEntries.get(0).getKey());
	}
	
	public void print(int level) {
		System.out.print("--DATA: ");
		for(KRid entry: entries) {
			System.out.print("|" + entry.getKey());
			int addressCount = 1;
			Address address = entry.getAddress();
			while (address.getNextAddress() != null) {
				++addressCount;
				address = address.getNextAddress();
			}
			
			System.out.print("(" + addressCount + ")|");
		}
		System.out.println();
	}
	
	public String getBinary() {
		return null;
	}
	
	public int getSize() {
		return entries.size();
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
