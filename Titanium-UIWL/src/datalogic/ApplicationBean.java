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
import entities.SchedulerService;
import entities.Status;

/**
 * The purpose of this bean is to hold all data that is shared among the users
 * and is changed rarely. Thread safety of the hash maps is not a concern since
 * they are never edited outside of this class.
 * 
 */
@ManagedBean(name = "applicationBean", eager = true)
@ApplicationScoped
public class ApplicationBean implements Serializable {

	/*
	 * Entities that are rarely changed are retrieved from the database during
	 * the startup of the application and stored here in hash maps to reduce SQL
	 * queries. Retrieval is done in O(1) time.
	 */
	public static final HashMap<Integer, Backend> BACKENDS = new HashMap<Integer, Backend>();
	public static final HashMap<Integer, Mode> MODES = new HashMap<Integer, Mode>();
	public static final HashMap<Integer, Composite> COMPOSITES = new HashMap<Integer, Composite>();
	public static final HashMap<Integer, Status> STATUSES = new HashMap<Integer, Status>();

	// Strings used by navigation rules in faces-config.xml
	public static final String UI_REDIRECT = "redirect_to_ui";
	public static final String LOGIN_REDIRECT = "redirect_to_login";
	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";

	public static SchedulerService SCHEDULER_SERVICE;

	public TimeZone timezone;

	// Style classes for different modes in the UI. Defines the coloring for
	// mode letters.
	public static final HashMap<Integer, String> MODE_STYLES = new HashMap<Integer, String>();

	// Hard coding styleClasses for different modes
	{
		MODE_STYLES.put(1, "activated");
		MODE_STYLES.put(2, "deactivated");
		MODE_STYLES.put(3, "removed");
	}

	// Dateformats for date conversion between the database and the UI
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");

	public static final DateFormat ORACLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-dd-MM HH:mm:ss");

	public ApplicationBean() {

		/*
		 * The constructor initializes the data in ApplicationBean
		 */

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

		SCHEDULER_SERVICE = tempConnector.getSchedulingService();

	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}

}
