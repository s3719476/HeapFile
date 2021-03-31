package dbload;

public class ByteConverter {
	
	public String intToBinaryStringToByteSize(int value, int byteSize) {
		String binary = intToBinaryString(value);
		String binaryWithByteSize = String.format(
				"%" + Integer.toString(byteSize) + "s"
				, binary). replaceAll(" ", "0");
		
		return binaryWithByteSize;
	}
	
	public String intToBinaryString(int value) {
		String binary = Integer.toBinaryString(value);
		
		return binary;
	}
	
	public String stringToBinary(String value) {
		String binary = "";
		
		char[] charArray = value.toCharArray();
		
		for (char character : charArray) {
			binary += String.format("%8s",  Integer.toBinaryString(character)).replaceAll(" ", "0");
		}
		
		return binary;
	}
}
