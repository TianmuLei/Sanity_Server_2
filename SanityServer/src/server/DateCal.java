package server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateCal {
	public static String calculateCurrentStart(String startSate,Integer period){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
		Calendar today = Calendar.getInstance();
		Calendar startday = Calendar.getInstance();
		try {
			startday.setTime(sdf.parse(startSate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		today.set(Calendar.HOUR_OF_DAY, 0);
		startday.set(Calendar.HOUR_OF_DAY, 0);
		
		while(startday.compareTo(today)<=0){
			startday.add(Calendar.DAY_OF_MONTH, period);	
		}
		//this is the current start day 
		startday.add(Calendar.DAY_OF_MONTH, -period);

		return sdf.format(startday.getTime());
	}
	public static String calculateCurrentStart(String startSate,Integer period,int history){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
		Calendar today = Calendar.getInstance();
		Calendar startday = Calendar.getInstance();
		try {
			startday.setTime(sdf.parse(startSate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		today.set(Calendar.HOUR_OF_DAY, 0);
		startday.set(Calendar.HOUR_OF_DAY, 0);
		
		while(startday.compareTo(today)<=0){
			startday.add(Calendar.DAY_OF_MONTH, period);	
		}
		//this is the current start day 
		startday.add(Calendar.DAY_OF_MONTH, -period);
		startday.add(Calendar.DAY_OF_MONTH, -history*period);
		return sdf.format(startday.getTime());
		
	}

}
