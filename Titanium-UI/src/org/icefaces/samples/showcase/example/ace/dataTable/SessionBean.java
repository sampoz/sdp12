package org.icefaces.samples.showcase.example.ace.dataTable;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.ace.model.table.RowState;

import java.awt.Event;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.FacesListener;

import org.icefaces.ace.event.ExpansionChangeEvent;
import org.icefaces.ace.event.RowEditEvent;

@ManagedBean (name = SessionBean.BEAN_NAME, eager=true)
@SessionScoped
public class SessionBean {
	
	public static final String BEAN_NAME = "sessionBean";
	
	public SessionBean(){
		this.scheduleData = this.connector.getSchedulings();
	}
	
	private SchedulingBuilder builder = new SchedulingBuilder();
	private DatabaseConnector connector = new DatabaseConnector();
	private List<Scheduling> scheduleData;
	private Scheduling stored;
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
	
	public void addScheduling(){
		Scheduling s = this.builder.build();
		this.connector.addScheduling(s);
		this.scheduleData = this.connector.getSchedulings();
	}
	
	public void edit(Scheduling e){
		
		System.out.println(this.connector.updateScheduling(e));
	}
	
	public void sysout(){
		System.out.println("DERP");
	}
	
	public void openRow(ExpansionChangeEvent e){
		
		if(e.isExpanded())
			
			System.out.println(((Scheduling)e.getRowData()).getName());
		else
			System.out.println("Close");
	}
	public void SelectAll(){
		Collection<RowState> allRows = stateMap.values();
        
        for (RowState s : allRows) {
            s.setSelected(true);
        }
		System.out.println("All rows selected");
	}
	public void DeSelectAll(){
		Collection<RowState> allRows = stateMap.values();
        
        for (RowState s : allRows) {
            s.setSelected(false);
        }
		System.out.println("All rows un-selected");
	}
	public void StopSelected(){
		System.out.println("pamp");
	      for (Object rowData : stateMap.getSelected()) {
	    	  Scheduling s = (Scheduling) rowData;
	    	  s.setStatusID(0);
	    	  //edit(s); ?
	        }
	}
	
}
