package datalogic;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import entities.Backend;
import entities.Composite;
import entities.Mode;
import entities.Status;

@ManagedBean(name = "applicationBean", eager = true)
@ApplicationScoped
public class ApplicationBean implements Serializable {

	public static final HashMap<Integer, Backend> BACKENDS = new HashMap<Integer, Backend>();
	public static final HashMap<Integer, Mode> MODES = new HashMap<Integer, Mode>();
	public static final HashMap<Integer, Composite> COMPOSITES = new HashMap<Integer, Composite>();
	public static final HashMap<Integer, Status> STATUSES = new HashMap<Integer, Status>();

	public static final HashMap<Integer, String> MODE_STYLES = new HashMap<Integer, String>();

	public TimeZone timezone;
	// Hard coded styleClasses for different modes
	{
		MODE_STYLES.put(1, "activated");
		MODE_STYLES.put(2, "deactivated");
		MODE_STYLES.put(3, "removed");
	}

	public static final int ENABLED = 1;
	public static final int DISABLED = 2;
	public static final int REMOVED = 3;
	
	public static final int INFINITY = -1;
	public static final int MAX_COMMENTS_SHOWN = 5;

	// PLACEHOLDER
	public String html = "<object>"
			+ "<param name=\"movie\"value=\"http://myinstants.com/media/bt/genericInstant_event.swf\" />"
			+ "<param name=\"wmode\" value=\"transparent\">"
			+ "<param name=\"flashVars\"	value=\"color=ff0000&sound=http://myinstants.com/media/sounds/inceptionbutton.mp3\">"
			+ "<embed src=\"http://myinstants.com/media/bt/genericInstant_event.swf\" flashVars=\"color=ff0000&sound=http://myinstants.com/media/sounds/inceptionbutton.mp3\" wmode=\"transparent\" width=\"100\" height=\"100\" />"
			+ "</object>";

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");

	public static final DateFormat ORACLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-dd-MM HH:mm:ss");

	public ApplicationBean() {

		DatabaseConnector tempConnector = new DatabaseConnector();

		List<Status> tempStatuses = tempConnector.getAllStatuses();
		for (Status s : tempStatuses) {
			STATUSES.put(s.getId(), s);
		}

		List<Composite> tempComposites = tempConnector.getAllComposites();
		for (Composite c : tempComposites) {
			COMPOSITES.put(c.getId(), c);
		}

		List<Backend> tempBackends = tempConnector.getAllBackends();
		for (Backend b : tempBackends) {
			BACKENDS.put(b.getId(), b);
		}
		List<Mode> tempModes = tempConnector.getAllModes();
		for (Mode m : tempModes) {
			MODES.put(m.getId(), m);
		}

		this.timezone = Calendar.getInstance().getTimeZone();

		tempConnector.close();
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}

}
