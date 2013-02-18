package entities;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import datalogic.ApplicationBean;

import javax.persistence.Id;

@Entity
@Table(name = "SCHEDULING")
public class Scheduling implements Serializable {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@Transient
	private String modeLabel;

	@Transient
	private String sourceName;

	@Transient
	private String targetName;

	@Transient
	private String styleClass;

	// @ManyToOne
	// @JoinColumn(name="SERVICEID", insertable=false, updatable=false)
	// private SchedulingService service;
	//
	// @ManyToOne
	// @JoinColumn(name="STATUSID", insertable=false, updatable=false)
	// private SchedulingStatus status;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CRON")
	private String cron;
	
	@Transient
	private String time;

	@Column(name = "REQUESTURL")
	private String requestURL;

	@Column(name = "BANKHOLIDAYONLY")
	private Integer bankHolidayOnly;

	@Column(name = "SERVICEID")
	private Integer serviceID;

	@Column(name = "STATUSID")
	private Integer statusID;

	@Column(name = "JAVAAGENTPOLLABLE")
	private Integer javaAgentPollable;

	@Column(name = "SOURCE")
	private Integer source;

	@Column(name = "TARGET")
	private Integer target;

	@Column(name = "CONTACTS")
	private String contacts;

	public Scheduling() {
	}

	public boolean matchesInstance(Instance i) {
		if (this.name != null
				&& i.getProcess() != null
				&& this.name.length() >= 4
				&& i.getProcess().length() >= 4
				&& this.name.substring(0, 4).equals(
						i.getProcess().substring(0, 4)))
			return true;
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getTime() {
		return TimeConverter.convertCronToTime(cron);
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public int getBankHolidayOnly() {
		return bankHolidayOnly;
	}

	public void setBankHolidayOnly(int bankHolidayOnly) {
		this.bankHolidayOnly = bankHolidayOnly;
	}

	public int getServiceID() {
		return serviceID;
	}

	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}

	public int getStatusID() {
		return statusID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}

	public int getJavaAgentPollable() {
		return javaAgentPollable;
	}

	public void setJavaAgentPollable(int javaAgentPollable) {
		this.javaAgentPollable = javaAgentPollable;
	}

	public void setBankHolidayOnly(Integer bankHolidayOnly) {
		this.bankHolidayOnly = bankHolidayOnly;
	}

	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}

	public void setStatusID(Integer statusID) {
		this.statusID = statusID;
	}

	public void setJavaAgentPollable(Integer javaAgentPollable) {
		this.javaAgentPollable = javaAgentPollable;
	}

	public String getModeLabel() {
		this.modeLabel = ApplicationBean.MODES.get(this.statusID).getLabel();
		return modeLabel;
	}

	public void setModeLabel(String modeLabel) {
		this.modeLabel = modeLabel;
	}

	public String getSourceName() {
		try {

			this.sourceName = ApplicationBean.BACKENDS.get(this.source)
					.getBackend();

		} catch (NullPointerException e) {
			return "SAMPSA UPDATEE RIVIT";
		}
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTargetName() {
		try {
			this.targetName = ApplicationBean.BACKENDS.get(this.target)
					.getBackend();
		} catch (NullPointerException e) {
			return "SAMPSA UPDATEE RIVIT";
		}
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getStyleClass() {
		this.styleClass = ApplicationBean.MODE_STYLES.get(this.statusID);
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scheduling other = (Scheduling) obj;
		if (bankHolidayOnly == null) {
			if (other.bankHolidayOnly != null)
				return false;
		} else if (!bankHolidayOnly.equals(other.bankHolidayOnly))
			return false;
		if (contacts == null) {
			if (other.contacts != null)
				return false;
		} else if (!contacts.equals(other.contacts))
			return false;
		if (cron == null) {
			if (other.cron != null)
				return false;
		} else if (!cron.equals(other.cron))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (javaAgentPollable == null) {
			if (other.javaAgentPollable != null)
				return false;
		} else if (!javaAgentPollable.equals(other.javaAgentPollable))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (requestURL == null) {
			if (other.requestURL != null)
				return false;
		} else if (!requestURL.equals(other.requestURL))
			return false;
		if (serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (!serviceID.equals(other.serviceID))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (statusID == null) {
			if (other.statusID != null)
				return false;
		} else if (!statusID.equals(other.statusID))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		
		return true;
	}

}
