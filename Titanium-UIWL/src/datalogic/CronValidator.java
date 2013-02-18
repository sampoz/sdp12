package datalogic;

import java.text.ParseException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.quartz.impl.triggers.CronTriggerImpl;

public class CronValidator implements Validator{

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		try {
			CronTriggerImpl triggerImpl = new CronTriggerImpl();
			triggerImpl.setCronExpression((String)value);
		} catch (ParseException e) {
			FacesMessage msg = new FacesMessage();
			msg.setDetail("Invalid Cron");
			msg.setSummary("Invalid Cron");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}

}
