package datalogic;


import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import users.User;


import entities.Backend;
import entities.Comment;
import entities.Composite;
import entities.Mode;
import entities.Status;

@ManagedBean (name = SessionBean.BEAN_NAME, eager=true)
@SessionScoped
public class SessionBean {
	
	public static final String BEAN_NAME = "sessionBean";
	
	public static final HashMap<Integer, Backend> BACKENDS = new HashMap<Integer, Backend>();  
	public static final HashMap<Integer,Mode> MODES = new HashMap<Integer, Mode>();
	public static final HashMap<Integer,Composite> COMPOSITES = new HashMap<Integer, Composite>();
	public static final HashMap<Integer, Status> STATUSES = new HashMap<Integer, Status>();
	
	public static final HashMap<Integer, String> MODE_STYLES = new HashMap<Integer, String>();
	// Hard coded styleClasses for different modes
	{
		MODE_STYLES.put(1, "activated");
		MODE_STYLES.put(2, "deactivated");
		MODE_STYLES.put(3, "removed");
	}
	
	// Hard coded states. Better options?
	public static final int ENABLED = 1;
	public static final int DISABLED = 2;
	public static final int REMOVED = 3;
	
	private DatabaseConnector connector = new DatabaseConnector();

	private User user = User.UNAUTHORISED;
	
	public SessionBean(){
		
		List<Status> tempStatuses = this.connector.getAllStatuses();
		for(Status s: tempStatuses ){
			STATUSES.put(s.getId(), s);
		}
		
		List<Composite> tempComposites = this.connector.getAllComposites();
		for(Composite c: tempComposites ){
			COMPOSITES.put(c.getId(), c);
		}
		
		List<Backend> tempBackends = this.connector.getAllBackends();
		for(Backend b: tempBackends){
			BACKENDS.put(b.getId(), b);
		}
		List<Mode> tempModes = this.connector.getAllModes();
		for (Mode m : tempModes) {
			MODES.put(m.getId(), m);
		}
		
		
	}
	
	

	public DatabaseConnector getConnector() {
		return connector;
	}

	public void setConnector(DatabaseConnector connector) {
		this.connector = connector;
	}



	public void authenticate(String username, String password) {
		System.out.println(username + " : " + password);
		
		if(username.toLowerCase().contains("business"))
			this.user = User.BUSINESS;
		else
			this.user = User.ADMINISTRATOR;
	}
	public void unAuthenticate() {
		this.user = User.UNAUTHORISED;
	}	


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		//this.user = user;
		throw new IllegalAccessError();
	}


	
}
