// Address ckass used as a node for the doubly linked list to address data
public class Address {
	private int offset;	// 2 byte
	private int page;	//2 byte
	private Address nextAddress = null;
	private Address previousAddress = null;
	private Address myLocation = null;
	private ByteConverter bc = ByteConverter.getInstance();
	private BinaryTreeFileWriter btfw = BinaryTreeFileWriter.getInstance();
	
	public Address(int page, int offset) {
		this.offset = offset;
		this.page = page;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getPage() {
		return page;
	}
	
	public Address getNextAddress() {
		return nextAddress;
	}
	
	public void setNextAddress(Address nextAddress) {
		this.nextAddress = nextAddress;
	}
	
	public Address getPreviousAddress() {
		return previousAddress;
	}
	
	public void setPreviousAddress(Address previousAddress) {
		this.previousAddress = previousAddress;
	}
	
	public String getBinary() {
		String binary = "";
		
		binary += bc.intToBinaryStringToByteSize(page, 3);
		binary += bc.intToBinaryStringToByteSize(offset, 3);
		
		return binary;
	}
	
	public void writeDataAddress() {
		if (nextAddress != null) nextAddress.writeDataAddress();
		
		myLocation = btfw.insertBack(getLLNodeBinary());
	}
	
	public String getLLNodeBinary() {
		String binary = getBinary();
		
		if (nextAddress == null) binary += bc.intToBinaryStringToByteSize(0, 6);
		else {
			Address nextAddressLocation = nextAddress.getMyLocation();
			binary += bc.intToBinaryStringToByteSize(nextAddressLocation.getPage(), 3);
			binary += bc.intToBinaryStringToByteSize(nextAddressLocation.getOffset(), 3);
		}

		return binary;
	}
	
	public Address getMyLocation() {
		return myLocation;
	}
	
	public void setMyLocation(Address myLocation) {
		this.myLocation = myLocation;
	}
	
}
