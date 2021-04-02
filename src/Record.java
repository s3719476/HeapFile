// Class to temporarily hold the data read in from the CSV file
public class Record {
	
	private int ID;
	private String Date_Time;
	private int Year;
	private String Month;
	private int Mdate;
	private String Day;
	private int Time;
	private int Sensor_ID;
	private String Sensor_name;
	private int Hourly_Counts;
	
	private final int numFields = 11;
	
	public Record(int iD, String date_Time, int year, String month, int mdate, String Day, int Time, int sensor_ID,
			String sensor_name, int hourly_Counts) {
		this.ID = iD;
		this.Date_Time = date_Time;
		this.Year = year;
		this.Month = month;
		this.Mdate = mdate;
		this.Day = Day;
		this.Time = Time;
		this.Sensor_ID = sensor_ID;
		this.Sensor_name = sensor_name;
		this.Hourly_Counts = hourly_Counts;
	}

	public int getID() {
		return ID;
	}

	public String getDate_Time() {
		return Date_Time;
	}

	public int getYear() {
		return Year;
	}

	public String getMonth() {
		return Month;
	}

	public int getMdate() {
		return Mdate;
	}

	public String getDay() {
		return Day;
	}

	public int getTime() {
		return Time;
	}

	public int getSensor_ID() {
		return Sensor_ID;
	}

	public String getSensor_name() {
		return Sensor_name;
	}

	public int getHourly_Counts() {
		return Hourly_Counts;
	}
	
	// Method sends back the binary of each attribute of this record
	// At the start also holds the binary of the offsets required to get to each field in the binary
	public String getBinaryWithOffsets() {
		ByteConverter bc = new ByteConverter();
		
		// Gets an array of each field in binary needed to be stored
		String[] recordBinaryDataArray = 
			{
				 bc.intToBinaryStringToNearestByteSize(getID()),
				 bc.stringToBinary(Integer.toString(getSensor_ID()) + getDate_Time()),
				 bc.stringToBinary(getDate_Time()),
				 bc.intToBinaryStringToNearestByteSize(getYear()),
				 bc.stringToBinary(getMonth()),
				 bc.intToBinaryStringToNearestByteSize(getMdate()),
				 bc.stringToBinary(getDay()),
				 bc.intToBinaryStringToNearestByteSize(getTime()),
				 bc.intToBinaryStringToNearestByteSize(getSensor_ID()),
				 bc.stringToBinary(getSensor_name()),
				 bc.intToBinaryStringToNearestByteSize(getHourly_Counts())
			};
		
		String binaryEntry = "";
		String binaryOffset = "";
		
		// For each field it creates an offset entry in the offset section of the binary
		// Sets the starting offset of the first field to where the data starts in the binary
		// Offsets stored are the binary format of how many bytes to go into the record to get to the value
		int previousOffset = numFields + 1;
		for (String binaryData : recordBinaryDataArray) {
			// Appends the entry to the entries
			// Appends the offset of the entry from the start of the record to the offsets
			binaryEntry += binaryData;
			
			binaryOffset += bc.intToBinaryStringToNearestByteSize(previousOffset);
			
			// Adds the length of the data entry to the offsets to use for the next data entry
			previousOffset += bc.getNumberOfBytes(binaryData);
		}
		// Offset to declare the end and or the next record
		binaryOffset += bc.intToBinaryStringToNearestByteSize(previousOffset);
		
		return (binaryOffset + binaryEntry);
	}
	
}
