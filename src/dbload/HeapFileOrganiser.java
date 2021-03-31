package dbload;

import java.util.Vector;

public class HeapFileOrganiser {
	
	final String csvFilePath = "data/Pedestrian_Counting_System_-_Monthly__counts_per_hour_ - Copy.csv";
	final String dlFilePath = "data/file";
	
	private ByteConverter bc;
	private CSVReader csvr;
	private DataLoader dl;
	
	private Vector<Record> recordList = new Vector<Record>();
	
//	private Vector<Pedestrian> pedestrainList = new Vector<Pedestrian>();
//	private Vector<Sensor> sensorList = new Vector<Sensor>();
//	private Vector<Time> timeList = new Vector<Time>();
//	
//	private Vector<Integer> sensorKeys = new Vector<Integer>();
//	private Vector<String> timeKeys = new Vector<String>();
//	private Vector<Integer> pedestrianKeys = new Vector<Integer>();
	
	
	public HeapFileOrganiser() {
		bc = new ByteConverter();
		csvr = new CSVReader(csvFilePath);
		dl = new DataLoader(dlFilePath);
	}
	
	public void loadAllData() {
		int counter = 0;
		
		String line = csvr.readNextLine();
		while (line != null) {
			System.out.println("Loading entry number: " + counter);
			++counter;
			
			storeEntry(line);
			
			line = csvr.readNextLine();
		}
		
	}
	
	private void storeEntry(String data) {
		String[] values = data.split(",");
		
		recordList.add(new Record(
				Integer.parseInt(values[0]),
				values[1],
				Integer.parseInt(values[2]),
				values[3],
				Integer.parseInt(values[4]),
				values[5],
				Integer.parseInt(values[6]),
				Integer.parseInt(values[7]),
				values[8],
				Integer.parseInt(values[9])
						)
				);
	}
	
	public void placeRecord() {
		Record currEntry = recordList.get(0);
		
		String entry = 
				bc.intToBinaryString(currEntry.getID()) +
				bc.intToBinaryString(currEntry.getSensor_ID()) +
				bc.stringToBinary(currEntry.getDate_Time()) +
				bc.intToBinaryString(currEntry.getYear()) +
				bc.stringToBinary(currEntry.getMonth()) +
				bc.intToBinaryString(currEntry.getMdate()) +
				bc.stringToBinary(currEntry.getDay()) +
				bc.intToBinaryString(currEntry.getTime()) +
				bc.stringToBinary(currEntry.getSensor_name()) +
				bc.intToBinaryString(currEntry.getHourly_Counts());
		
		System.out.println(entry);
	}
	
	
}
