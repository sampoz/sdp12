

import org.icefaces.samples.showcase.example.ace.dataTable.Scheduling;

public class SchedulingBuilder {
	
	private String name;
	private int javaAgentPollable;
	private int statusID;
	private int serviceID;
	private int bankHolidayOnly;
	
	public String getName() {
		return name;
	}

	public int getJavaAgentPollable() {
		return javaAgentPollable;
	}

	public int getStatusID() {
		return statusID;
	}

	public int getServiceID() {
		return serviceID;
	}

	public int getBankHolidayOnly() {
		return bankHolidayOnly;
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


	private String requestURL;
	private String cron;
	private String description;
	private int id;
	
	public Scheduling build(){
		Scheduling s = new Scheduling();
		s.setName(name);
		s.setId(id);
		s.setBankHolidayOnly(bankHolidayOnly);
		s.setCron(cron);
		s.setDescription(description);
		s.setRequestURL(requestURL);
		s.setServiceID(serviceID);
		s.setStatusID(statusID);
		s.setJavaAgentPollable(javaAgentPollable);
		return s;
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

	public void setBankHolidayOnly(int bankHolidayOnly) {
		this.bankHolidayOnly = bankHolidayOnly;
	}

	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}


	public void setJavaAgentPollable(int javaAgentPollable) {
		this.javaAgentPollable = javaAgentPollable;
	};
	
	
}
