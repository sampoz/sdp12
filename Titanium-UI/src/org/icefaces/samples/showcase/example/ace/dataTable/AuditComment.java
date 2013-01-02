package org.icefaces.samples.showcase.example.ace.dataTable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Table(name = "SCHEDULING_COMMENT")
@Entity
public class AuditComment {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int id;
	private String schedulingDate;
	private String text;
	private int schedulingId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSchedulingDate() {
		return schedulingDate;
	}
	public void setSchedulingDate(String schedulingDate) {
		this.schedulingDate = schedulingDate;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getSchedulingId() {
		return schedulingId;
	}
	public void setSchedulingId(int schedulingId) {
		this.schedulingId = schedulingId;
	}
}
