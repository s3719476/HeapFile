package dbload;

import java.io.*;

public class DataLoader {

	private String filePath;
	private FileWriter writer;
	
	public DataLoader(String filePath) {
		this.filePath = filePath;
		
		try {
			this.writer = new FileWriter(new File(this.filePath));
		} catch (IOException e) {
			System.out.println("Can not Open File");
		}
	}
	
	public void writeData(String data) {
		try {
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
