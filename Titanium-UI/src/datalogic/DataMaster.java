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
import javax.faces.event.ValueChangeEvent;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.ExpansionChangeEvent;
import org.icefaces.ace.model.table.RowStateMap;

import antlr.debug.NewLineEvent;

import entities.Backend;
import entities.Composite;
import entities.Instance;
import entities.Mode;

@ManagedBean
@CustomScoped(value = "#{window}")
public class DataMaster implements Serializable {

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	public static final String BEAN_NAME = "dataMaster";

	private DataTable dataTable;

	private DataTable i_dataTable;

	private List<Instance> instances = new ArrayList<Instance>();
	
	private List<Instance> filteredInstances = new ArrayList<Instance>();

	private RowStateMap i_stateMap = new RowStateMap();

	private HttpConnector http = new HttpConnector();

	private SchedulingBuilder builder;

	private Collection<Mode> modes = SessionBean.MODES.values();
	private Collection<Composite> composites = SessionBean.COMPOSITES.values();
	private Collection<Backend> backends = SessionBean.BACKENDS.values();
	

	private boolean editError;
	private String editErrorMessage;

	private boolean addError;
	private String addErrorMessage;
	private HashMap<Integer, Instance> instanceEditBuffer = new HashMap<Integer, Instance>();

	private boolean showSkipped;
	
	@PostConstruct
	private void init() {
		this.builder = new SchedulingBuilder();
	}

	public void instanceExpansion(ExpansionChangeEvent e){
		int Id = ((Instance) e.getRowData()).getId();
		if (e.isExpanded()){
			this.instanceEditBuffer.put(Id, (Instance) e.getRowData());
			}
		else {
			this.instanceEditBuffer.remove(Id);
		}
		
	}

	public void runInstances() {
		System.out.println("run instances");
	}
	
	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
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
		if (this.instances == null || this.instances.isEmpty()) {
			System.out.println("sql call from getInstances()");
			this.refreshInstances();
		}
		return instances;
//		if (this.showSkipped) return instances;
		
//		List<Instance> Return = new ArrayList<Instance>();
//		for(Instance i: instances){
//			if(i.getStatusID() != 6) Return.add(i);
//		}
//		return Return;
	}

	public void refreshInstances() {
		this.instances = this.session.getConnector().getInstances();
		filterInstances();
	}
	
	public void filterInstances() {
		if(this.showSkipped) this.filteredInstances = this.instances;
		else {
			this.filteredInstances = new ArrayList<Instance>();
			for(Instance i: this.instances){
				if(i.getStatusID() != 6) this.filteredInstances.add(i);
			}
		}
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	

	public List<Instance> getFilteredInstances() {
		if (this.instances == null || this.instances.isEmpty()) {
			System.out.println("sql call from getFilteredInstances()");
			this.refreshInstances();
		}
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

	public HashMap<Integer, Instance> getInstanceEditBuffer() {
		return instanceEditBuffer;
	}

	public void setInstanceEditBuffer(HashMap<Integer, Instance> Buffer) {
		this.instanceEditBuffer = Buffer;
	}

	public SchedulingBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(SchedulingBuilder builder) {
		this.builder = builder;
	}

	public Collection<Mode> getModes() {
		return modes;
	}

	public void setModes(Collection<Mode> modes) {
		this.modes = modes;
	}

	public Collection<Composite> getComposites() {
		return composites;
	}

	public void setComposites(Collection<Composite> composites) {
		this.composites = composites;
	}

	public Collection<Backend> getBackends() {
		return backends;
	}

	public void setBackends(Collection<Backend> backends) {
		this.backends = backends;
	}

	public boolean isEditError() {
		return editError;
	}

	public void setEditError(boolean editError) {
		this.editError = editError;
	}

	public String getEditErrorMessage() {
		return editErrorMessage;
	}

	public void setEditErrorMessage(String editErrorMessage) {
		this.editErrorMessage = editErrorMessage;
	}

	public boolean isAddError() {
		return addError;
	}

	public void setAddError(boolean addError) {
		this.addError = addError;
	}

	public String getAddErrorMessage() {
		return addErrorMessage;
	}

	public void setAddErrorMessage(String addErrorMessage) {
		this.addErrorMessage = addErrorMessage;
	}

	public void showSkippedMethod(ValueChangeEvent e){
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
