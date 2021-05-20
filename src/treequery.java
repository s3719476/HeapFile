
public class treequery {

	public static void main(String[] args) {
		if (args.length == 2) {
			BPTree bpt = new BPTree();
			bpt.queryTree(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
