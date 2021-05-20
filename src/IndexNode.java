import java.util.Vector;

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
		
		while (found == false && currKeyIdx < keys.size()) {
			if (entry.getKey() < keys.get(currKeyIdx)) found = true;
			else ++currKeyIdx;
		}
		
		Node branch = branches.get(currKeyIdx);
		branch.insert(entry);
		setSize(getSize() + 1);
		
		if (branch.getSize() > getFanout()) {
			NodeKeySplit split = branch.split();
			keys.add(currKeyIdx, split.getKey());
			branches.add(currKeyIdx+1, split.getNode());
		}
	}
	
	public NodeKeySplit split() {
		boolean foundSplitPoint = false;
		int splitPoint = (int)Math.ceil(getFanout()/2);
		
		while (foundSplitPoint == false && splitPoint > 0) {
			if (keys.get(splitPoint) != keys.get(splitPoint-1)) foundSplitPoint = true;
			else --splitPoint;
		}
		if (foundSplitPoint == false) splitPoint = (int)Math.ceil(getFanout()/2);
		
		int newKey = keys.get(splitPoint);
		keys.remove(splitPoint);
		
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
	
	public void writeKRid() {
		for (Node branch : branches) branch.writeKRid();
	}
	
	public void writeDataNodes() {
		for (Node branch : branches) branch.writeDataNodes();
	}
	
	public void writeIndexNodes() {
		for (Node branch : branches) branch.writeIndexNodes();
		
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