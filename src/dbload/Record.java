package dbload;

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
	
	public String getBinaryWithOffsets() {
		ByteConverter bc = new ByteConverter();
		
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
		
		int previousOffset = 0;
		for (String binaryData : recordBinaryDataArray) {
			binaryEntry += binaryData;
			
			binaryOffset += bc.intToBinaryStringToNearestByteSize(previousOffset);
			
			previousOffset += bc.getNumberOfBytes(binaryData);
		}
		binaryOffset += bc.intToBinaryStringToNearestByteSize(previousOffset);
		
		return binaryOffset + binaryEntry;
	}
	
}
