package org.icefaces.samples.showcase.example.ace.dataTable;


import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Id;



@Entity
@Table(name = "SCHEDULING")
public class Scheduling implements Serializable  {
	
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="SERVICEID", insertable=false, updatable=false)
	private SchedulingService service;
	
	@ManyToOne
	@JoinColumn(name="STATUSID", insertable=false, updatable=false)
	private SchedulingStatus status;

	@Column(name = "NAME")
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "CRON")
	private String Cron;
	
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
	
	public Scheduling(){}
	
	


	public Scheduling(String name, String description, String cron,
			int serviceID, int statusID) {
		super();
		this.name = name;
		this.description = description;
		Cron = cron;
		this.serviceID = serviceID;
		this.statusID = statusID;
		this.bankHolidayOnly = 0;
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
		return Cron;
	}


	public void setCron(String cron) {
		Cron = cron;
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
	
	public SchedulingService getService() {
		return service;
	}




	public void setService(SchedulingService service) {
		this.service = service;
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
	
	public SchedulingStatus getStatus() {
		return status;
	}




	public void setStatus(SchedulingStatus status) {
		this.status = status;
	}
	
}
