package datalogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.ExpansionChangeEvent;
import org.icefaces.ace.model.table.RowStateMap;

import entities.Backend;
import entities.Composite;
import entities.Instance;
import entities.Mode;

/**
 * Class for handling data related to {@link Instance} objects and the instance
 * log view.
 * 
 */
@ManagedBean(name = "instanceDataManager")
@ViewScoped
public class InstanceDataManager implements Serializable {

	// Injected bean for the current session
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	/*
	 * List of instances, instances filtered by status and the binding of the
	 * corresponding data table
	 */
	private DataTable i_dataTable;
	private List<Instance> instances = new ArrayList<Instance>();
	private List<Instance> filteredInstances = new ArrayList<Instance>();

	// Do we show the skipped instances in the UI
	private boolean showSkipped;

	// Dates for searching instances in between
	private Date startDate;
	private Date endDate;

	// The most recent dates allowed for start and end
	private Date maxStartDate;
	private Date maxEndDate = new Date();

	// Date error visibility and the message
	private boolean showDateError;
	private String dateError;

	/*
	 * After bean creation, initialize the limits for date selections, retrieve
	 * the list of instances from the session bean and filter them accordingly.
	 */
	@PostConstruct
	private void init() {

		Calendar c = Calendar.getInstance();
		c.setTime(maxEndDate);
		c.add(Calendar.DATE, -1);
		this.maxStartDate = c.getTime();
		this.startDate = this.maxStartDate;
		this.endDate = this.maxEndDate;

		this.instances = this.session.getInstances();
		filterInstances();
	}

	/**
	 * Refreshes the instances of the session and retrieves them, then filters
	 * them accordingly.
	 */
	public void refreshInstances() {
		this.instances.clear();
		this.session.refreshInstances();
		this.instances = this.session.getInstances();
		filterInstances();
	}

	// Filter instances that are shown in the UI
	private void filterInstances() {
		if (this.showSkipped)
			this.filteredInstances = this.instances;
		else {
			this.filteredInstances = new ArrayList<Instance>();
			for (Instance i : this.instances) {
				if (i.getStatusID() != 6)
					this.filteredInstances.add(i);
			}
		}
	}

	/**
	 * Filter the instances specifically by the interval given by the user
	 */
	public void filterInstancesByDate() {
		this.filteredInstances = new ArrayList<Instance>();
		Map<Date, List<Instance>> map = this.session.getInstancesByDate();
		for (Date d : map.keySet()) {
			if (d.after(this.startDate) && d.before(this.endDate)) {
				for (Instance i : map.get(d)) {
					if (showSkipped || i.getStatusID() != 6) {
						this.filteredInstances.add(i);
					}
				}
			}
		}
	}
	
	/**
	 * Called when the boolean check box for showing/hiding skipped instances is changed. Filters the instances in the UI accodringly.
	 */
	public void showSkippedMethod(ValueChangeEvent e) {
		this.showSkipped = Boolean.parseBoolean(e.getNewValue().toString());
		this.filterInstances();
	}

	// ==================== GETTERS & SETTERS ====================
	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public List<Instance> getFilteredInstances() {
		return filteredInstances;
	}

	public void setFilteredInstances(List<Instance> filteredInstances) {
		this.filteredInstances = filteredInstances;
	}

	public DataTable getI_dataTable() {
		return i_dataTable;
	}

	public void setI_dataTable(DataTable i_dataTable) {
		this.i_dataTable = i_dataTable;
	}

	public SessionBean getSessionBean() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public boolean isShowSkipped() {
		return showSkipped;
	}

	public void setShowSkipped(boolean showSkipped) {
		this.showSkipped = showSkipped;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getMaxStartDate() {
		return maxStartDate;
	}

	public void setMaxStartDate(Date maxStartDate) {
		this.maxStartDate = maxStartDate;
	}

	public Date getMaxEndDate() {
		return maxEndDate;
	}

	public void setMaxEndDate(Date maxEndDate) {
		this.maxEndDate = maxEndDate;
	}

	public boolean isShowDateError() {
		return showDateError;
	}

	public void setShowDateError(boolean showDateError) {
		this.showDateError = showDateError;
	}

	public String getDateError() {
		return dateError;
	}

	public void setDateError(String dateError) {
		this.dateError = dateError;
	}
}
