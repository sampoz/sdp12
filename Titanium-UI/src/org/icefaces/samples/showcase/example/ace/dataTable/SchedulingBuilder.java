package org.icefaces.samples.showcase.example.ace.dataTable;


import org.icefaces.samples.showcase.example.ace.dataTable.Scheduling;

/*
 * Builder class for creating a new scheduling.
 */

public class SchedulingBuilder {
	
	
	private String name;
	private String javaAgentPollable = "0";
	private String statusID = "1";
	private String serviceID = "1";
	private String bankHolidayOnly = "0";
	private String requestURL;
	private String cron = "*";
	private String description;
	private int id;
	
	public SchedulingBuilder(){}
	
	public SchedulingBuilder(Scheduling s){
		this.name = s.getName();
		this.javaAgentPollable = Integer.toString(s.getJavaAgentPollable());
		this.statusID = Integer.toString(s.getStatusID());
		this.serviceID = Integer.toString(s.getServiceID());
		this.bankHolidayOnly = Integer.toString(s.getBankHolidayOnly());
		this.requestURL = s.getRequestURL();
		this.cron = s.getCron();
		this.description = s.getDescription();
		this.id = s.getId();
	}
	
	public String getName() {
		return name;
	}

	public String getJavaAgentPollable() {
		return javaAgentPollable;
	}

	public String getStatusID() {
		return statusID;
	}

	public String getServiceID() {
		return serviceID;
	}

	public String getBankHolidayOnly() {
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
	
	public Scheduling build(){
		Scheduling s = new Scheduling();
		s.setName(name);
		s.setId(id);
		s.setBankHolidayOnly(Integer.parseInt(bankHolidayOnly));
		s.setCron(cron);
		s.setDescription(description);
		s.setRequestURL(requestURL);
		s.setServiceID(Integer.parseInt(serviceID));
		s.setStatusID(Integer.parseInt(statusID));
		s.setJavaAgentPollable(Integer.parseInt(javaAgentPollable));
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

	public void setBankHolidayOnly(String bankHolidayOnly) {
		this.bankHolidayOnly = bankHolidayOnly;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public void setStatusID(String statusID) {
		this.statusID = statusID;
	}

	public void setJavaAgentPollable(String javaAgentPollable) {
		this.javaAgentPollable = javaAgentPollable;
	};
	
	public void sync(Scheduling s){
		s.setName(name);
		s.setId(id);
		s.setBankHolidayOnly(Integer.parseInt(bankHolidayOnly));
		s.setCron(cron);
		s.setDescription(description);
		s.setRequestURL(requestURL);
		s.setServiceID(Integer.parseInt(serviceID));
		s.setStatusID(Integer.parseInt(statusID));
		s.setJavaAgentPollable(Integer.parseInt(javaAgentPollable));
	}
}
