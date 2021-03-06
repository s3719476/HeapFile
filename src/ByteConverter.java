// Helper class to do binary conversions and byte calculations
public class ByteConverter {
	private static ByteConverter INSTANCE = null;
	
	private ByteConverter() {}
	
	public String intToBinaryStringToNearestByteSize(int value) {
		String binary = intToBinaryString(value);
		String binaryWithByteSize = String.format(
				"%" + getIntToNearestMultiple(8, binary.length()) + "s"
				, binary). replaceAll(" ", "0");
		
		return binaryWithByteSize;
	}
	
	public String intToBinaryStringToByteSize(int value, int byteSize) {
		String binary = intToBinaryString(value);
		
		String binaryWithByteSize = String.format(
				"%" + byteSize*8 + "s"
				, binary).replaceAll(" ", "0");
		
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
	
	private int getIntToNearestMultiple(int multiple, int value) {
		return (value + (multiple-1)) / multiple * multiple;
	}
	  
	public int getNumberOfBytes(String binary) {
		return binary.length()/8;
	}
	
	public int getNumberOfBytesToAllowValue(int number) {
		int result = (int) Math.ceil((int)(Math.log(number) / Math.log(2)));
		
		return getIntToNearestMultiple(8, result)/8;
	}
	
	public int binaryToInt(String binary) {
		return Integer.parseInt(binary, 2);
	}
	
	public String binaryToString(String binary) {
		String value = "";
		
		String[] charBinaryArray = binary.split("(?<=\\G.{8})");
		
		for (String charBinary : charBinaryArray) {
			value += new Character((char)Integer.parseInt(charBinary, 2)).toString();
		}
		
		return value;
	}
	
	public byte binaryStringToByte(String binary) {
		return Byte.parseByte(binary, 2);
	}

	public static ByteConverter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ByteConverter();
		}
		
		return INSTANCE;
	}
}