
public class BPTree {
	private Node root = null;
	
	public BPTree(int fanout) {
		root.setFanout(fanout);
	}
	
	public void insert(KRid entry) {
		root.insert(entry);
	}
	
}
