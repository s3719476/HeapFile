package dbload;

import interfaces.Record;

public class Pedestrian implements Record{
	
	private int ID;
	private String Date_Time;
	private int Sensor_ID;
	private int Hourly_Counts;
	
	public Pedestrian(int ID, String Date_Time, int Sensor_ID, int Hourly_Counts) {
		this.ID = ID;
		this.Date_Time = Date_Time;
		this.Sensor_ID = Sensor_ID;
		this.Hourly_Counts = Hourly_Counts;
	}
	
	public int getID() {
		return ID;
	}
	public String getDate_Time() {
		return Date_Time;
	}
	public int getSensor_ID() {
		return Sensor_ID;
	}
	public int getHourly_Counts() {
		return Hourly_Counts;
	}
}
