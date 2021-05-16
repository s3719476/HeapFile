
public abstract class treeload {
	
	private final static int fanout = 3;
	
	public static void main(String[] args) {
		if (args.length == 1) {
			BPTree bpt = new BPTree(fanout);
			bpt.bulkInsert(Integer.parseInt(args[0]), 8);
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
