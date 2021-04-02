import java.io.*;

// Class used to write data
public class Writer {

	private String filePath;
	private FileWriter writer;
	
	public Writer(String filePath) {
		this.filePath = filePath;
		
		try {
			this.writer = new FileWriter(new File(this.filePath));
		} catch (IOException e) {
			System.out.println("Can not Open File");
		}
	}
	
	public void writeData(String data) {
		try {
			writer.flush();
			writer.write(data);
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
