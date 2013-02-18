package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
/**
 * Status entity is for Soa_composite_status -table.
 * Table holds only so many rows that there is different statuses.
 * Value is a string that is shown in UI for corresponding id.
 * 
 *
 */
@Entity
@Table(name="SOA_COMPOSITE_STATUS")
public class Status {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int id;
	
	@Column(name="VALUE")
	private String value;
	
	//Not used in application
	@Column(name="ORDER_NUM")
	private int orderNum;
	
	//Not used in application
	@Column(name="IMAGE")
	private String image;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
	
}
