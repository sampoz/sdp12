package org.icefaces.samples.showcase.example.ace.dataTable;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class DatabaseConnector {
	
	private EntityManager manager;
	
	public void addScheduling(Scheduling s){
		
		// Initialize entity manager if it isn't already
		lazyInit();
		
		this.manager.getTransaction().begin();
		this.manager.persist(s);
		this.manager.getTransaction().commit();
	}	
	
	public List<Scheduling> getSchedulings(){
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("from Scheduling");
		List<Scheduling> result = q.getResultList();
		for (Scheduling scheduling : result) {
			
			if(scheduling.getService()!= null) 
				System.out.println(scheduling.getServiceID() + " : " + scheduling.getService().getId() +" : " + scheduling.getService().getOutputText());
			
			if(scheduling.getStatus()!= null) 
				System.out.println(scheduling.getStatusID() + " : " + scheduling.getStatus().getId() +" : " + scheduling.getStatus().getLabel());
		}
		this.manager.getTransaction().commit();
		return result;
	}
	
	private void lazyInit(){
		if(this.manager == null){
			 EntityManagerFactory factory = Persistence.createEntityManagerFactory("manager1");
			 this.manager = factory.createEntityManager();
		} 
		return;
	}
	
	public EntityManager getManager() {
		return manager;
	}

	public void setManager(EntityManager manager) {
		this.manager = manager;
	}
	
	public boolean updateScheduling(Scheduling s){
		lazyInit();
		try{
			this.manager.getTransaction().begin();
			this.manager.merge(s);
			this.manager.getTransaction().commit();
		} catch (IllegalArgumentException e){
			return false;
		}
		return true;
	}
	
	
	
}
