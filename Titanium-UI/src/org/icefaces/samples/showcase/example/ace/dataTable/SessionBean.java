package org.icefaces.samples.showcase.example.ace.dataTable;

import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;


import entities.Backend;
import entities.Composite;
import entities.Mode;

@ManagedBean (name = SessionBean.BEAN_NAME, eager=true)
@ApplicationScoped
public class SessionBean {
	
	public static final String BEAN_NAME = "sessionBean";
	
	public static final HashMap<Integer, Backend> BACKENDS = new HashMap<Integer, Backend>();  
	public static final HashMap<Integer,Mode> MODES = new HashMap<Integer, Mode>();
	public static final HashMap<Integer,Composite> COMPOSITES = new HashMap<Integer, Composite>();
	
	private DatabaseConnector connector = new DatabaseConnector();
	
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
		}
		
	}
	
	

	public DatabaseConnector getConnector() {
		return connector;
	}

	public void setConnector(DatabaseConnector connector) {
		this.connector = connector;
	}

	
	
}
