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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
		
		if(startday.compareTo(today)<=0){
			
			//System.out.println("here");
			
			while(startday.compareTo(today)<=0){
				startday.add(Calendar.DAY_OF_MONTH, period);	
				//System.out.println("add");
				//System.out.println(sdf.format(startday.getTime()));
				//System.out.println(sdf.format(today.getTime()));
			}
			//this is the current start day 
			startday.add(Calendar.DAY_OF_MONTH, -period);
			
			
		}
		else{
			
			while(startday.compareTo(today)>0){
				startday.add(Calendar.DAY_OF_MONTH, -period);	
			}
			
			
		}
		
		
		startday.add(Calendar.DAY_OF_MONTH, -history*period);
		return sdf.format(startday.getTime());
		
	}
	
	public static String today() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar today = Calendar.getInstance();
		
		//return "2017-12-1";
		return sdf.format(today.getTime());
		
	}
	public static String getEndDate(String startSate,Integer period){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar startday = Calendar.getInstance();
		try {
			startday.setTime(sdf.parse(startSate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startday.add(Calendar.DAY_OF_MONTH, period);
		return sdf.format(startday.getTime());
		
	}
	
	public static long getRemian(String endDate){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar endday = Calendar.getInstance();
		try {
			endday.setTime(sdf.parse(endDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Calendar today = Calendar.getInstance();
		
		Date startDate = today.getTime();
		Date endDate1 = endday.getTime();
		
		long startTime = startDate.getTime();
		long endTime = endDate1.getTime();
		long diffTime = endTime - startTime;
		long diffDays = diffTime / (1000 * 60 * 60 * 24);
		return diffDays;
		
		
		
		
	}
	
	
	/*public static void main( String[] args ){
		String start="2017-11-1";
		long result=DateCal.getRemian(start);
		System.out.println(result);
	}*/
	

}