import java.io.*;

// Class to read data
public class Reader {
	private String filePath;
	private FileReader reader;
	private BufferedReader br;
	private FileInputStream byteReader;
	
	public Reader(String filePath) {
		this.filePath = filePath;
		
		try {
			this.reader = new FileReader(new File(this.filePath));
			br = new BufferedReader(this.reader);
			
			this.byteReader = new FileInputStream(new File(this.filePath));
		} catch (IOException e) {
			System.out.println("Can not Open File");
		}
	}
	
	public String readNextLine() {
		String retVal = "";
		
		try {
			retVal = br.readLine();
		} catch (IOException e) {
			System.out.println("File not open");
		}
		
		return retVal;
	}
	
	// EDIT FROM ASSIGNMENT 1
	// Messy way to ensure that previous assignment 1 code still works without major refactoring
	// As the binary file is now written in real binary and bytes, a quick fix is to change the byte data into a binary string 
	public String readToBytes(int readSize) {
		String retVal = "";
		byte[] readBuffer = new byte[readSize];
		
		int empty = 0;
		try {
			empty = byteReader.read(readBuffer) ;
		} catch (IOException e) {
			System.out.println("File not open");
		}
		if (empty != -1) {
				for (byte readByte : readBuffer) {
					retVal += String.format("%8s", Integer.toBinaryString(readByte & 0xFF)).replace(' ', '0');
				}
		};

		return retVal;
	}
	
	public void closeFile() {
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("File not open");
		}
	}
}
