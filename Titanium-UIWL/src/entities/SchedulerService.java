package entities;


import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import datalogic.ApplicationBean;

import javax.persistence.Id;
/**
 * SchedulerService entity is for Schduling_config table.
 * Scheduling_config tells only whether the Scheduler is running or not.
 */
@Entity
@Table(name = "SCHEDULING_CONFIG")
public class SchedulerService {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@Column(name = "STANDBY")
	private int standby;
	
	public static int STOPPED = 1;
	public static int RUNNING = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStandby() {
		return standby;
	}

	public void setStandby(int standby) {
		this.standby = standby;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "URL")
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
