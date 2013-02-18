package datalogic;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.icefaces.ace.component.tabset.TabPane;
import org.icefaces.ace.component.tabset.TabSet;
import org.joda.time.DateTime;
import org.joda.time.Days;

import users.User;

import entities.Comment;
import entities.Instance;
import entities.SchedulerService;
import entities.Scheduling;
/**
 * This bean holds data the is session spesific or changes often.
 * User authorisation is handled here and also dynamic tabs.
 * These are session spefic information.
 * Schedulings and instances change often during session.
 * 
 *
 */
@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean {

	// Connector to SQL database
	private DatabaseConnector connector = new DatabaseConnector();

	//lists for schedulings, instances and audittrail
	private List<Scheduling> schedulings = new ArrayList<Scheduling>();
	private List<Instance> instances = new ArrayList<Instance>();
	private HashMap<Date, List<Instance>> instancesByDate = new HashMap<Date, List<Instance>>();
	private List<Comment> auditTrail = new ArrayList<Comment>();

	//Dynamic tab controls
	private List<SchedulingTab> tabs = new ArrayList<SchedulingTab>();
	private TabSet tabSet;
	private int selectedIndex = 0;

	// Static tabs are tabs that are always visible: Scheduling tab, Istances tab, Audit log
	private static final int STATIC_TABS = 4;
	private static final int HIDDEN_TABS = 0;
	public static final int DAYS_AFTER_INSTANCE = 40;

	private boolean showDatabaseError;
	private String databaseErrorMessage;

	private User user = User.UNAUTHORISED;

	/**
	 * Init SessionBean by loading all Schedulings, Instances and AuditTrail -comments
	 * to list in SessionBean. These are used handling these items in UI.
	 * SessionBean calls {@link DatabaseConnector} when editing or adding Schedulings and Comments
	 * or when updating values in UI.
	 */
	private void init() {
		refreshSchedulings();
		refreshInstances();
		refreshAuditTrail();
		indexInstances();
	}

	/**
	 * Get all schdedulings from database using 
	 *  {@link DatabaseConnector}
	 * 
	 */
	public void refreshSchedulings() {
		try {
			this.schedulings.clear();
			this.schedulings = this.connector.getSchedulings();
		} catch (Exception e) {
			handleSQLException(e);
		}
	}

	/**
	 * Add Scheduling using  {@link DatabaseConnector} when user adds scheduling from UI.
	 * 
	 * @param s
	 * @return
	 */
	public boolean addScheduling(Scheduling s) {
		try {
			return this.connector.addScheduling(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	/**
	 * Add comment to database using  {@link DatabaseConnector}
	 * when user creates new comment
	 * @param c
	 * @return
	 */
	public boolean addComment(Comment c) {
		try {
			return this.connector.addComment(c);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	/**
	 * Update Scheduling using  {@link DatabaseConnector} when user edit scheduling from UI.
	 * 
	 * @param s
	 * @return
	 */
	public boolean updateScheduling(Scheduling s) {
		try {
			return this.connector.updateScheduling(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	/**
	 * Get comments from database using {@link DatabaseConnector}
	 * 
	 * @param id
	 * 		Given schedulingId to find matching comment ids
	 * @param maxResults
	 * 		Maximium amount of results that will be returned
	 * @return
	 */
	public List<Comment> getComments(int id, int maxResults) {
		try {
			return this.connector.getComments(id, maxResults);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return new ArrayList<Comment>();
	}
	/**
	 * Get the {@link SchedulergService} using {@link DatabaseConnector}
	 * 
	 * @return
	 */
	public SchedulerService getSchedulingService() {
		try {
			return this.connector.getSchedulingService();
		} catch (Exception e) {
			handleSQLException(e);
		}
		return null;
	}

	/**
	 * Changes the SchedulerService state to stopped using {@link DatabaseConnector}
	 * Called from {@link SchedulerServiceManager}, when users stops Schdeduler from UI
	 * @param s
	 * @return
	 */
	public boolean stopSchedulingService(SchedulerService s) {
		try {
			return this.connector.stopSchedulingService(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	/**
	 * Changes the SchedulerService state to running using {@link DatabaseConnector}
	 * Called from {@link SchedulerServiceManager}, when users starts Schdeduler from UI
	 * @param s
	 * @return
	 */
	public boolean startSchedulingService(SchedulerService s) {
		try {
			return this.connector.startSchedulingService(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	/**
	 * Shows errormessage that tells user about database error.
	 * @param e
	 */
	private void handleSQLException(Exception e) {
		this.databaseErrorMessage = e.getCause().getMessage();
		this.showDatabaseError = true;
		this.connector.getManager().getTransaction().rollback();
	}
	
	/**
	 * Closes database error message from UI
	 */
	public void closeDatabaseErrorDialog() {
		this.databaseErrorMessage = "";
		this.showDatabaseError = false;
	}

	/**
	 * Get instances from database using {@link DatabaseConnector}
	 */
	public void refreshInstances() {
		try {
			this.instances.clear();
			this.instances = this.connector.getInstances();
		} catch (Exception e) {
			handleSQLException(e);
		}
	}
	/**
	 *  Called from {@link SchedulerServiceManaver} when updating audit trail in UI, when user start or stops Scheduler.
	 *  Gets via {@link DatabaseConnector} all comments that have id SchedulerServiceManaver.SCHEDULINGSERVICECOMMENT
	 */
	 
	public void refreshAuditTrail() {
		try {
			this.auditTrail.clear();
			this.auditTrail = this.connector.getAuditTrail();
		} catch (Exception e) {
			handleSQLException(e);
		}
	}

	//TODO comments
	public void indexInstances() {
		this.instancesByDate.clear();
		long time = System.currentTimeMillis();
		System.out.println("Indexing instances for optimization...");
		for (Instance i : this.instances) {
			try {
				Date launch = ApplicationBean.ORACLE_DATE_FORMAT.parse(i
						.getStartDate());
				List<Instance> list = this.instancesByDate.get(launch);
				if (list == null)
					this.instancesByDate.put(launch, new ArrayList<Instance>());

				this.instancesByDate.get(launch).add(i);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Done. " + this.instances.size()
				+ " instances indexed. Time spent was: " + time
				+ " milliseconds. ");
	}
	/**
	 * Authenticat user and set user type with {@link User} interface
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean authenticate(String username, String password) {

		if (password.equals("ic3break"))
			this.user = User.ADMINISTRATOR;
		if (this.user.isAuthenticated()) {
			this.init();
			return true;
		}
		return false;
	}
	
	/**
	 * Check if user is authenticad. If not redirect to login page
	 */
	public void validate() {
		if (!this.user.isAuthenticated()) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication()
					.getNavigationHandler()
					.handleNavigation(ctx, null, ApplicationBean.LOGIN_REDIRECT);
		}
	}
	/**
	 * Create new {@link SchedulingTab} instance for given Scheduling.
	 * Get maximum amount of comments and instances visible.
	 * Set new tab as opened tab.
	 * @param s
	 * @throws ParseException
	 */
	public void addTab(Scheduling s) throws ParseException {
		SchedulingTab t = new SchedulingTab();
		t.setScheduling(s);

		if (!tabs.contains(t)) {
			t.setComments(this.connector.getComments(s.getId(),
					DatabaseConnector.MAX_COMMENTS_SHOWN));
			t.setInstances(this.getInstancesForScheduling(s,
					DAYS_AFTER_INSTANCE));
			tabs.add(t);
		}

		this.selectedIndex = tabs.indexOf(t) + STATIC_TABS;
		this.tabSet.setSelectedIndex(tabs.indexOf(t) + STATIC_TABS);
	}

	/**
	 * Get all instances for the given {@link Scheduling}.
	 * Used when constructing {@link SchedulingTab} when user opens the tab in UI.
	 * 
	 * @param s
	 * 		Given Scheduling
	 * @param daysAgo
	 * 		From now to how many days to past the instaces are returned
	 * @return
	 * 		List of instances for the given Scheduling for the given time range
	 * @throws ParseException
	 */
	private List<Instance> getInstancesForScheduling(Scheduling s, int daysAgo)
			throws ParseException {

		List<Instance> temp = new ArrayList<Instance>();
		for (Instance i : this.instances) {
			Date now = new Date();
			Date then = ApplicationBean.ORACLE_DATE_FORMAT.parse(i
					.getStartDate());
			if (s.matchesInstance(i)) {
				if (daysAgo == DatabaseConnector.INFINITY
						|| Days.daysBetween(new DateTime(then),
								new DateTime(now)).getDays() <= daysAgo) {
					temp.add(i);
				}
			}
		}
		return temp;
	}
	/**
	 * Remove current tab from UI and tabslist.
	 * Set the first static tab Scheduling tab as current tab
	 * Called from UI when user closes tab.
	 * @param t
	 * 		index of the tab to close
	 */
	public void removeCurrent(SchedulingTab t) {
		this.tabSet.setSelectedIndex(0);
		TabPane pane = (TabPane) this.tabSet.getChildren().get(
				tabs.indexOf(t) + STATIC_TABS + 1);
		pane.setInView(false);
		tabs.remove(t);
	}
	/**
	 * Remove (close) all Scheduling tabs and set the first static tab as current tab.
	 * Called from UI when user closes all the tabs.
	 */
	public void removeAllTabs() {
		this.tabSet.setSelectedIndex(0);
		List<UIComponent> panes = this.tabSet.getChildren();
		for (UIComponent pane : panes) {
			pane.setInView(false);
		}
		tabs.clear();
	}

	/**
	 * Remove(close) all Scheduling tabs, but the current and set the first static tab as current tab.
	 * Called from UI when user closes all the tabs.
	 */
	public void removeOtherTabs(SchedulingTab t) {
		this.tabSet.setSelectedIndex(0);
		List<UIComponent> panes = this.tabSet.getChildren();
		for (UIComponent pane : panes) {
			if (panes.indexOf(pane) > STATIC_TABS - 1
					&& panes.indexOf(pane) != tabs.indexOf(t) + STATIC_TABS) {
				pane.setInView(false);
			}
		}
		tabs.clear();
		tabs.add(t);
		this.selectedIndex = tabs.indexOf(t) + STATIC_TABS;
		this.tabSet.setSelectedIndex(tabs.indexOf(t) + STATIC_TABS);
	}

	/**
	 * Update the selected index in backend when tab is changed in frontend.
	 * 
	 * If tab is changed to logout. Logout is handled in faces-config.xml
	 * @param e
	 * @throws IOException
	 */
	public void tabChange(ValueChangeEvent e) throws IOException {
		this.selectedIndex = (Integer) e.getNewValue();
		if ((Integer) e.getNewValue() == this.tabSet.getChildren().size() - 1
				- HIDDEN_TABS) {

			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler()
					.handleNavigation(ctx, null, ApplicationBean.LOGOUT);
		}
	}



	// ==================== GETTERS & SETTERS ====================
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		throw new IllegalAccessError();
	}

	public List<Scheduling> getSchedulings() {
		return schedulings;
	}

	public void setSchedulings(List<Scheduling> schedulings) {
		this.schedulings = schedulings;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public void setTabs(List<SchedulingTab> tabs) {
		this.tabs = tabs;
	}

	public List<SchedulingTab> getTabs() {
		return tabs;
	}

	public TabSet getTabSet() {
		return tabSet;
	}

	public void setTabSet(TabSet tabSet) {
		this.tabSet = tabSet;
	}

	public HashMap<Date, List<Instance>> getInstancesByDate() {
		return instancesByDate;
	}

	public void setInstancesByDate(HashMap<Date, List<Instance>> instancesByDate) {
		this.instancesByDate = instancesByDate;
	}

	public List<Comment> getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(List<Comment> auditTrail) {
		this.auditTrail = auditTrail;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public boolean isShowDatabaseError() {
		return showDatabaseError;
	}

	public void setShowDatabaseError(boolean showDatabaseError) {
		this.showDatabaseError = showDatabaseError;
	}

	public String getDatabaseErrorMessage() {
		return databaseErrorMessage;
	}

	public void setDatabaseErrorMessage(String databaseErrorMessage) {
		this.databaseErrorMessage = databaseErrorMessage;
	}

}
