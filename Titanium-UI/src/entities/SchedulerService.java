package entities;


import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import datalogic.ApplicationBean;

import javax.persistence.Id;

@Entity
@Table(name = "SCHEDULING_CONFIG")
public class SchedulerService {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@Column(name = "STANDBY")
	private int standby;

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
