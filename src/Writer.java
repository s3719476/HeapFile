import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

// Class used to write data
public class Writer {

	private String filePath;
	private FileOutputStream writer;
	private ByteConverter bc = ByteConverter.getInstance();
	
	public Writer(String filePath) {
		this.filePath = filePath;
		
		try {
			this.writer = new FileOutputStream(new File(this.filePath));
		} catch (IOException e) {
			System.out.println("Can not Open File");
		}
	}
	
	// EDIT FROM ASSIGNMENT 1
	// Messy way to ensure that previous assignment 1 code still works without major refactoring
	// Translate the binary string data into bytes to write to the binary file
	public void writeData(String data) {
		try {
			writer.flush();
			for (String splitData : data.split("(?<=\\G.{8})")) {
				byte bval = (byte) Integer.parseInt(splitData, 2);

				writer.write(bval);
			}
		} catch (IOException e) {
			System.out.println("File not open");
		}
	}
	
	public void closeFile() {
		try {
			writer.close();
		} catch (IOException e) {
			System.out.println("File not open");
		}
	}
	
}
