
public abstract class Node {
	private static int fanout;
	
	public abstract void insert(KRid entry);
	
	public abstract NodeKeySplit split();
	
	public abstract int getSize();
	
	public abstract String getBinary();
	
	public void setFanout(int fanout) {
		Node.fanout = fanout;
	}
	
	public int getFanout() {
		return fanout;
	}
}
