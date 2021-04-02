
public class dbquery {

	public static void main(String[] args) {
		if (args.length == 2) {
			HeapFileOrganiser hfo = new HeapFileOrganiser();
			// Change last parameter to change the field to search
			hfo.search(Integer.parseInt(args[1]), args[0], 1);
		} else {
			System.out.println("Incorrect Arguments");
		}
	}

}
