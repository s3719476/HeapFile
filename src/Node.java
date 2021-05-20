
public abstract class Node {
	protected static ByteConverter bc = ByteConverter.getInstance();
	private BinaryTreeFileWriter btfw = BinaryTreeFileWriter.getInstance();
	private Address myLocation;
	private int size;
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	private static int fanout = 0;
	
	public abstract void insert(KRid entry);
	
	public abstract NodeKeySplit split();
	
	public abstract String getBinary();
	
	public abstract void print(int level);
	
	public abstract void writeKRid();
	
	public abstract void writeDataNodes();
	
	public abstract void writeIndexNodes();
	
	public abstract int getBytesSize();
	
	public void setFanout(int fanout) {
		Node.fanout = fanout;
	}
	
	public int getFanout() {
		return fanout;
	}
	
	public Address getLocation() {
		return myLocation;
	}
	
	public void setLocation(Address myLocation) {
		this.myLocation = myLocation;
	}
	
}
