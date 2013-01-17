package datalogic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.model.table.RowStateMap;
import org.quartz.CronExpression;

import entities.Backend;
import entities.Comment;
import entities.Composite;
import entities.Mode;
import entities.Scheduling;

@ManagedBean(name = "schedulingDataManager")
@SessionScoped
public class SchedulingDataManager {

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	private SchedulingBuilder builder;
	private Comment addComment = new Comment();

	private List<Scheduling> schedulings = new ArrayList<Scheduling>();
	private RowStateMap stateMap = new RowStateMap();
	private DataTable dataTable;

	private Collection<Mode> modes = ApplicationBean.MODES.values();
	private Collection<Composite> composites = ApplicationBean.COMPOSITES
			.values();
	private Collection<Backend> backends = ApplicationBean.BACKENDS.values();

	private boolean showEditError;
	private String editErrorMessage;

	private boolean showAddError;
	private String addErrorMessage;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");

	private HttpConnector httpConnector = new HttpConnector();

	private boolean responseDialogVisible;
	private String runReport;

	private String schedulingList;

	private Date startDate = new Date();
	private Date endDate = new Date();

	private List<Scheduling> matching;

	@PostConstruct
	private void init() {
		this.builder = new SchedulingBuilder();
		this.schedulings = this.session.getSchedulings();
	}

	public void listSelected() {
		if (stateMap.getSelected().isEmpty()) {
			this.schedulingList = "None";
			return;
		}

		this.schedulingList = "";
		for (Scheduling s : (List<Scheduling>) stateMap.getSelected()) {
			this.schedulingList += s.getName().substring(0, 4) + "<br/>";
		}
	}

	/**
	 * Method to be called from the UI when a new {@link Scheduling} is to be
	 * constructed from the {@link SchedulingBuilder}
	 */
	public void addScheduling() {

		try {

			/*
			 * Build the Scheduling from values inserted to the builder. Values
			 * are validated within the builder and an IllegalOperationException
			 * will be thrown if the values are invalid
			 */
			Scheduling s = this.builder.build();

			// If the database connector returns true from the persisting of
			// Scheduling we can safely add it to the table
			if (this.session.getConnector().addScheduling(s)) {
				this.schedulings.add(s);
				this.builder = new SchedulingBuilder();
			}

			/*
			 * If there is some text in addComment, we'll add it straight away.
			 * It will be connected by the ID of previously added Scheduling so
			 * its important to add the Comment after the Scheduling has been
			 * persisted and an ID has been assigned to it.
			 */
			if (addComment.getText() != null
					&& !"".equals(addComment.getText())) {
				addComment.setSchedulingID(s.getId());
				addComment.setCreationDate(DATE_FORMAT.format(new Date()));
				session.getConnector().addComment(addComment);
			}

			// If we've gotten this far, there were no errors and we can hide
			// all error messages
			showAddError = false;

			// Http request, consult Hanzki for any details
			System.out.println("HttpConnector returned: "
					+ httpConnector.addId(
							ApplicationBean.COMPOSITES.get(s.getServiceID())
									.getDestinationURL(), s.getId()));
		} catch (IllegalOperationException e) {

			/*
			 * An error was thrown during the validation of the Scheduling: we
			 * set the error message to be shown in Add Scheduling section and
			 * set the visibility to true
			 */
			addErrorMessage = e.getMessage();
			showAddError = true;
		}
	}

	public void confirmEdit(SchedulingTab t) {

		try {

			/*
			 * Retrieve the SchedulingBuilder corresponding to the id of
			 * Scheduling passed as a parameter from the UI and build it.
			 * Validation is carried out normally within the builder
			 */
			Scheduling n = t.getBuilder().build();

			/*
			 * In order to refresh the DataTable we need to "brute force" the
			 * change by removing the unedited Scheduling and adding the newly
			 * built one
			 */
			this.schedulings.remove(t.getScheduling());
			this.schedulings.add(n);
			t.setScheduling(n);

			// this.session.getConnector().updateScheduling(n);

			// If we've gotten this far there were no errors and we can hide all
			// error messages
			showEditError = false;

			// Http request, consult Hanzki for any details
			System.out.println("HttpConnector returned: "
					+ httpConnector.editId(
							ApplicationBean.COMPOSITES.get(
									t.getScheduling().getServiceID())
									.getDestinationURL(), t.getScheduling()
									.getId()));
		} catch (IllegalOperationException e) {

			/*
			 * An error was thrown during the validation of the edited
			 * Scheduling: we set the error message to be shown in Edit section
			 * and set the visibility to true
			 */
			editErrorMessage = e.getMessage();
			showEditError = true;
		}

	}

	public void submitComment(SchedulingTab t) {
		Comment c = t.getAddComment();

		c.setCreationDate(DATE_FORMAT.format(new Date()));
		c.setSchedulingID(t.getScheduling().getId());

		// If the database connector returns true from the persisting of the
		// comment we can safely add it to the table
		if (this.session.getConnector().addComment(c)) {
			t.setAddComment(new Comment());
			t.setComments(this.session.getConnector().getLastComments(
					t.getScheduling().getId()));
		}
	}

	/**
	 * Method to be called from the UI when Select All button is pressed and all
	 * rows currently filtered (or lazily loaded) are to be selected. The [@link
	 * DataTable} is bound to this bean so we can operate on the filtered data.
	 */
	public void selectFiltered() {

		List<Scheduling> filteredRows = dataTable.getFilteredData();

		/*
		 * If the filtered data is empty or null, we do not currently have a
		 * filter. To prevent a NullPointerException we use the standard method
		 * of the stateMap for selecting all rows
		 */
		if (filteredRows == null || filteredRows.isEmpty()) {
			return;
		}

		// Iterate through the filtered data and set all rows to be selected
		for (Scheduling s : filteredRows) {
			stateMap.get(s).setSelected(true);
		}
	}

	/**
	 * Method to be called from the UI when Deselect All button is pressed and
	 * all selected rows are to be deselected.
	 */
	public void deselectAll() {
		Collection<RowState> allRows = stateMap.values();

		for (RowState s : allRows) {
			s.setSelected(false);
		}
	}

	// TODO Comments
	public void runSelectedSchedules() {
		String runReport = stateMap.getSelected().size()
				+ " schedulings were run.\n";
		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;
			runReport += "Response for scheduling "
					+ s.getName().substring(0, 4)
					+ " was "
					+ httpConnector.runId(
							ApplicationBean.COMPOSITES.get(s.getServiceID())
									.getDestinationURL(), s.getId()) + ".\n";
		}
		this.runReport = runReport;
		this.responseDialogVisible = true;
	}

	// TODO comments
	public void resumeSelected() {

		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;

			if (s.getStatusID() != ApplicationBean.REMOVED) {
				s.setStatusID(ApplicationBean.ENABLED);

				// this.session.getConnector().updateScheduling(s);
				System.out.println("HttpConnector returned: "
						+ httpConnector.editId(
								ApplicationBean.COMPOSITES
										.get(s.getServiceID())
										.getDestinationURL(), s.getId()));
			}
		}
	}

	// TODO comments
	public void holdSelected() {

		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;

			if (s.getStatusID() != ApplicationBean.REMOVED) {
				s.setStatusID(ApplicationBean.DISABLED);

				// this.session.getConnector().updateScheduling(s);
				System.out.println("HttpConnector returned: "
						+ httpConnector.editId(
								ApplicationBean.COMPOSITES
										.get(s.getServiceID())
										.getDestinationURL(), s.getId()));
			}
		}
	}

	/**
	 * Method for refreshing the contents of the data table.
	 */
	public void refresh() {
		this.schedulings.clear();
		this.session.refreshSchedulings();
		this.schedulings = this.session.getSchedulings();
	}

	public void dateSubmit() {
		Date today = new Date();
		if(this.startDate.after(today) || this.endDate.after(today))
			return;
		if(this.endDate.equals(this.startDate) || this.endDate.before(this.startDate))
			return;
		this.matching = new ArrayList<Scheduling>();
		for (Scheduling s : this.schedulings) {

			if (s.getStatusID() != 1)
				continue;

			CronExpression cron;
			try {
				cron = new CronExpression(s.getCron());
				
				if (cron.getNextValidTimeAfter(this.startDate).before(
						this.endDate)
						&& cron.getNextValidTimeAfter(this.endDate)
								.after(today)) {

					Calendar cal = Calendar.getInstance(); // creates calendar
					cal.setTime(today); // sets calendar time/date
					cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
					if (cron.getNextValidTimeAfter(today).after(cal.getTime()))
						this.matching.add(s);

				}
			} catch (ParseException e) {
				System.out.println("HIENO CRONI HERMANNI " + s.getCron());
			}
		}

	}

	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}

	public SchedulingBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(SchedulingBuilder builder) {
		this.builder = builder;
	}

	public Comment getAddComment() {
		return addComment;
	}

	public void setAddComment(Comment addComment) {
		this.addComment = addComment;
	}

	public List<Scheduling> getSchedulings() {
		return schedulings;
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

	public boolean isShowEditError() {
		return showEditError;
	}

	public void setEditError(boolean editError) {
		// Cannot be set from the UI but the setter has to exist
	}

	public String getEditErrorMessage() {
		return editErrorMessage;
	}

	public void setEditErrorMessage(String editErrorMessage) {
		this.editErrorMessage = editErrorMessage;
	}

	public boolean isShowAddError() {
		return showAddError;
	}

	public void setShowAddError(boolean addError) {
		// Cannot be set from the UI but the setter has to exist
	}

	public String getAddErrorMessage() {
		return addErrorMessage;
	}

	public void setAddErrorMessage(String addErrorMessage) {
		this.addErrorMessage = addErrorMessage;
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
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

	public boolean isResponseDialogVisible() {
		return responseDialogVisible;
	}

	public void setResponseDialogVisible(boolean responseDialogVisible) {
		this.responseDialogVisible = responseDialogVisible;
	}

	public String getRunReport() {
		return runReport;
	}

	public void setRunReport(String runReport) {
		this.runReport = runReport;
	}

	public String getSchedulingList() {
		return schedulingList;
	}

	public void setSchedulingList(String schedulingList) {
		this.schedulingList = schedulingList;
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

	public List<Scheduling> getMatching() {
		return matching;
	}

	public void setMatching(List<Scheduling> matching) {
		this.matching = matching;
	}

}
