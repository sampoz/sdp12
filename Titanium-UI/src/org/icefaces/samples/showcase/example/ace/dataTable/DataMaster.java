package org.icefaces.samples.showcase.example.ace.dataTable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.ExpansionChangeEvent;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.model.table.RowStateMap;

import com.icesoft.faces.component.SelectBooleanCheckboxTag;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;

import entities.Backend;
import entities.Composite;
import entities.Mode;
import entities.Scheduling;

@ManagedBean
@CustomScoped(value = "#{window}")
public class DataMaster implements Serializable {

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	public static final String BEAN_NAME = "dataMaster";

	private DataTable dataTable;

	private List<Scheduling> schedulings;

	private RowStateMap stateMap = new RowStateMap();

	private HashMap<Integer, SchedulingBuilder> editBuffer = new HashMap<Integer, SchedulingBuilder>();

	private SchedulingBuilder builder = new SchedulingBuilder();

	private Collection<Mode> modes = SessionBean.MODES.values();
	private Collection<Composite> composites = SessionBean.COMPOSITES.values();
	private Collection<Backend> backends = SessionBean.BACKENDS.values();

	public void addScheduling() {

		Scheduling s = this.builder.build();
		session.getConnector().addScheduling(s);
	}

	public void confirmEdit(Scheduling s) {

		this.editBuffer.get(s.getId()).sync(s);

		// System.out.println(this.connector.updateScheduling(s));
	}

	public void expansion(ExpansionChangeEvent e) {
		if (e.isExpanded())
			this.editBuffer.put(((Scheduling) e.getRowData()).getId(),
					new SchedulingBuilder((Scheduling) e.getRowData()));
		else
			this.editBuffer.remove(((Scheduling) e.getRowData()).getId());
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

	public void stopSelected() {
		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;
			s.setStatusID(0);
			// edit(s); ?
		}
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
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

	public void refresh() {
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

	public HashMap<Integer, SchedulingBuilder> getEditBuffer() {
		return editBuffer;
	}

	public void setEditBuffer(HashMap<Integer, SchedulingBuilder> editBuffer) {
		this.editBuffer = editBuffer;
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

}
