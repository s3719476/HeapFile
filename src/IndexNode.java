import java.util.Vector;

public class IndexNode extends Node{
	private Vector<Integer> keys;
	private Vector<Node> branches;
	
	public void insert(KRid entry) {
		int currKeyIdx = 0;
		boolean found = false;
		
		while (found == false && currKeyIdx < keys.size()) {
			if (entry.getKey() < keys.get(currKeyIdx)) found = true;
			else ++currKeyIdx;
		}
		
		branches.get(currKeyIdx).insert(entry);
	}
}
