package datalogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.ExpansionChangeEvent;
import org.icefaces.ace.model.table.RowStateMap;

import entities.Backend;
import entities.Composite;
import entities.Instance;
import entities.Mode;

@ManagedBean(name="instanceDataManager")
@SessionScoped
public class InstanceDataManager implements Serializable {

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	private DataTable i_dataTable;

	private List<Instance> instances = new ArrayList<Instance>();

	private List<Instance> filteredInstances = new ArrayList<Instance>();

	private RowStateMap i_stateMap = new RowStateMap();

	private boolean showSkipped;

	@PostConstruct
	private void init() {
		this.session.initInstanceManager(this);
	}

	public void runInstances() {
		System.out.println("run instances");
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

	public void refreshInstances() {
		this.instances.clear();
		this.session.refreshInstances();
		this.instances = this.session.getInstances();
		filterInstances();
	}

	public void filterInstances() {
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

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public List<Instance> getFilteredInstances() {
		return filteredInstances;
	}

	public void setFilteredInstances(List<Instance> filteredInstances) {
		this.filteredInstances = filteredInstances;
	}

	public RowStateMap getI_stateMap() {
		return i_stateMap;
	}

	public void setI_stateMap(RowStateMap i_stateMap) {
		this.i_stateMap = i_stateMap;
	}

	public void showSkippedMethod(ValueChangeEvent e) {
		this.showSkipped = Boolean.parseBoolean(e.getNewValue().toString());
		this.filterInstances();
	}

	public boolean isShowSkipped() {
		return showSkipped;
	}

	public void setShowSkipped(boolean showSkipped) {
		this.showSkipped = showSkipped;
	}
}
