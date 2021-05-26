
public abstract class treeload {
	
	private final static int fanout = 20;
	
	public static void main(String[] args) {
		if (args.length == 1) {
			BPTree bpt = new BPTree();
			int pageSize = Integer.parseInt(args[0]);
			bpt.bulkInsert(pageSize, 8, fanout);
			bpt.writeTree(pageSize);
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
