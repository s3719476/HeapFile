
public class dbload {

	public static void main(String[] args) {
		if (args[0].equals("-p") && args.length == 3) {
			HeapFileOrganiser hfo = new HeapFileOrganiser();
			hfo.loadDataToHeap(Integer.parseInt(args[1]), args[2]);
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
