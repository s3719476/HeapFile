package dbload;

public class Main {

	public static void main(String[] args) {
		HeapFileOrganiser hfo = new HeapFileOrganiser();
		hfo.loadAllData();
		hfo.recordsToPages();
		hfo.writeAllEntries();
		hfo.closeEverything();
		hfo.search();
	}

}
