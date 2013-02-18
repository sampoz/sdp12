package entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
/**
 * Mode entity is for the Scheduling_status table.
 * Scheduling_status gives the labels for the scheduling_Status ints.
 * Labels are easier for the user than Status (magic) numbers.
 * 
 *
 */
@Entity
@Table(name="SCHEDULING_STATUS")
public class Mode implements Serializable {
	
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
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
	
	@Override
	public String toString(){
		return this.value;
	}
}
