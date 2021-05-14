
public abstract class Node {
	private static int fanout;
	
	public abstract void insert(KRid entry);
	
	public void setFanout(int fanout) {
		Node.fanout = fanout;
	}
	
	public int getFanout() {
		return fanout;
	}
}
