import java.util.Vector;

// Singleton class to organise writing to a binary file
public class BinaryTreeFileWriter {
	private static BinaryTreeFileWriter INSTANCE = null;
	private int freeByteSpace;
	private String currentFrontPageBinary;
	private String currentBackPageBinary;
	private Writer writer;
	private int pageSize;
	private int pageAmount;
	private Vector<String> pages = new Vector<String>();
	
	private BinaryTreeFileWriter() {
		currentFrontPageBinary = "";
		currentBackPageBinary = "";
		pageAmount = 0;
	}
	
	public static BinaryTreeFileWriter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BinaryTreeFileWriter();
		}
		
		return INSTANCE;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		this.freeByteSpace = pageSize;
		this.writer = new Writer("./tree." + pageSize);
	}
	
	// Inserts the binary at the start of the binary file
	public Address insertFront(String binary) {
		int binaryByteSize = binary.length() / 8;
		
		checkSpaceWrite(binaryByteSize);
		
		int offsetLocation = currentFrontPageBinary.length();
		
		currentFrontPageBinary += binary;
		freeByteSpace -= binaryByteSize;
		
		return new Address(pageAmount, offsetLocation);
	}
	
	// Inserts the binary at the back of the binary file
	public Address insertBack(String binary) {
		int binaryByteSize = binary.length() / 8;
		
		checkSpaceWrite(binaryByteSize);
		
		int offsetLocation = ((pageSize*8) - currentBackPageBinary.length() - binary.length());
		
		currentBackPageBinary = binary + currentBackPageBinary;
		freeByteSpace -= binaryByteSize;
		
		return new Address(pageAmount, offsetLocation);
	}
	
	// Checks if the amount of bytes is large enough and if not then store that page to get a new page
	private void checkSpaceWrite(int bytesToTake) {
		if (bytesToTake > freeByteSpace) {
			storeCurrentPage();
			++pageAmount;
		}
	}
	
	// Stores the page for later bulk writing to a binary file
	private void storeCurrentPage() {
		String data = currentFrontPageBinary;
		data +=	new String(new char[freeByteSpace*8]).replace("\0", "0");
		data += currentBackPageBinary;
		
		pages.add(data);
		freeByteSpace = pageSize;
		currentFrontPageBinary = "";
		currentBackPageBinary = "";
	}
	
	// Shortcut to store the current page for later bulk writing
	public void completePages(String binary) {
		storeCurrentPage();
		pages.set(0, binary + pages.get(0).substring(binary.length()));
	}
	
	// Inserts space at the start of the page
	public void insertRootSpace(int rootBytes) {
		freeByteSpace -= rootBytes;
		currentFrontPageBinary += new String(new char[rootBytes*8]).replace("\0", "0");
	}
	
	// Bulk write all the pages
	public void writeAllPages() {
		int count = 0;
		
		for (String page : pages) {
//			System.out.println("PRINTING PAGE: " + count + " " + page.length());
//			System.out.println(page);
			writer.writeData(page);
			++count;
		}
	}
	
	public int getPageAmount() {
		return pageAmount;
	}
	
	public int getFrontFreeSpaceLocation() {
		return currentFrontPageBinary.length();
	}
}
