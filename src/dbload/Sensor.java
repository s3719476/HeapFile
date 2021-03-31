package dbload;

import interfaces.Record;

public class Sensor implements Record{
	
	private int Sensor_ID;
	private String Sensor_name;
	
	public Sensor(int Sensor_ID, String Sensor_name) {
		this.Sensor_ID = Sensor_ID;
		this.Sensor_name = Sensor_name;
	}
	
	public int getSensor_ID() {
		return Sensor_ID;
	}
	public String getSensor_name() {
		return Sensor_name;
	}
	
}
