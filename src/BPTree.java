import java.util.Vector;

public class BPTree {
	private Node root = null;
	
	public BPTree(int fanout) {
		root.setFanout(fanout);
	}
	
	public void insert(KRid entry) {
		if (root == null) root = new DataNode();
		
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
	
}
