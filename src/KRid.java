// Data class to wrap the data addresses to the heap and their relevant keys
// Essentially the linked list holding the data addresses
public class KRid {
	private int key;	// 3 bytes
	private Address dataAddress;	// 6 bytes
	private Address lastAddress;	// Store last node in linked list for efficiency purposes when adding a new entry to the end of the linked list so no iteration is needed to reach the end
	private int addressAmount;
	private Address myLocation = null;
	private BinaryTreeFileWriter btfw = BinaryTreeFileWriter.getInstance();
	private ByteConverter bc = ByteConverter.getInstance();
	
	public KRid(int key, Address dataAddress) {
		this.key = key;
		this.dataAddress = dataAddress;
		this.lastAddress = dataAddress;
		this.addressAmount = 1;
	}
	
	public int getKey() {
		return key;
	}
	
	public Address getDataAddress() {
		return dataAddress;
	}
	
	public int getAddressAmount() {
		return addressAmount;
	}
	
	public String getBinary() {
		String binary = bc.intToBinaryStringToByteSize(addressAmount, 3);

		binary += bc.intToBinaryStringToByteSize(dataAddress.getMyLocation().getPage(), 3);
		binary += bc.intToBinaryStringToByteSize(dataAddress.getMyLocation().getOffset(), 3);
		
		return binary;
	}
	
	// Appends the address parameter to the end of the linked list
	public void addAddress(Address newAddress) {
		// Adds the address to the end of the list and sorts the prev and next address to maintain doubly linked list structure
		newAddress.setPreviousAddress(lastAddress);
		lastAddress.setNextAddress(newAddress);
		lastAddress = newAddress;
		
		++addressAmount;
	}
	
	public void writeKRid() {
		Address llDataAddress = lastAddress;
		
		for (int i = addressAmount; i > 0; --i) {
			llDataAddress.setMyLocation(btfw.insertBack(llDataAddress.getLLNodeBinary()));
			llDataAddress = llDataAddress.getPreviousAddress();
		}
		
		myLocation = btfw.insertBack(getBinary());
	}
	
	public Address getLocation() {
		return myLocation;
	}
}