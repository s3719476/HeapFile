import java.util.Vector;

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
		pageAmount = 1;
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
	
	public Address insertFront(String binary) {
		int binaryByteSize = binary.length() / 8;
		
		checkSpaceWrite(binaryByteSize);
		
		int offsetLocation = currentFrontPageBinary.length();
		
		currentFrontPageBinary += binary;
		freeByteSpace -= binaryByteSize;
		
		return new Address(pageAmount, offsetLocation);
	}
	
	public Address insertBack(String binary) {
		int binaryByteSize = binary.length() / 8;
		
		checkSpaceWrite(binaryByteSize);
		
		int offsetLocation = ((pageSize*8) - currentBackPageBinary.length() - binary.length());
		
		currentBackPageBinary = binary + currentBackPageBinary;
		freeByteSpace -= binaryByteSize;
		
		return new Address(pageAmount, offsetLocation);
	}
	
	private void checkSpaceWrite(int bytesToTake) {
		if (bytesToTake > freeByteSpace) {
			storeCurrentPage();
			++pageAmount;
		}
	}
	
	private void storeCurrentPage() {
		String data = currentFrontPageBinary;
		data +=	new String(new char[freeByteSpace*8]).replace("\0", "0");
		data += currentBackPageBinary;
		
		pages.add(data);
		freeByteSpace = pageSize;
		currentFrontPageBinary = "";
		currentBackPageBinary = "";
	}
	
	public void completePages(String binary) {
		storeCurrentPage();
		pages.set(0, binary + pages.get(0).substring(binary.length()));
	}
	
	public void insertRootSpace(int rootBytes) {
		freeByteSpace -= rootBytes;
		currentFrontPageBinary += new String(new char[rootBytes*8]).replace("\0", "0");
	}
	
	public void writeAllPages() {
		for (String page : pages) {
			writer.writeData(page);
		}
	}
	
	public int getPageAmount() {
		return pageAmount;
	}
	
	public int getFrontFreeSpaceLocation() {
		return currentFrontPageBinary.length();
	}
}
