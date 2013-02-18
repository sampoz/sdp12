package datalogic;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import entities.Backend;
import entities.Comment;
import entities.Composite;
import entities.Instance;
import entities.Mode;
import entities.SchedulerService;
import entities.Scheduling;
import entities.Status;

public class DatabaseConnector {

	private EntityManager manager;
	
	public boolean addScheduling(Scheduling s) {
		// Initialize entity manager if it isn't already
		lazyInit();

		this.manager.getTransaction().begin();
		this.manager.persist(s);
		this.manager.getTransaction().commit();
		return true;
	}

	public List<Scheduling> getSchedulings() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT s FROM Scheduling s");
		List<Scheduling> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	public List<Instance> getInstances() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager
				.createQuery("SELECT i FROM Instance i ORDER BY i.startDate desc");
		List<Instance> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	public List<Mode> getAllModes() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT m FROM Mode m");
		List<Mode> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	public List<Composite> getAllComposites() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT c FROM Composite c");
		List<Composite> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	public List<Comment> getComments(int id, int maxResults) {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager
				.createQuery("SELECT c FROM Comment c WHERE c.schedulingID = "
						+ id + " ORDER BY c.creationDate desc");
		if (maxResults != ApplicationBean.INFINITY) {
			q.setFirstResult(0);
			q.setMaxResults(maxResults);
		}
		List<Comment> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	private void lazyInit() {
		if (this.manager == null) {
			EntityManagerFactory factory = Persistence
					.createEntityManagerFactory("manager1");
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

	public boolean updateScheduling(Scheduling s) {
		lazyInit();
		try {
			this.manager.getTransaction().begin();
			this.manager.merge(s);
			this.manager.getTransaction().commit();
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	public List<Backend> getAllBackends() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT b FROM Backend b");
		List<Backend> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	public boolean addComment(Comment c) {
		lazyInit();

		this.manager.getTransaction().begin();
		this.manager.persist(c);
		this.manager.getTransaction().commit();
		return true;
	}

	public List<Status> getAllStatuses() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT s FROM Status s");
		List<Status> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	public void close() {
		this.manager.close();
	}

	public List<Comment> getAuditTrail() {
		return this.getComments(0, ApplicationBean.INFINITY);
	}

	public boolean startSchedulingService(SchedulerService s) {	
		return changeSchedulingStandByValue(s, SchedulerService.RUNNING);
	}
	
	public boolean stopSchedulingService(SchedulerService s) {	
		return changeSchedulingStandByValue(s, SchedulerService.STOPPED);
	}
	
	private boolean changeSchedulingStandByValue(SchedulerService s, int standByValue){
		lazyInit();
		s.setStandby(standByValue);
		try {
			this.manager.getTransaction().begin();
			this.manager.merge(s);
			this.manager.getTransaction().commit();
		} catch (IllegalArgumentException e) {
			return false;
		}
		System.out.println("Stand by value set to "+ standByValue);
		return true;
	}
	
	public SchedulerService getSchedulingService(){
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT s FROM SchedulerService s");
		List<SchedulerService> schedulingServices = q.getResultList();
		this.manager.getTransaction().commit();
		if (schedulingServices.isEmpty()){
			return null;
		}
		return schedulingServices.get(0);
		
	}
}
