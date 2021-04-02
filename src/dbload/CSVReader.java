package dbload;

import java.io.*;

public class CSVReader {
	private String filePath;
	private FileReader reader;
	private BufferedReader br;
	
	public CSVReader(String filePath) {
		this.filePath = filePath;
		
		try {
			this.reader = new FileReader(new File(this.filePath));
			br = new BufferedReader(this.reader);
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
	
	public String readToBytes(int readSize) {
		String retVal = "";
		char[] charString = new char[readSize*8];
		
		int empty = 0;
		try {
			empty = reader.read(charString, 0, readSize*8) ;
		} catch (IOException e) {
			System.out.println("File not open");
		}
		if (empty != -1) retVal = new String(charString);

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
