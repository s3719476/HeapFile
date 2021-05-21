// Address ckass used as a node for the doubly linked list to address data
public class Address {
	private int offset;	// 2 byte
	private int page;	//1 byte
	private Address nextAddress = null;
	private Address previousAddress = null;
	private ByteConverter bc = ByteConverter.getInstance();
	
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
		
		binary += bc.intToBinaryStringToByteSize(page, 1);
		binary += bc.intToBinaryStringToByteSize(offset, 2);
		
		return binary;
	}
	
}
