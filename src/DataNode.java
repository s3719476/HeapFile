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
		
		if (entries.size() > super.getFanout()) split();
	}
	
	private void split() {
		Vector<KRid> splitEntries = new Vector<KRid>();
		for (int i = (int)Math.ceil(getFanout()/2); i < entries.size(); ++i) splitEntries.add(entries.get(i));
		
		DataNode newNode = new DataNode(splitEntries);
		
		nextNode.getNextNode().setPrevNode(newNode);
		nextNode = newNode;
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
