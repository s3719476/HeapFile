package dbload;

import interfaces.Record;

public class Time implements Record{
	
	private String Date_Time;
	private int Ydate;
	private String Month;
	private int Mdate;
	private String Day;
	private int Time;
	
	public Time(String Date_time, int Ydate, String Month, int Mdate, String Day, int Time) {
		this.Date_Time = Date_time;
		this.Ydate = Ydate;
		this.Month = Month;
		this.Mdate = Mdate;
		this.Day = Day;
		this.Time = Time;
	}
	
	public String getDate_Time() {
		return Date_Time;
	}
	public int getYdate() {
		return Ydate;
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

}
