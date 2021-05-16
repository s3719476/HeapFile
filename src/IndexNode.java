import java.util.Vector;

public class IndexNode extends Node{
	private Vector<Integer> keys;
	private Vector<Node> branches;
	
	public IndexNode(Vector<Integer> splitKeys, Vector<Node> splitBranches) {
		this.keys = splitKeys;
		this.branches = splitBranches;
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
		
		if (branch.getSize() > getFanout()) {
			NodeKeySplit split = branch.split();
			keys.add(currKeyIdx, split.getKey());
			branches.add(currKeyIdx, split.getNode());
		}
	}
	
	public NodeKeySplit split() {
		int splitPoint = (int)Math.ceil(getFanout()/2);
		
		int newKey = keys.get(splitPoint);
		keys.remove(splitPoint);
		
		Vector<Integer> splitKeys = new Vector<Integer>();
		for (int i = keys.size()-1; i >= splitPoint; ++i) {
			splitKeys.add(0, keys.get(i));
			keys.remove(keys.size()-1);
		}
		
		Vector<Node> splitBranches = new Vector<Node>();
		for (int i = branches.size()-1; i > splitPoint; ++i) {
			splitBranches.add(0, branches.get(i));
			branches.remove(branches.size()-1);
		}
		
		return new NodeKeySplit(new IndexNode(splitKeys, splitBranches), newKey);
	}
	
	public String getBinary() {
		return null;
	}
	
	public int getSize() {
		return keys.size();
	}
}