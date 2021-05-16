
public class KRid {
	private int key;
	private Address address;
	
	public KRid(int key, Address address) {
		this.key = key;
		this.address = address;
	}
	
	public int getKey() {
		return key;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public String getBinary() {
		return null;
	}
	
	public void addAddress(Address newAddress) {
		Address currentAddress = this.address;
		while (currentAddress.getNextAddress() != null) {
			currentAddress = currentAddress.getNextAddress();
		}
		
		newAddress.setPreviousAddress(currentAddress);
		currentAddress.setNextAddress(newAddress);
	}
}
