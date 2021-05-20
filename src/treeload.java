
public abstract class treeload {
	
	private final static int fanout = 3;
	
	public static void main(String[] args) {
		if (args.length == 1) {
			BPTree bpt = new BPTree();
			bpt.bulkInsert(Integer.parseInt(args[0]), 8, fanout);
			bpt.writeTree(Integer.parseInt(args[0]));
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
