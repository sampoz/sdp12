package datalogic;

import entities.Scheduling;

/**
 * Bean class for holding all the information regarding a specific run of a
 * {@link Scheduling}. May or may not correspond to a certain {@link Instance}
 * object.
 * 
 */
public class Run {

	private Scheduling scheduling;
	private String status;
	private String prev;
	private String next;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrev() {
		return prev;
	}

	public void setPrev(String prev) {
		this.prev = prev;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public Scheduling getScheduling() {
		return scheduling;
	}

	public void setScheduling(Scheduling scheduling) {
		this.scheduling = scheduling;
	}

}
