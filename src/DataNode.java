import java.util.Vector;

public class DataNode extends Node {
	private DataNode prevNode = null;
	private DataNode nextNode = null;
	private Vector<KRid> entries;
	
	public DataNode(Vector<KRid> splitEntries) {
		this.entries = splitEntries;
	}
	
	public DataNode() {}
	
	public void insert(KRid entry) {
		boolean found = false;
		int currEntryIdx = 0;
		while (found == false && currEntryIdx < entries.size()) {
			if (entry.getKey() < entries.get(currEntryIdx).getKey()) found = true;
		}
		
		entries.add(currEntryIdx, entry);
	}
	
	public NodeKeySplit split() {
		int splitPoint = (int)Math.ceil(getFanout()/2);
		
		Vector<KRid> splitEntries = new Vector<KRid>();
		for (int i = entries.size()-1; i >= splitPoint; ++i) {
			splitEntries.add(0, entries.get(i));
			entries.remove(entries.size()-1);
		}
		
		DataNode newNode = new DataNode(splitEntries);
		
		nextNode.getNextNode().setPrevNode(newNode);
		nextNode = newNode;
		
		return new NodeKeySplit(newNode, splitEntries.get(0).getKey());
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
