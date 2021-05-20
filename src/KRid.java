
public class KRid {
	private int key;	// 3 bytes
	private Address dataAddress;	// 3 bytes
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
		
		Address currentAddress = dataAddress;
		while (currentAddress != null) {
			binary += currentAddress.getBinary();
			
			currentAddress = currentAddress.getNextAddress();
		}
		
		return binary;
	}
	
	public void addAddress(Address newAddress) {
		Address currentAddress = this.dataAddress;
		while (currentAddress.getNextAddress() != null) {
			currentAddress = currentAddress.getNextAddress();
		}
		
		newAddress.setPreviousAddress(currentAddress);
		currentAddress.setNextAddress(newAddress);
		
		++addressAmount;
	}
	
	public void writeKRid() {
		myLocation = btfw.insertBack(getBinary());
	}
	
	public Address getLocation() {
		return myLocation;
	}
}