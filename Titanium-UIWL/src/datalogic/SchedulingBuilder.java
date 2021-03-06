package datalogic;

import entities.Backend;
import entities.Comment;
import entities.Composite;
import entities.Mode;
import entities.Scheduling;

/**
 * Builder class for creating a new scheduling.
 */
public class SchedulingBuilder {

	private String name;
	private boolean javaAgentPollable = false;
	private boolean bankHolidayOnly = false;
	private Mode mode;
	private Composite composite;
	private Backend source;
	private Backend target;
	private String requestURL = "";
	private String cron = "";
	private String description = "";
	private String contacts = "";
	private int id;

	private Comment comment = new Comment();

	// Upon creation we need to initialize the composite and backends to avoid
	// errors in the UI drop down menus
	public SchedulingBuilder() {
		initComposite();
		initBackends();
	}

	private void initComposite() {
		Composite c = null;
		int i = 0;
		while (c == null) {
			c = ApplicationBean.COMPOSITES.get(i);
			i++;
		}
		this.composite = c;
	}

	private void initBackends() {
		Backend b = null;
		int i = 0;
		while (b == null) {
			b = ApplicationBean.BACKENDS.get(i);
			i++;
		}
		this.target = b;
		this.source = b;
	}

	// A scheduling may be passed to the constructor to initialize the fields
	// accordingly
	public SchedulingBuilder(Scheduling s) {
		this.name = s.getName();
		this.mode = ApplicationBean.MODES.get(s.getStatusID());
		this.composite = ApplicationBean.COMPOSITES.get(s.getServiceID());
		this.source = ApplicationBean.BACKENDS.get(s.getSource());
		this.target = ApplicationBean.BACKENDS.get(s.getTarget());
		if (this.composite.getJavaAgentPollable() == 1
				&& s.getJavaAgentPollable() == 1)
			this.javaAgentPollable = true;

		if (s.getBankHolidayOnly() == 1)
			this.bankHolidayOnly = true;
		this.requestURL = s.getRequestURL();
		this.cron = s.getCron();
		this.description = s.getDescription();
		this.contacts = s.getContacts();
		this.id = s.getId();
	}

	/**
	 * Builds the {@link Scheduling} out of the values in the fields. Values are not
	 * validated as they are expected to be checked by the form validation.
	 * 
	 * @return the new scheduling object
	 */
	public Scheduling build() {
		Scheduling s = new Scheduling();
		s.setName(name);
		s.setId(id);
		s.setContacts(contacts);

		if (this.bankHolidayOnly)
			s.setBankHolidayOnly(1);
		else
			s.setBankHolidayOnly(0);
		if (this.composite.getJavaAgentPollable() == 1
				&& this.javaAgentPollable)
			s.setJavaAgentPollable(1);
		else
			s.setJavaAgentPollable(0);

		s.setCron(cron);
		s.setDescription(description);
		s.setRequestURL(requestURL);
		s.setServiceID(this.composite.getId());
		s.setStatusID(this.mode.getId());
		s.setSource(this.source.getId());
		s.setTarget(this.target.getId());
		return s;
	}

	// ==================== GETTERS & SETTERS ====================
	public String getName() {
		return name;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public String getCron() {
		return cron;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public boolean isJavaAgentPollable() {
		return javaAgentPollable;
	}

	public void setJavaAgentPollable(boolean javaAgentPollable) {
		this.javaAgentPollable = javaAgentPollable;
	}

	public boolean isBankHolidayOnly() {
		return bankHolidayOnly;
	}

	public void setBankHolidayOnly(boolean bankHolidayOnly) {
		this.bankHolidayOnly = bankHolidayOnly;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
		if (this.composite.getJavaAgentPollable() == 0)
			this.javaAgentPollable = false;
	}

	public Backend getSource() {
		return source;
	}

	public void setSource(Backend source) {
		this.source = source;
	}

	public Backend getTarget() {
		return target;
	}

	public void setTarget(Backend target) {
		this.target = target;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

}
