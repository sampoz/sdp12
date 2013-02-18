package entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="SCHEDULING_COMMENT")
public class Comment implements Serializable {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int id;
	
	@Column(name="CREATION_DATE")
	private String creationDate;
	
	@Column(name="TEXT")
	private String text;
	
	@Column(name="SCHEDULING_ID")
	private int schedulingID;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSchedulingID() {
		return schedulingID;
	}

	public void setSchedulingID(int schedulingID) {
		this.schedulingID = schedulingID;
	}
	
	

}
