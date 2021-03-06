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

@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean {

	private DatabaseConnector connector = new DatabaseConnector();

	private List<Scheduling> schedulings = new ArrayList<Scheduling>();
	private List<Instance> instances = new ArrayList<Instance>();
	private HashMap<Date, List<Instance>> instancesByDate = new HashMap<Date, List<Instance>>();
	private List<Comment> auditTrail = new ArrayList<Comment>();

	private List<SchedulingTab> tabs = new ArrayList<SchedulingTab>();
	private TabSet tabSet;
	private int selectedIndex = 0;

	// Magic numbers
	private static final int STATIC_TABS = 4;
	private static final int HIDDEN_TABS = 0;
	public static final int DAYS_AFTER_INSTANCE = 40;

	private boolean showDatabaseError;
	private String databaseErrorMessage;

	private User user = User.UNAUTHORISED;

	@PreDestroy
	public void closeConnector() {
		this.connector.close();
	}
	
	private void init() {
		refreshSchedulings();
		refreshInstances();
		refreshAuditTrail();
		indexInstances();
	}

	public void refreshSchedulings() {
		try {
			this.schedulings.clear();
			this.schedulings = this.connector.getSchedulings();
		} catch (Exception e) {
			handleSQLException(e);
		}
	}

	public boolean addScheduling(Scheduling s) {
		try {
			return this.connector.addScheduling(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	public boolean addComment(Comment c) {
		try {
			return this.connector.addComment(c);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	public boolean updateScheduling(Scheduling s) {
		try {
			return this.connector.updateScheduling(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	public List<Comment> getComments(int id, int maxResults) {
		try {
			return this.connector.getComments(id, maxResults);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return new ArrayList<Comment>();
	}

	public SchedulerService getSchedulingService() {
		try {
			return this.connector.getSchedulingService();
		} catch (Exception e) {
			handleSQLException(e);
		}
		return null;
	}

	public boolean stopSchedulingService(SchedulerService s) {
		try {
			return this.connector.stopSchedulingService(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	public boolean startSchedulingService(SchedulerService s) {
		try {
			return this.connector.startSchedulingService(s);
		} catch (Exception e) {
			handleSQLException(e);
		}
		return false;
	}

	private void handleSQLException(Exception e) {
		this.databaseErrorMessage = e.getMessage();
		this.showDatabaseError = true;
	}

	public void closeDatabaseErrorDialog() {
		this.databaseErrorMessage = "";
		this.showDatabaseError = false;
	}

	public void refreshInstances() {
		try {
			this.instances.clear();
			this.instances = this.connector.getInstances();
		} catch (Exception e) {
			handleSQLException(e);
		}
	}

	public void refreshAuditTrail() {
		try {
			this.auditTrail.clear();
			this.auditTrail = this.connector.getAuditTrail();
		} catch (Exception e) {
			handleSQLException(e);
		}
	}

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

	public boolean authenticate(String username, String password) {
		System.out.println(username + " : " + password);

		if (username.toLowerCase().contains("business"))
			this.user = User.BUSINESS;
		else
			this.user = User.ADMINISTRATOR;
		this.init();
		return true;
	}

	public void addTab(Scheduling s) throws ParseException {
		SchedulingTab t = new SchedulingTab();
		t.setScheduling(s);

		if (!tabs.contains(t)) {
			t.setComments(this.connector.getComments(s.getId(),
					ApplicationBean.MAX_COMMENTS_SHOWN));
			t.setInstances(this.getInstancesForScheduling(s,
					DAYS_AFTER_INSTANCE));
			tabs.add(t);
		}

		this.selectedIndex = tabs.indexOf(t) + STATIC_TABS;
		this.tabSet.setSelectedIndex(tabs.indexOf(t) + STATIC_TABS);
	}

	private List<Instance> getInstancesForScheduling(Scheduling s, int daysAgo)
			throws ParseException {

		List<Instance> temp = new ArrayList<Instance>();
		for (Instance i : this.instances) {
			Date now = new Date();
			Date then = ApplicationBean.ORACLE_DATE_FORMAT.parse(i
					.getStartDate());
			if (s.matchesInstance(i)) {
				if (daysAgo == ApplicationBean.INFINITY
						|| Days.daysBetween(new DateTime(then),
								new DateTime(now)).getDays() <= daysAgo) {
					temp.add(i);
				}
			}
		}
		return temp;
	}

	public void removeCurrent(SchedulingTab t) {
		this.tabSet.setSelectedIndex(0);
		TabPane pane = (TabPane) this.tabSet.getChildren().get(
				tabs.indexOf(t) + STATIC_TABS + 1);
		pane.setInView(false);
		tabs.remove(t);
	}

	public void removeAllTabs() {
		this.tabSet.setSelectedIndex(0);
		List<UIComponent> panes = this.tabSet.getChildren();
		for (UIComponent pane : panes) {
			pane.setInView(false);
		}
		tabs.clear();
	}

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

	public void tabChange(ValueChangeEvent e) throws IOException {
		this.selectedIndex = (Integer) e.getNewValue();
		if ((Integer) e.getNewValue() == this.tabSet.getChildren().size() - 1
				- HIDDEN_TABS) {

			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler()
					.handleNavigation(ctx, null, ApplicationBean.LOGOUT);
		}
	}

	public void validate() {
		if (!this.user.isAuthenticated()) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication()
					.getNavigationHandler()
					.handleNavigation(ctx, null, ApplicationBean.LOGIN_REDIRECT);
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
