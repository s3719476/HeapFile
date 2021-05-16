
public abstract class Node {
	private static int fanout = 0;
	
	public abstract void insert(KRid entry);
	
	public abstract NodeKeySplit split();
	
	public abstract int getSize();
	
	public abstract String getBinary();
	
	public abstract void print(int level);
	
	public void setFanout(int fanout) {
		Node.fanout = fanout;
	}
	
	public int getFanout() {
		return fanout;
	}
}
