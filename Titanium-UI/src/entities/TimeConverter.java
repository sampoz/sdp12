package entities;

import java.text.ParseException;

import org.quartz.impl.triggers.CronTriggerImpl;

public class TimeConverter {

	private CronTriggerImpl triggerImpl;

	public TimeConverter() {
		triggerImpl = new CronTriggerImpl();
	}
	
	public String convertCronToTime(String Cron) {
		try {
			triggerImpl.setCronExpression(Cron);
			return triggerImpl.getExpressionSummary();
		} catch (ParseException e) {
			return e.toString();
		}
	}
	
}
