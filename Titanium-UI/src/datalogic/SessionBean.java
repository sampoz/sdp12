package datalogic;


import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


import entities.Backend;
import entities.Composite;
import entities.Mode;

@ManagedBean (name = SessionBean.BEAN_NAME, eager=true)
@SessionScoped
public class SessionBean {
	
	public static final String BEAN_NAME = "sessionBean";
	
	public static final HashMap<Integer, Backend> BACKENDS = new HashMap<Integer, Backend>();  
	public static final HashMap<Integer,Mode> MODES = new HashMap<Integer, Mode>();
	public static final HashMap<Integer,Composite> COMPOSITES = new HashMap<Integer, Composite>();
	
	// Hard coded disabled state. Better options?
	public static final int DISABLED = 2;
	
	private DatabaseConnector connector = new DatabaseConnector();

	private boolean authenticated = false;
	
	public SessionBean(){
		
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
			System.out.println(m.getId() + " : " + m.getLabel());
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
		this.authenticated = true;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) throws IllegalOperationException {
		throw new IllegalOperationException("Access violation");
	}
	
}
