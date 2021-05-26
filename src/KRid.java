// Data class to wrap the data addresses to the heap and their relevant keys
// Essentially the linked list holding the data addresses
public class KRid {
	private int key;	// 3 bytes
	private Address dataAddress;	// 6 bytes
	private int addressAmount;
	private Address myLocation = null;
	private BinaryTreeFileWriter btfw = BinaryTreeFileWriter.getInstance();
	private ByteConverter bc = ByteConverter.getInstance();
	
	public KRid(int key, Address dataAddress) {
		this.key = key;
		this.dataAddress = dataAddress;
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
		String binary = bc.intToBinaryStringToByteSize(addressAmount, 1);

		binary += bc.intToBinaryStringToByteSize(dataAddress.getMyLocation().getPage(), 1);
		binary += bc.intToBinaryStringToByteSize(dataAddress.getMyLocation().getOffset(), 2);
		
		return binary;
	}
	
	// Appends the address parameter to the end of the linked list
	public void addAddress(Address newAddress) {
		// Traverses through the addresses of the linked list until the next address is null (end of list)
		Address currentAddress = this.dataAddress;
		while (currentAddress.getNextAddress() != null) {
			currentAddress = currentAddress.getNextAddress();
		}
		
		// Adds the address to the end of the list and sorts the prev and next address to maintain doubly linked list structure
		newAddress.setPreviousAddress(currentAddress);
		currentAddress.setNextAddress(newAddress);
		
		++addressAmount;
	}
	
	public void writeKRid() {
		dataAddress.writeDataAddress();
		
		myLocation = btfw.insertBack(getBinary());
	}
	
	public Address getLocation() {
		return myLocation;
	}
}