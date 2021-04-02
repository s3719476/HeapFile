// Helper class to get the generic details of a record and formatting strings for given record prints
public class RecordTemplateHelper {
	
	private final String[] fieldTypes = {"int", "String", "String", "int", "String", "int", "String", "int", "int", "String", "int"};
	private final String[] fieldNames = {
			"ID", "SDT_Name", "Date_Time", "Year", "Month", "Mdate", "Day", "Time", "Sensor_ID", "Sensor_Name", "Hourly_Counts"
	};
	private final int fieldNum = 11;
	
	public RecordTemplateHelper() {
		
	}
	
	public String getFieldType(int index) {
		return fieldTypes[index];
	}
	
	public String getFieldName(int index) {
		return fieldNames[index];
	}
	
	public String[] getFieldTypes() {
		return fieldTypes;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}
	
	public int getFieldNum() {
		return fieldNum;
	}

	public String getFormattedNameValue(int nameIndex, String value) {
		return (fieldNames[nameIndex] + ": " + value);
	}
	
	public String getFormattedNameValue(int nameIndex, int value) {
		return getFormattedNameValue(nameIndex, Integer.toString(value));
	}
	
	public String getFormattedNameValue(String[] values) {
		String retVal = "";
		
		boolean isFirst = true;
		for (int i = 0; i < values.length; ++i) {
			if (isFirst == false) retVal += ", ";
			isFirst = false;
			
			retVal += fieldNames[i];
			retVal += ": ";
			retVal += values[i];
		}
		
		return retVal;
	}

}
