package org.icefaces.samples.showcase.example.ace.dataTable;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.ace.model.table.RowState;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.ExpansionChangeEvent;

@ManagedBean (name = SessionBean.BEAN_NAME, eager=true)
@SessionScoped
public class SessionBean {
	
	public static final String BEAN_NAME = "sessionBean";
	private List<Composite> composites;
	public static final HashMap<Integer, Backend> BACKENDS = new HashMap<Integer, Backend>();  
	public static final HashMap<Integer,Mode> MODES = new HashMap<Integer, Mode>();
	private DataTable aceDataTable;
	
	public DataTable getAceDataTable() {
		return aceDataTable;
	}
	public void setAceDataTable(DataTable aceDataTable) {
		this.aceDataTable = aceDataTable;
	}
	
	public SessionBean(){
		this.scheduleData = this.connector.getSchedulings();
		this.composites = this.connector.getAllComposites();
		
		List<Backend> tempBackends = this.connector.getAllBackends();
		for(Backend b: tempBackends){
			BACKENDS.put(b.getId(), b);
			System.out.println(b.getDescription());
		}
		List<Mode> tempModes = this.connector.getAllModes();
		for (Mode mode : tempModes) {
			MODES.put(mode.getId(), mode);
		}
	}
	
	private SchedulingBuilder builder = new SchedulingBuilder();
	private DatabaseConnector connector = new DatabaseConnector();
	private List<Scheduling> scheduleData;
	private HashMap<Integer, SchedulingBuilder> editBuffer = new HashMap<Integer, SchedulingBuilder>();
	private RowStateMap stateMap = new RowStateMap();

	public RowStateMap getStateMap() { return stateMap; }
	public void setStateMap(RowStateMap stateMap) { this.stateMap = stateMap; }
	
	public List<Scheduling> getScheduleData() {
		return scheduleData;
	}

	public void setScheduleData(List<Scheduling> scheduleData) {
		this.scheduleData = scheduleData;
	}

	public DatabaseConnector getConnector() {
		return connector;
	}

	public void setConnector(DatabaseConnector connector) {
		this.connector = connector;
	}

	public SchedulingBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(SchedulingBuilder builder) {
		this.builder = builder;
	}
	
	public HashMap<Integer, SchedulingBuilder> getEditBuffer() {
		return editBuffer;
	}
	
	public void setEditBuffer(HashMap<Integer, SchedulingBuilder> editBuffer) {
		this.editBuffer = editBuffer;
	}
	
	
	public void addScheduling(){
		Scheduling s = this.builder.build();
		this.connector.addScheduling(s);
		this.scheduleData = this.connector.getSchedulings();
	}
	
	public void confirmEdit(Scheduling s){
		
		this.editBuffer.get(s.getId()).sync(s);
		
		//System.out.println(this.connector.updateScheduling(s));
	}
	
	
	
	public void expansion(ExpansionChangeEvent e){
		if(e.isExpanded())
			this.editBuffer.put(((Scheduling)e.getRowData()).getId(),new SchedulingBuilder((Scheduling)e.getRowData()));
		else
			this.editBuffer.remove(((Scheduling)e.getRowData()).getId());
	}
	
	public void resetEdit(Scheduling s){
		this.editBuffer.put(s.getId(), new SchedulingBuilder(s));
	}
	
	
	public void SelectAll(){
		List<Scheduling> filteredRows = aceDataTable.getFilteredData();
		if(filteredRows == null || filteredRows.isEmpty()) {
			stateMap.setAllSelected(true);
			return;
		}
        for (Scheduling s : filteredRows) {
            stateMap.get(s).setSelected(true);
            System.out.println(s.getName());
        }
	}
	
	public void DeSelectAll(){
		Collection<RowState> allRows = stateMap.values();
        
        for (RowState s : allRows) {
            s.setSelected(false);
        }
		System.out.println("All rows un-selected");
	}
	
	public void StopSelected(){
	      for (Object rowData : stateMap.getSelected()) {
	    	  Scheduling s = (Scheduling) rowData;
	    	  s.setStatusID(0);
	    	  //edit(s); ?
	        }
	}
	
	public void saveTableState(){
		
	}
	
	
}
