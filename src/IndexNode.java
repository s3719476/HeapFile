import java.util.Vector;

// Index nodes used to traverse to the data nodes then
public class IndexNode extends Node{
	private Vector<Integer> keys = new Vector<Integer>();
	private Vector<Node> branches = new Vector<Node>();
	private BinaryTreeFileWriter btfw = BinaryTreeFileWriter.getInstance();
	
	public IndexNode(Vector<Integer> splitKeys, Vector<Node> splitBranches) {
		this.keys = splitKeys;
		this.branches = splitBranches;
		setSize(0);
	}
	
	public void insert(KRid entry) {
		int currKeyIdx = 0;
		boolean found = false;
		
		// Iterates through the keys to find the applicable key
		while (found == false && currKeyIdx < keys.size()) {
			if (entry.getKey() < keys.get(currKeyIdx)) found = true;
			else ++currKeyIdx;
		}
		
		// Calls insert of the next branch relative to the found key
		Node branch = branches.get(currKeyIdx);
		branch.insert(entry);
		setSize(getSize() + 1);
		
		// If the insert resulted in the relevant branch becoming larger than the fanout
		// Split the node
		if (branch.getSize() > getFanout()) {
			// Splits the node which returns the new node created after the split
			// Then adds the split node to this index node and adds the relevant key
			NodeKeySplit split = branch.split();
			keys.add(currKeyIdx, split.getKey());
			branches.add(currKeyIdx+1, split.getNode());
		}
	}
	
	public NodeKeySplit split() {
		// Algorithm to find where to split this node
		// This is to ensure that the values split are not seperated poorly to create an incorrect B+Tree
		boolean foundSplitPoint = false;
		int splitPoint = (int)Math.ceil(getFanout()/2);
		
		while (foundSplitPoint == false && splitPoint > 0) {
			if (keys.get(splitPoint) != keys.get(splitPoint-1)) foundSplitPoint = true;
			else --splitPoint;
		}
		if (foundSplitPoint == false) splitPoint = (int)Math.ceil(getFanout()/2);
		
		// Obtains the new key that the above node will use to point to the old and split nodes
		int newKey = keys.get(splitPoint);
		keys.remove(splitPoint);
		
		// Seperates the keys and branches
		Vector<Integer> splitKeys = new Vector<Integer>();
		for (int i = keys.size()-1; i >= splitPoint; --i) {
			splitKeys.add(0, keys.get(i));
			keys.remove(keys.size()-1);
			setSize(getSize() - 1);
		}
		
		Vector<Node> splitBranches = new Vector<Node>();
		for (int i = branches.size()-1; i > splitPoint; --i) {
			splitBranches.add(0, branches.get(i));
			branches.remove(branches.size()-1);
		}
		
		// Returns a new node with the split branches and keys as well as the key which the parent node will use to point to this new node
		return new NodeKeySplit(new IndexNode(splitKeys, splitBranches), newKey);
	}
	
	public void print(int level) {
		System.out.print("LEVEL " + level + ": ");
		for (int key : keys) System.out.print("|" + key + "|");
		System.out.println();
		
		for (Node branch : branches) branch.print(level+1); 
	}
	
	public String getBinary() {
		String binary = "";
		
		binary += bc.intToBinaryStringToByteSize(getSize(), 1);
		
		for (int i = 0; i < getSize(); ++i) {
			binary += branches.get(i).getLocation().getBinary();
			binary += bc.intToBinaryStringToByteSize(keys.get(i), 3);
		}
		binary += branches.lastElement().getLocation().getBinary();
		
		return binary;
	}
	
	// Iterates through all the children telling them to write the linked lists
	public void writeKRid() {
		for (Node branch : branches) branch.writeKRid();
	}
	
	// Iterates throguh all the children telling them to write the data nodes
	public void writeDataNodes() {
		for (Node branch : branches) branch.writeDataNodes();
	}
	
	// Iterates through all the children telling them to write the index nodes
	public void writeIndexNodes() {
		for (Node branch : branches) branch.writeIndexNodes();
		
		// After all its children have wrote the data nodes which sets there address location on the binary file
		// Write this index nodes location to the binary file
		setLocation(btfw.insertFront(getBinary()));
	}
	
	public int getSize() {
		return keys.size();
	}
	
	public int getBytesSize() {
		int bytesSize = 
				1 +
				(branches.size() * 3) +
				(keys.size() * 3);
		return bytesSize;
	}
}