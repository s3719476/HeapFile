
public class Address {
	private int offset;
	private int page;
	private Address nextAddress = null;
	private Address previousAddress = null;
	
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
	
	
}
