package org.icefaces.samples.showcase.example.ace.dataTable;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import entities.Backend;
import entities.Composite;
import entities.Mode;
import entities.Scheduling;

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
		this.manager.getTransaction().commit();
		return result;
	}
	
	public List<Mode> getAllModes(){
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("from Mode");
		List<Mode> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}
	
	public List<Composite> getAllComposites(){
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("from Composite");
		List<Composite> result = q.getResultList();
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

	public List<Backend> getAllBackends() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("from Backend");
		List<Backend> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}
	
	
	
}
