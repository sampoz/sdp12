package entities;

import java.text.ParseException;

import org.quartz.impl.triggers.CronTriggerImpl;
/**
 *  
 *
 *
 */
public class TimeConverter {

	public static String convertCronToTime(String Cron) {
		try {
			CronTriggerImpl triggerImpl = new CronTriggerImpl();
			triggerImpl.setCronExpression(Cron);
			return triggerImpl.getExpressionSummary();
		} catch (ParseException e) {
			return e.toString();
		}
	}
	
}
