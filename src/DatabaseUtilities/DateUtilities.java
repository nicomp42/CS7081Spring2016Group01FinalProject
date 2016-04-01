package DatabaseUtilities;

import java.util.Random;

public class DateUtilities {
	private DateClass date;
	private DateClass startDate;
	private DateClass throughDate;
	//static Random myRandom;
	
	/**
	 * Shabby test main
	 * @param args
	 */
	public static void main(String[] args) {

		DateClass d = new DateClass();
		for (int i = 0; i < 10; i++) {
			d = DateUtilities.getRandomDate(new DateClass(2012,1,1), new DateClass(2012,12,31));
			System.out.println(d.toString());
		}
	}
	/**
	 * Constructor
	 * @param date The Date object 
	 * @param myRandom The Random object that should be used to generate the date, or null
	 */
//	public DateUtilities(Random myRandom){
//		if (myRandom != null) {
//			Config.getRandom() = myRandom;
//		} else {
//			this.myRandom = new Random();
//		}
//	}
	
	public static DateClass getRandomDate(DateClass startDate, DateClass throughDate) {
		//if (myRandom == null) {	myRandom = new Random();}
		//int sYear, sMonth, sDay, tYear, tMonth, tDay;	// Start and through
		int yearDiff, monthDiff, dayDiff;
		DateClass randomDate = new DateClass();
		int startYear = startDate.getYear();
		int startMonth = startDate.getMonth();
		int startDay = startDate.getDay();
		int throughYear = throughDate.getYear();
		int throughMonth = throughDate.getMonth();
		int throughDay = throughDate.getDay();
		yearDiff = throughYear - startYear;
		monthDiff = throughMonth - startMonth;
		dayDiff = throughDay - startDay;

		while (true) {
			randomDate = new DateClass((startYear + Config.getRandom().nextInt(yearDiff + 1)), 
					              (startMonth + Config.getRandom().nextInt(monthDiff + 1)),
					              (startDay + Config.getRandom().nextInt(dayDiff + 1)));
			if (randomDate.getYear() == throughYear) {
				if (randomDate.getMonth() > throughMonth) continue;
				if ((randomDate.getMonth() == throughMonth) && (randomDate.getDay() > throughDay)) continue;
			}
			// Make sure the date is physically valid.
			int maxDay = getMaxDay(randomDate.getYear(), randomDate.getMonth());
			if (randomDate.getDay() > maxDay) continue;
			
			// If we get this far we have a random date in the proper range;
			break;
		}
		return randomDate;
	}
	public static int getMaxDay(int year, int month) {
		int maxDay = 0;
		switch (month) {
		case 1:	
			maxDay = 31;
			break;
		case 2:
			if (((year % 4) == 0) && ((year % 100) != 0)) 
				maxDay = 29;
			else
				maxDay = 28;
			break;
		case 3:	
			maxDay = 31;
			break;
		case 4:			// April
			maxDay = 30;
			break;
		case 5:	
			maxDay = 31;
			break;
		case 6:			// June
			maxDay = 30;
			break;
		case 7:	
			maxDay = 31;
			break;
		case 8:	
			maxDay = 31;
			break;
		case 9:			// September
			maxDay = 30;
			break;
		case 10:	
			maxDay = 31;
			break;
		case 11:		// November
			maxDay = 30;
			break;
		case 12:	
			maxDay = 31;
			break;

		}
		return maxDay;	
	}
	public static TimeClass getRandomTime() {
		//if (myRandom == null) {	myRandom = new Random();}
		TimeClass randomTime = new TimeClass(Config.getRandom().nextInt(24), Config.getRandom().nextInt(60));

		return randomTime;
	}
	/**
	 * Get a random time within a range. 
	 * @param startTime Minimum time. Inclusive
	 * @param throughTime Maximum time. Inclusive
	 * @return The random time
	 */
	public static TimeClass getRandomTime(TimeClass startTime, TimeClass throughTime) {
		//if (myRandom == null) {	myRandom = new Random();}
		int startHour = startTime.getHour(), startMinute = startTime.getMinute();
		int throughHour = throughTime.getHour(), throughMinute = throughTime.getMinute();
		
		TimeClass randomTime = null;
		while (true) {
			randomTime = new TimeClass(Config.getRandom().nextInt(24), Config.getRandom().nextInt(60));
			int tmpHour = randomTime.getHour(), tmpMinute = randomTime.getMinute();
			int startTotalMinutes = startHour * 60 + startMinute;
			int throughTotalMinutes = throughHour * 60 + throughMinute;
			int tmpTotalMinutes = tmpHour * 60 + tmpMinute;
			if ((tmpTotalMinutes >= startTotalMinutes) && (tmpTotalMinutes <= throughTotalMinutes)) {
				break;
			}
		}
		return randomTime;
	}
}
