package DatabaseUtilities;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateClass {
	private int year, month, day;
	public static String delimiter = "/";
	public static enum enumDayOfWeek {Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Error};
	
	/**
	 * Blank constructor
	 */
	public DateClass() {
		year = 0;
		month = 0;
		day = 0;
	}
	
	/**
	 * Initialization constructor
	 * @param year Year
	 * @param month Month
	 * @param day Day
	 */
	public DateClass(int year, int month, int day){
		this.year = year;
		this.month = month;
		this.day = day;
	}
	/**
	 * Copy constructor
	 * @param date The date object to be copied. 
	 */
	public DateClass(DateClass date) {
		year = date.getYear();
		month = date.getMonth();
		day = date.getDay();
	}
	
	public int getYear() {return year;}
	public int getMonth() {return month;}
	public int getDay() {return day;}

	public int setYear(int year) {this.year = year; return year;}
	public int setMonth(int month) {this.month = month; return month;}
	public int setDay(int day) {this.day = day; return day;}

	/**
	 * Convert to a string
	 * @return The string
	 */
	public String toString() {
		return year + delimiter + month + delimiter + day; 
	}
	/**
	 * Convert to a SQL-compatible string literal
	 * @return The SQL-compatible string literal
	 */
	public String toStringSQL() {
		return "#" + toString() + "#"; 
	}
	/**
	 * 
	 */
	public void addDay() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(this.year,getCalendarMonth(),this.day);
		//System.out.println((calendar.get(Calendar.MONTH) + 1)+ "-"+ calendar.get(Calendar.DATE)+ "-"+ calendar.get(Calendar.YEAR));
		calendar.add(Calendar.DATE, 1);
		//System.out.println((calendar.get(Calendar.MONTH) + 1)+ "-"+ calendar.get(Calendar.DATE)+ "-"+ calendar.get(Calendar.YEAR));
		this.year = calendar.get(Calendar.YEAR);
		this.month = calendar.get(Calendar.MONTH) + 1;
		this.day = calendar.get(Calendar.DAY_OF_MONTH);
	}
	/**
	 * 
	 * @return
	 */
	private int getCalendarMonth() {
		switch (this.month) {
		case 1:	 return Calendar.JANUARY;
		case 2:	 return Calendar.FEBRUARY;
		case 3:	 return Calendar.MARCH;
		case 4:	 return Calendar.APRIL;
		case 5:	 return Calendar.MAY;
		case 6:	 return Calendar.JUNE;
		case 7:	 return Calendar.JULY;
		case 8:	 return Calendar.AUGUST;
		case 9:	 return Calendar.SEPTEMBER;
		case 10: return Calendar.OCTOBER;
		case 11: return Calendar.NOVEMBER;
		case 12: return Calendar.DECEMBER;
		default: return 0;
		}
	}
	
	static public void main(String[] args) {
		//DateClass x = new DateClass(2012,1,7);
		//x.addDay();
		
		DateClass d = new DateClass(2012,1,1);
		for (int i = 1; i <= 365; i++) {
			System.out.println(d.toString());
			d.addDay();
			System.out.println(d.toString());
		}
	}
	/**
	 * Get the day of the week
	 * @return The day of the week as defined by our enum, enumDayOfWeek
	 */
	public enumDayOfWeek getDayOfWeek() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(this.year,getCalendarMonth(),this.day);
	    switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		    case 1:	return enumDayOfWeek.Sunday;
		    case 2:	return enumDayOfWeek.Monday;
		    case 3:	return enumDayOfWeek.Tuesday;
		    case 4:	return enumDayOfWeek.Wednesday;
		    case 5:	return enumDayOfWeek.Thursday;
		    case 6:	return enumDayOfWeek.Friday;
		    case 7:	return enumDayOfWeek.Saturday;
		    default: return enumDayOfWeek.Error;	
	    }
	}
}
