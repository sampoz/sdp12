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

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.ExpansionChangeEvent;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.model.table.RowStateMap;


import entities.Backend;
import entities.Composite;
import entities.Instance;
import entities.Mode;
import entities.Scheduling;

@ManagedBean
@CustomScoped(value = "#{window}")
public class DataMaster implements Serializable {

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	public static final String BEAN_NAME = "dataMaster";

	private DataTable dataTable;
	
	private DataTable i_dataTable;

	private List<Scheduling> schedulings = new ArrayList<Scheduling>();
	
	private List<Instance> instances;

	private RowStateMap stateMap = new RowStateMap();
	
	private RowStateMap i_stateMap = new RowStateMap();

	private HashMap<Integer, SchedulingBuilder> editBuffer = new HashMap<Integer, SchedulingBuilder>();

	private HttpConnector http = new HttpConnector();
	
	private SchedulingBuilder builder;

	private Collection<Mode> modes = SessionBean.MODES.values();
	private Collection<Composite> composites = SessionBean.COMPOSITES.values();
	private Collection<Backend> backends = SessionBean.BACKENDS.values();
	
	private boolean editError;
	private String editErrorMessage;
	
	private boolean addError;
	private String addErrorMessage;
	
	private HashMap<Integer, String> instanceEditBuffer = new HashMap<Integer, String>();
	
	@PostConstruct
	private void init(){
		this.builder = new SchedulingBuilder();
	}
	
	public void addScheduling() {

		Scheduling s;
		try {
			s = this.builder.build();
			session.getConnector().addScheduling(s);
			addError = false;
			System.out.print("HttpConnector returned: " + http.addId(SessionBean.COMPOSITES.get(s.getServiceID()).getDestinationURL(), s.getId()));
		} catch (IllegalOperationException e) {
			addErrorMessage = e.getMessage();
			addError = true;
		}
		
	}

	public void confirmEdit(Scheduling s) {

		try {
			
			Scheduling n = this.editBuffer.get(s.getId()).build();
			this.schedulings.remove(s);
			this.schedulings.add(n);

			//this.session.getConnector().updateScheduling(n);
			editError = false;
			System.out.print("HttpConnector returned: " + http.editId(SessionBean.COMPOSITES.get(s.getServiceID()).getDestinationURL(), s.getId()));
		} catch (IllegalOperationException e) {
			editErrorMessage = e.getMessage();
			editError = true;
		}

	}

	public void expansion(ExpansionChangeEvent e) {
		if (e.isExpanded()){
			this.editBuffer.put(((Scheduling) e.getRowData()).getId(),
					new SchedulingBuilder((Scheduling) e.getRowData()));
			this.editError = false;}
		else {
			this.editBuffer.remove(((Scheduling) e.getRowData()).getId());
		}
	}

	public void instanceExpansion(ExpansionChangeEvent e){
		String instanceErrorMessage = "g0ljuException";
		int Id = ((Instance) e.getRowData()).getId();
		if (e.isExpanded()){
				int statusId = ((Instance) e.getRowData()).getStatusID();
		
				if (statusId != 6) {
					instanceErrorMessage = ((Instance) e.getRowData()).getName();
				}
				
				this.instanceEditBuffer.put(Id, instanceErrorMessage);
			}
		else {
			this.instanceEditBuffer.remove(Id);
		}
		
	}
	
	public void resetEdit(Scheduling s) {
		SchedulingBuilder b = new SchedulingBuilder(s);

		this.editBuffer.put(s.getId(), new SchedulingBuilder(s));
	}

	public void selectAll() {
		List<Scheduling> filteredRows = dataTable.getFilteredData();
		if (filteredRows == null || filteredRows.isEmpty()) {
			stateMap.setAllSelected(true);
			return;
		}
		for (Scheduling s : filteredRows) {
			stateMap.get(s).setSelected(true);
		}
	}

	public void deselectAll() {
		Collection<RowState> allRows = stateMap.values();

		for (RowState s : allRows) {
			s.setSelected(false);
		}
	}
	
	public void startSelected() {
		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;

			s.setStatusID(SessionBean.ENABLED);

			//this.session.getConnector().updateScheduling(s);
			System.out.print("HttpConnector returned: " + http.editId(SessionBean.COMPOSITES.get(s.getServiceID()).getDestinationURL(), s.getId()));
		}
	}
	

	public void stopSelected() {
		
		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;

			s.setStatusID(SessionBean.DISABLED);

			//this.session.getConnector().updateScheduling(s);
			System.out.print("HttpConnector returned: " + http.editId(SessionBean.COMPOSITES.get(s.getServiceID()).getDestinationURL(), s.getId()));
		}
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

	public List<Scheduling> getSchedulings() {
		if (this.schedulings == null || this.schedulings.isEmpty()) {
			this.refresh();
		}
		return schedulings;
	}

	public List<Instance> getInstances() {
		if (this.instances == null || this.instances.isEmpty()) {
			this.refreshInstances();
		}
		return instances;
	}

	public void refreshInstances() {
		this.instances = this.session.getConnector().getInstances();
	}
	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public void refresh() {
		this.schedulings.clear();
		this.schedulings = this.session.getConnector().getSchedulings();
	}

	public void setSchedulings(List<Scheduling> schedulings) {
		this.schedulings = schedulings;
	}

	public RowStateMap getStateMap() {
		return stateMap;
	}

	public void setStateMap(RowStateMap stateMap) {
		this.stateMap = stateMap;
	}

	public RowStateMap getI_stateMap() {
		return i_stateMap;
	}

	public void setI_stateMap(RowStateMap i_stateMap) {
		this.i_stateMap = i_stateMap;
	}

	public HashMap<Integer, SchedulingBuilder> getEditBuffer() {
		return editBuffer;
	}

	public void setEditBuffer(HashMap<Integer, SchedulingBuilder> editBuffer) {
		this.editBuffer = editBuffer;
	}
	public HashMap<Integer, String> getInstanceEditBuffer() {
		return instanceEditBuffer;
	}

	public void setInstanceEditBuffer(HashMap<Integer, String> Buffer) {
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



}
