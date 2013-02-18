package datalogic;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.component.dialog.Dialog;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.model.table.RowStateMap;
import org.quartz.CronExpression;

import datalogic.SchedulingTab;

import entities.Backend;
import entities.Comment;
import entities.Composite;
import entities.Instance;
import entities.Mode;
import entities.Scheduling;

/**
 * This class is responsible for all of the actions regarding {@link Scheduling} objects and
 * the scheduling view.
 * 
 */
@ManagedBean(name = "schedulingDataManager")
@ViewScoped
public class SchedulingDataManager implements Serializable {

	// Injected bean for the current session
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	// Builder for constructing a new scheduling in the UI
	private SchedulingBuilder builder;

	/*
	 * List of schedulings, the selected rows and the binding of the
	 * corresponding data table are naturally in this class.
	 */
	private List<Scheduling> schedulings = new ArrayList<Scheduling>();
	private RowStateMap stateMap = new RowStateMap();
	private DataTable dataTable;

	/*
	 * All the needed shared data is retrieved from the application and stored
	 * in the fields of this bean so it can be used in the UI.
	 */
	private Collection<Mode> modes = ApplicationBean.MODES.values();
	private Collection<Composite> composites = ApplicationBean.COMPOSITES
			.values();
	private Collection<Backend> backends = ApplicationBean.BACKENDS.values();

	// HTTP handler
	private HttpConnector httpConnector = new HttpConnector();

	// Fields for the visibility of a response dialog and the message to be
	// shown.
	private boolean responseDialogVisible;
	private List<String> runReport = new ArrayList<String>();

	// List of schedulings to be shown in confirmation dialogs constructed into
	// one string.
	private String schedulingList;

	// Dates for searching failed runs in between
	private Date startDate;
	private Date endDate;

	// The most recent dates allowed for start and end
	private Date maxStartDate;
	private Date maxEndDate = new Date();

	// Date error visibility and the message
	private boolean showDateError = false;
	private String dateError;

	// List of matching runs in the failed runs section
	private List<Run> matching = new ArrayList<Run>();
	private RowStateMap runStateMap = new RowStateMap();

	// Max number of characters of the scheduling name shown in confirmation
	// dialogs
	private static final int SCHEDULING_NAME_LEN = 30;

	// Should addScheduling or EditScheduling dialog box be visible
	private boolean addSchedulingInf = false;
	private boolean EditSchedulingInf = false;

	// What is the error message
	private String addSchedulingMessage;
	private String editSchedulingMessage;

	// Was the adding or editing scheduling successful
	private boolean editSchedulingSuccess = false;
	private boolean addSchedulingSuccess = false;

	// Http response messages
	public static String EDIT_SUCCESS_MSG = "Changes to the scheduling have been saved.";
	public static String ADD_SUCCESS_MSG = "Scheduling was added succesfully.";
	public static String EMPTYPARAMETER_ERROR_MSG = "The Scheduling Service received empty parameter.";
	public static String INTERNAL_ERROR_MSG = "Error 500 - Internal Server Error";
	public static String UNKNOWN_ERROR_MSG = "ClientProtocolException or an IOException";

	/*
	 * After bean creation, initialize the limits for date selections, create
	 * the builder and retrieve the list of schedulings from the session bean.
	 */
	@PostConstruct
	private void init() {

		Calendar c = Calendar.getInstance();
		c.setTime(maxEndDate);
		c.add(Calendar.DATE, -1);
		this.maxStartDate = c.getTime();
		this.startDate = this.maxStartDate;
		this.endDate = this.maxEndDate;

		this.builder = new SchedulingBuilder();

		this.schedulings = this.session.getSchedulings();
	}

	/**
	 * Re-initialize the scheduling builder so the fields in the UI are cleared
	 * as well.
	 */
	public void clearScheduling() {
		this.builder = new SchedulingBuilder();
	}

	/**
	 * Whenever a confirmation dialog for selected schedulings is to be shown,
	 * the selected schedulings are listed through this method. The list is
	 * constructed into a single string and then used in the dialog.
	 */
	public void listSelected() {
		if (stateMap.getSelected().isEmpty()) {
			this.schedulingList = "None";
			return;
		}

		this.schedulingList = "";
		for (Scheduling s : (List<Scheduling>) stateMap.getSelected()) {
			this.schedulingList += getTrimmedName(s.getName());
		}
	}

	/**
	 * Whenever a confirmation dialog for selected runs is to be shown, the
	 * selected runs are listed through this method. The list is constructed
	 * into a single string and then used in the dialog.
	 */
	public void listSelectedRuns() {
		if (runStateMap.getSelected().isEmpty()) {
			this.schedulingList = "None";
			return;
		}

		this.schedulingList = "";
		for (Run r : (List<Run>) runStateMap.getSelected()) {
			this.schedulingList += getTrimmedName(r.getScheduling().getName());
		}
	}

	/**
	 * Whenever a confirmation dialog for all runs is to be shown, the runs are
	 * listed through this method. The list is constructed into a single string
	 * and then used in the dialog.
	 */
	public void listAllRuns() {
		if (this.matching.isEmpty()) {
			this.schedulingList = "None";
			return;
		}

		this.schedulingList = "";
		for (Run r : this.matching) {
			this.schedulingList += getTrimmedName(r.getScheduling().getName());
		}
	}

	// Trims the name of the scheduling provided
	private String getTrimmedName(String fullName) {
		if (fullName.length() <= SCHEDULING_NAME_LEN)
			return fullName + "<br/>";
		else
			return fullName.substring(0, SCHEDULING_NAME_LEN) + "<br/>";
	}

	/**
	 * Method to be called from the UI when a new {@link Scheduling} is to be
	 * constructed from the {@link SchedulingBuilder}
	 */
	public void addScheduling() {

		/*
		 * Build the Scheduling from values inserted to the builder. Values are
		 * validated in the UI so none of them need to be checked here.
		 */
		Scheduling s = this.builder.build();

		// If the database connector returns true from the persisting of
		// Scheduling we can safely add it to the table and continue
		if (this.session.addScheduling(s)) {
			this.schedulings.add(s);

			/*
			 * There must be a comment in the builder, which will be added right
			 * away. It will be connected by the ID of previously added
			 * Scheduling so its important to add the Comment after the
			 * Scheduling has been persisted and an ID has been assigned to it.
			 */
			this.submitCommentFromAdd(s);

			// HTTP request to inform the correct service about the added
			// scheduling
			int http_response = httpConnector.addId(ApplicationBean.COMPOSITES
					.get(s.getServiceID()).getDestinationURL(), s.getId());

			// Show the correct message in the UI based on the response
			switch (http_response) {
			case HttpConnector.RESPONSE_OK:
				setAddSchedulingMessage(ADD_SUCCESS_MSG);
				setAddSchedulingSuccess(true);
				break;
			case HttpConnector.RESPONSE_EMPTY_PARAMETER:
				setAddSchedulingMessage(EMPTYPARAMETER_ERROR_MSG);
				setAddSchedulingSuccess(false);
				break;
			case HttpConnector.RESPONSE_INTERNAL_ERROR:
				setAddSchedulingMessage(INTERNAL_ERROR_MSG);
				setAddSchedulingSuccess(false);
				break;
			case HttpConnector.RESPONSE_UNKOWN_ERROR:
				setAddSchedulingMessage(UNKNOWN_ERROR_MSG);
				setAddSchedulingSuccess(false);
				break;
			default:
				setAddSchedulingMessage(UNKNOWN_ERROR_MSG);
				break;
			}

			// Initialize a new builder
			this.builder = new SchedulingBuilder();

		} else {
			// Something went wrong with the persisting of the scheduling
			setAddSchedulingSuccess(false);
		}

		// Set message visible
		setAddSchedulingInf(true);
	}

	/**
	 * Method to be called from the UI when a {@link Scheduling} is to be edited
	 * within a {@link SchedulingTab}
	 */
	public void confirmEdit(SchedulingTab t) {

		/*
		 * Build the new version of the Scheduling from values inserted to the
		 * builder. Values are validated in the UI so none of them need to be
		 * checked here.
		 */
		Scheduling n = t.getBuilder().build();

		if (n.equals(t.getScheduling())) {
			// No changes
			return;
		}

		// If the database connector returns true from the persisting of
		// Scheduling we can safely continue
		if (this.session.updateScheduling(n)) {

			/*
			 * In order to refresh the DataTable we need to "brute force" the
			 * change by removing the unedited Scheduling and adding the newly
			 * built one
			 */
			this.schedulings.remove(t.getScheduling());
			this.schedulings.add(n);

			/*
			 * There must be a comment in the builder, which will be added right
			 * away. It will be connected by the ID of previously edited
			 * Scheduling
			 */
			this.submitCommentFromEdit(t);

			// Set the scheduling of the tab to the edited one
			t.setScheduling(n);

			// HTTP request to inform the correct service about the edited
			// scheduling
			int http_response = httpConnector.editId(ApplicationBean.COMPOSITES
					.get(t.getScheduling().getServiceID()).getDestinationURL(),
					t.getScheduling().getId());

			switch (http_response) {
			case HttpConnector.RESPONSE_OK:
				setEditSchedulingMessage(EDIT_SUCCESS_MSG);
				setEditSchedulingSuccess(true);
				break;
			case HttpConnector.RESPONSE_EMPTY_PARAMETER:
				setEditSchedulingMessage(EMPTYPARAMETER_ERROR_MSG);
				setEditSchedulingSuccess(false);
				break;
			case HttpConnector.RESPONSE_INTERNAL_ERROR:
				setEditSchedulingMessage(INTERNAL_ERROR_MSG);
				setEditSchedulingSuccess(false);
				break;
			case HttpConnector.RESPONSE_UNKOWN_ERROR:
				setEditSchedulingMessage(UNKNOWN_ERROR_MSG);
				setEditSchedulingSuccess(false);
				break;
			default:
				setEditSchedulingMessage(UNKNOWN_ERROR_MSG);
				setEditSchedulingSuccess(false);
				break;
			}

		} else {
			// Something went wrong with the database
			setEditSchedulingSuccess(false);
		}
		setEditSchedulingInf(true);

	}

	/**
	 * Reset the scheduling builder in the provided tab to the initial values
	 * 
	 * @param t
	 */
	public void resetEdit(SchedulingTab t) {
		t.reset();
	}

	// Submit a comment when a new scheduling is added
	private void submitCommentFromAdd(Scheduling s) {
		this.builder.getComment().setSchedulingID(s.getId());
		this.builder.getComment().setCreationDate(
				ApplicationBean.DATE_FORMAT.format(new Date()));
		if (session.addComment(this.builder.getComment())) {
			this.builder.setComment(new Comment());
		}
	}

	// Submit a comment when a scheduling is edited
	private void submitCommentFromEdit(SchedulingTab t) {
		if (this.submitComment(t.getBuilder().getComment(), t))
			t.getBuilder().setComment((new Comment()));
	}

	private boolean submitComment(Comment c, SchedulingTab t) {
		c.setCreationDate(ApplicationBean.DATE_FORMAT.format(new Date()));
		c.setSchedulingID(t.getScheduling().getId());

		if (this.session.addComment(c)) {
			refreshComments(t);
			return true;
		}
		return false;
	}

	// Refresh comments of a tab
	private void refreshComments(SchedulingTab t) {
		t.setComments(this.session.getComments(t.getScheduling().getId(),
				DatabaseConnector.MAX_COMMENTS_SHOWN));
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
		 * filter and do not wish to select anything.
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

	/**
	 * Method for running the selected schedules.
	 */
	public void runSelectedSchedules() {
		run(stateMap.getSelected());
	}

	/**
	 * Method for changing the mode of the selected schedules to active unless
	 * they have been removed.
	 */
	public void resumeSelected() {

		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;

			// If the status is removed we'll skip the scheduling
			if (s.getStatusID() != Scheduling.REMOVED) {
				s.setStatusID(Scheduling.ENABLED);

				this.session.updateScheduling(s);

				// HTTP request to inform the service about the change
				httpConnector.editId(
						ApplicationBean.COMPOSITES.get(s.getServiceID())
								.getDestinationURL(), s.getId());
			}
		}
	}

	/**
	 * Method for changing the mode of the selected schedules to deactivated
	 * unless they have been removed.
	 */
	public void holdSelected() {

		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;

			// If the status is removed we'll skip the scheduling
			if (s.getStatusID() != Scheduling.REMOVED) {
				s.setStatusID(Scheduling.DISABLED);

				this.session.updateScheduling(s);

				// HTTP request to inform the service about the change
				System.out.println("HttpConnector returned: "
						+ httpConnector.editId(
								ApplicationBean.COMPOSITES
										.get(s.getServiceID())
										.getDestinationURL(), s.getId()));
			}
		}
	}

	/**
	 * Method for changing the mode of the selected schedules to removed.
	 */
	public void removeSelected() {

		for (Object rowData : stateMap.getSelected()) {
			Scheduling s = (Scheduling) rowData;

			// If the status is removed we'll skip the scheduling
			if (s.getStatusID() != Scheduling.REMOVED) {
				s.setStatusID(Scheduling.REMOVED);

				this.session.updateScheduling(s);

				// HTTP request to inform the service about the change
				System.out.println("HttpConnector returned: "
						+ httpConnector.editId(
								ApplicationBean.COMPOSITES
										.get(s.getServiceID())
										.getDestinationURL(), s.getId()));
			}
		}
	}

	/**
	 * Method for refreshing the contents of the data table. We need to tell
	 * {@link SessionBean} to refresh the [@link Scheduling} list and then
	 * retrieve it to a local variable.
	 */
	public void refresh() {
		this.schedulings.clear();
		this.session.refreshSchedulings();
		this.schedulings = this.session.getSchedulings();
	}

	/**
	 * Executed when a date interval is submitted from the UI. We want to search
	 * for active {@link Scheduling} instances that should've had a CRON trigger
	 * during the interval. Then we check for any {@link Instance} objects
	 * belonging to the {@link Scheduling} and with a date matching that trigger
	 * . If we don't find any matches or the matching {@link Instance} failed,
	 * we want to show the {@link Scheduling} in UI.
	 */
	public void dateSubmit() {

		try {
			if (validateDates(this.startDate, this.endDate));
				;
			this.showDateError = false;
		} catch (IllegalOperationException e) {
			this.dateError = e.getMessage();
			this.showDateError = true;
			return;
		}

		Date today = new Date();

		// Clear any previous results
		this.matching = new ArrayList<Run>();

		for (Scheduling s : this.schedulings) {

			// If the status isn't active we can skip this scheduling
			if (s.getStatusID() != Scheduling.ENABLED)
				continue;

			try {

				CronExpression cron = new CronExpression(s.getCron());

				/*
				 * Initialize a calendar and retrieve date one hour from this
				 * moment
				 */
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				cal.add(Calendar.HOUR_OF_DAY, 1);

				// Earliest allowed launch from this moment
				Date threshold = cal.getTime();

				// The last launch between start date and end date
				Date prev = this.getPreviousLaunch(cron, this.startDate,
						this.endDate);

				// Next launch from this moment
				Date next = cron.getNextValidTimeAfter(today);

				/*
				 * Check that previous run is after given start date (this
				 * should never happen but just to be sure) and before given end
				 * date. Also check that next run isn't earlier than one hour
				 * from now
				 */
				if (prev != null && prev.after(this.startDate)
						&& prev.before(this.endDate) && next.after(threshold)) {

					// Get instances matching the previous run
					List<Instance> instances = this.session
							.getInstancesByDate().get(prev);

					Run r = new Run();
					r.setScheduling(s);
					r.setNext(next.toString());
					r.setPrev(prev.toString());

					/*
					 * If instances is null, we know Scheduling was not run and
					 * can add it to the list straight away
					 */
					if (instances == null || instances.isEmpty()) {
						r.setStatus("None");
						this.matching.add(r);
					} else {

						// If there were instances, iterate through them
						for (Instance i : instances) {

							/*
							 * We want the instance to match the scheduling, but
							 * its status musn't be Completed or Skipped. If we
							 * get a match, no need to look further
							 */
							if (s.matchesInstance(i)
									&& !(i.getStatusValue().equals("Completed") || i
											.getStatusValue().equals("Skipped"))) {
								r.setStatus(i.getStatusValue());
								this.matching.add(r);
								break;
							}
						}

					}
				}

			} catch (ParseException e) {
				// The CRON was invalid, just ignore it as it has been manually
				// imported (UI doesn't allow invalid Crons to be added)
			}
		}

	}

	// Validate that the given interval is valid
	private boolean validateDates(Date start, Date end)
			throws IllegalOperationException {
		boolean error = false;
		Date today = new Date();
		String msg = "";
		if (this.startDate.after(today)) {
			error = true;
			msg += "Start date cannot be after present!\n";
		}

		if (this.endDate.after(today)) {
			error = true;
			msg += "End date cannot be after present!\n";
		}

		if (this.endDate.equals(this.startDate)) {
			error = true;
			msg += "End date cannot be the same as start date!\n";
		}

		if (this.endDate.before(this.startDate)) {
			error = true;
			msg += "End date cannot be before start date!\n";
		}

		if (error)
			throw new IllegalOperationException(msg);

		return true;
	}

	/*
	 * Helper method to retrieve the previous run of the CRON between given
	 * dates since CronExpression doesn't have this functionality.
	 */
	private Date getPreviousLaunch(CronExpression cron, Date min, Date max) {
		Date candidate = min;
		do {
			candidate = cron.getNextValidTimeAfter(candidate);
		} while (cron.getNextValidTimeAfter(candidate).before(max));

		if (candidate.before(max))
			return candidate;
		else
			return null;
	}

	/**
	 * Deselect all selected runs in the failed runs section
	 */
	public void deselectAllRuns() {
		Collection<RowState> allRows = runStateMap.values();

		for (RowState r : allRows) {
			r.setSelected(false);
		}
	}

	/**
	 * Run all schedulings corresponding to selected runs in the failed runs
	 * section
	 */
	public void runSelectedRuns() {
		List<Scheduling> schedulings = new ArrayList<Scheduling>();
		for (Object r : runStateMap.getSelected()) {
			schedulings.add(((Run) r).getScheduling());
		}

		run(schedulings);
	}

	/**
	 * Run all schedulings corresponding to all runs in the failed runs section
	 */
	public void runAllRuns() {
		List<Scheduling> schedulings = new ArrayList<Scheduling>();
		for (Run r : this.matching) {
			schedulings.add(r.getScheduling());
		}

		run(schedulings);
	}

	// Run the list of schedulings provided and construct a response message for
	// the dialog.
	private void run(List<Scheduling> schedulings) {
		this.runReport.clear();
		int succesfulRuns = 0;
		String failedName = null;
		int error = 0;
		for (Scheduling s : schedulings) {
			int response = httpConnector.runId(
					ApplicationBean.COMPOSITES.get(s.getServiceID())
							.getDestinationURL(), s.getId());

			if (response == HttpConnector.RESPONSE_OK) {
				succesfulRuns++;
			} else {
				failedName = getTrimmedName(s.getName());
				error = response;
				break;
			}
		}

		if (failedName != null) {
			this.runReport.add((succesfulRuns + 1) + " schedulings were run.");
			this.runReport.add(succesfulRuns + " schedulings were succesful.");
			this.runReport.add("Response for scheduling " + failedName
					+ " was " + error + ".");
		} else if (succesfulRuns > 0) {
			this.runReport.add(succesfulRuns + " schedulings were run.");

			this.runReport.add(succesfulRuns + " schedulings were succesful.");
		} else
			this.runReport.add("No schedulings were run.");
		this.responseDialogVisible = true;
	}
	
	/**
	 * Close run report dialog.
	 */
	public void closeRunReport() {
		this.responseDialogVisible = false;
	}
	
	/**
	 * Close add new scheduling report dialog.
	 */
	public void closeAddSchedulingInf() {
		this.addSchedulingInf = false;
	}
	
	/**
	 * Close edit scheduling report dialog.
	 */
	public void closeEditSchedulingInf() {
		this.EditSchedulingInf = false;
	}

	// ==================== GETTERS & SETTERS ====================
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

	public void setShowAddError(boolean addError) {
		// Cannot be set from the UI but the setter has to exist
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

	public List<String> getRunReport() {
		return runReport;
	}

	public void setRunReport(List<String> runReport) {
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

	public List<Run> getMatching() {
		return matching;
	}

	public void setMatching(List<Run> matching) {
		this.matching = matching;
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

	public String getDateError() {
		return dateError;
	}

	public void setDateError(String dateError) {
		this.dateError = dateError;
	}

	public boolean getShowDateError() {
		return showDateError;
	}

	public void setShowDateError(boolean showDateError) {
		this.showDateError = showDateError;
	}

	public RowStateMap getRunStateMap() {
		return runStateMap;
	}

	public void setRunStateMap(RowStateMap runStateMap) {
		this.runStateMap = runStateMap;
	}

	public boolean getAddSchedulingInf() {
		return addSchedulingInf;
	}

	public void setAddSchedulingInf(boolean addSchedulingInf) {
		this.addSchedulingInf = addSchedulingInf;
	}

	public Object getAddSchedulingMessage() {
		return addSchedulingMessage;
	}

	public void setAddSchedulingMessage(String addSchedulingMessage) {
		this.addSchedulingMessage = addSchedulingMessage;
	}

	public boolean getEditSchedulingInf() {
		return EditSchedulingInf;
	}

	public void setEditSchedulingInf(boolean editSchedulingInf) {
		EditSchedulingInf = editSchedulingInf;
	}

	public String getEditSchedulingMessage() {
		return editSchedulingMessage;
	}

	public void setEditSchedulingMessage(String editSchedulingMessage) {
		this.editSchedulingMessage = editSchedulingMessage;
	}

	public boolean getEditSchedulingSucces() {
		return editSchedulingSuccess;
	}

	public void setEditSchedulingSuccess(boolean editSchedulingSuccess) {
		this.editSchedulingSuccess = editSchedulingSuccess;
	}

	public boolean getAddSchedulingSuccess() {
		return addSchedulingSuccess;
	}

	public void setAddSchedulingSuccess(boolean addSchedulingSuccess) {
		this.addSchedulingSuccess = addSchedulingSuccess;
	}

}
