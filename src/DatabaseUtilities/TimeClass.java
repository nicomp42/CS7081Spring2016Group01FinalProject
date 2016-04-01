package DatabaseUtilities;

public class TimeClass {
	private int hour, minute;
	public static String delimiter = ":";
	/**
	 * Blank constructor
	 */
	public TimeClass() {
		hour = 0;
		minute = 0;
	}
	/**
	 * Initialization constructor
	 * @param year Year
	 * @param month Month
	 * @param day Day
	 */
	public TimeClass(int hour, int minute){
		this.hour = hour;
		this.minute = minute;
	}
	/**
	 * Copy constructor
	 * @param Time The Time object to be copied. 
	 */
	public TimeClass(TimeClass Time) {
		hour = Time.getHour();
		minute = Time.getMinute();
	}
	
	public int getHour() {return hour;}
	public int getMinute() {return minute;}

	public int setHour(int hour) {this.hour = hour; return hour;}
	public int setMinute(int minute) {this.minute = minute; return minute;}

	/**
	 * Convert to a string
	 * @return The string
	 */
	public String toString() {
		return hour + delimiter + minute; 
	}
	/**
	 * Convert to a SQL-compatible string literal
	 * @return The SQL-compatible string literal
	 */
	public String toStringSQL() {
		return "#" + toString() + "#"; 
	}

}
