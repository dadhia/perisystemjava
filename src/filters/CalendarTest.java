package filters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarTest {

	public static void main(String [] args) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		System.out.println("Today's date is: " + df.format(today));
		System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
		
		
		calendar.add(Calendar.YEAR, -1);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 7)			//if sunday, make it friday
			calendar.set(Calendar.DAY_OF_WEEK, -2);
		else if (dayOfWeek == 6)	//if sunday, make it friday
			calendar.set(Calendar.DAY_OF_WEEK, -1);
		
		Date lastYear = calendar.getTime();
		System.out.println("Last year's date is: " + df.format(lastYear));
		
		
	}
}
