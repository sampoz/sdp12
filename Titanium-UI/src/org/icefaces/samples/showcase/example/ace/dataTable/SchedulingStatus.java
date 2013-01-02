package org.icefaces.samples.showcase.example.ace.dataTable;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SCHEDULING_STATUS")
public class SchedulingStatus implements Serializable {
	
	
	@Id
	@Column(name="ID")
	private int id;
	
	@Column(name="LABEL")
	private String label;
	
	@Column(name="VALUE")
	private String value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
