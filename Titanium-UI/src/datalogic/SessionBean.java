package datalogic;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.icefaces.ace.component.tabset.TabPane;
import org.icefaces.ace.component.tabset.TabSet;
import org.joda.time.DateTime;
import org.joda.time.Days;

import users.User;

import entities.Backend;
import entities.Comment;
import entities.Composite;
import entities.Instance;
import entities.Mode;
import entities.Scheduling;
import entities.Status;

@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean {

	private InstanceDataManager instanceManager;
	private SchedulingDataManager schedulingManager;

	private DatabaseConnector connector = new DatabaseConnector();

	private List<Scheduling> schedulings = new ArrayList<Scheduling>();
	private List<Instance> instances = new ArrayList<Instance>();

	private List<SchedulingTab> tabs = new ArrayList<SchedulingTab>();
	private TabSet tabSet;
	
	public List<SchedulingTab> getTabs() {
		return tabs;
	}

	private static final int STATIC_TABS = 4;
	private static final int HIDDEN_TABS = 0;

	private static final DateFormat ORACLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-dd-MM HH:mm:ss");

	private User user = User.UNAUTHORISED;

	@PreDestroy
	public void closeConnector() {
		this.connector.close();
	}

	private void initData() {
		refreshSchedulings();
		refreshInstances();
	}

	public void initSchedulingManager(SchedulingDataManager schedulingManager) {
		this.schedulingManager = schedulingManager;
	}

	public void initInstanceManager(InstanceDataManager instanceManager) {
		this.instanceManager = instanceManager;
	}

	public void refreshSchedulings() {
		this.schedulings.clear();
		this.schedulings = this.connector.getSchedulings();
	}

	public void refreshInstances() {
		this.instances.clear();
		this.instances = this.connector.getInstances();
	}

	public DatabaseConnector getConnector() {
		return connector;
	}

	public void setConnector(DatabaseConnector connector) {
		this.connector = connector;
	}

	public boolean authenticate(String username, String password) {
		System.out.println(username + " : " + password);

		if (username.toLowerCase().contains("business"))
			this.user = User.BUSINESS;
		else
			this.user = User.ADMINISTRATOR;
		
		initData();
		return true;
	}

	public void addTab(Scheduling s) throws ParseException {
		SchedulingTab t = new SchedulingTab();
		t.setScheduling(s);

		if (!tabs.contains(t)) {

			List<Instance> temp = new ArrayList<Instance>();
			for (Instance i : this.instances) {

				Date now = new Date();
				Date then = ORACLE_DATE_FORMAT.parse(i.getStartDate());
				if (i.getProcess() != null
						&& s.getName().substring(0, 4)
								.equals(i.getProcess().substring(0, 4))
						&& Days.daysBetween(new DateTime(then),
								new DateTime(now)).getDays() <= 40) {
					temp.add(i);
				}
			}

			t.setComments(this.connector.getLastComments(s.getId()));
			t.setInstances(temp);
			tabs.add(t);
		}

		this.tabSet.setSelectedIndex(tabs.indexOf(t) + STATIC_TABS);
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
		this.tabSet.setSelectedIndex(tabs.indexOf(t) + STATIC_TABS);
	}
	
	public void tabChange(ValueChangeEvent e) throws IOException{
		if((Integer) e.getNewValue() == this.tabSet.getChildren().size() - 1 -  HIDDEN_TABS) {
			
			FacesContext ctx =  FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "logout");
		}
	}
	
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

	public TabSet getTabSet() {
		return tabSet;
	}

	public void setTabSet(TabSet tabSet) {
		this.tabSet = tabSet;
	}
	
	public void validate(){
		if(!this.user.isAuthenticated()){
			FacesContext ctx =  FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "logout");
		}
	}

}
