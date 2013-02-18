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

/**
 * 
 * This class handles all of the traffic between the managed beans and the
 * database.
 */
public class DatabaseConnector {

	private EntityManager manager;

	// Magic number to define unlimited query results
	public static final int INFINITY = -1;

	// Max amount of comments retrieved for each scheduling
	public static final int MAX_COMMENTS_SHOWN = 10;

	/**
	 * Persists the provided {@link Scheduling} object to the database.
	 * 
	 * @param s
	 * @return true if successful
	 */
	public boolean addScheduling(Scheduling s) {
		lazyInit();
		this.manager.getTransaction().begin();
		this.manager.persist(s);
		this.manager.getTransaction().commit();
		return true;
	}

	/**
	 * Retrieves all {@link Scheduling} objects from the database.
	 * 
	 * @return list of retrieved objects
	 */
	public List<Scheduling> getSchedulings() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT s FROM Scheduling s");
		List<Scheduling> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	/**
	 * Retrieves all {@link Instance} objects from the database.
	 * 
	 * @return list of retrieved objects
	 */
	public List<Instance> getInstances() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager
				.createQuery("SELECT i FROM Instance i ORDER BY i.startDate desc");
		List<Instance> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	/**
	 * Retrieves all {@link Mode} objects from the database.
	 * 
	 * @return list of retrieved objects
	 */
	public List<Mode> getAllModes() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT m FROM Mode m");
		List<Mode> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	/**
	 * Retrieves all {@link Composite} objects from the database.
	 * 
	 * @return list of retrieved objects
	 */
	public List<Composite> getAllComposites() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT c FROM Composite c");
		List<Composite> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	/**
	 * Retrieves {@link Comment} objects from the database corresponding to the
	 * {@link Scheduling} with the given id. Limit for the amount of retrieved
	 * objects is given as the second parameter. If -1 is passed as maximum
	 * results, no limit is enforced.
	 * 
	 * @param id
	 * @param maxResults
	 * @return list of retrieved objects
	 */
	public List<Comment> getComments(int id, int maxResults) {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager
				.createQuery("SELECT c FROM Comment c WHERE c.schedulingID = "
						+ id + " ORDER BY c.creationDate desc");
		if (maxResults != INFINITY) {
			q.setFirstResult(0);
			q.setMaxResults(maxResults);
		}
		List<Comment> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	/**
	 * Retrieves all {@link Status} objects from the database.
	 * 
	 * @return list of retrieved objects
	 */
	public List<Status> getAllStatuses() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT s FROM Status s");
		List<Status> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}

	/**
	 * Retrieves all {@link Backend} objects from the database.
	 * 
	 * @return list of retrieved objects
	 */
	public List<Backend> getAllBackends() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT b FROM Backend b");
		List<Backend> result = q.getResultList();
		this.manager.getTransaction().commit();
		return result;
	}
	
	/**
	 * Merges the provided {@link Scheduling} object with the corresponding object in the database.
	 * 
	 * @return true if successful
	 */
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
	
	/**
	 * Persists the provided {@link Comment} object to the database.
	 * 
	 * @param c
	 * @return true if successful
	 */
	public boolean addComment(Comment c) {
		lazyInit();
		this.manager.getTransaction().begin();
		this.manager.persist(c);
		this.manager.getTransaction().commit();
		return true;
	}
	
	/**
	 * Retrieves all {@link Comment} objects with SCHEDULINGID 0, which are the audit trail of the systems scheduler service.
	 * 
	 * @return list of retrieved objects
	 */
	public List<Comment> getAuditTrail() {
		return this.getComments(0, INFINITY);
	}
	
	/**
	 * Changes the status of the provided {@link SchedulerService} to running.
	 * 
	 * @param s
	 * @return true if successful
	 */
	public boolean startSchedulingService(SchedulerService s) {
		return changeSchedulingStandByValue(s, SchedulerService.RUNNING);
	}
	
	/**
	 * Changes the status of the provided {@link SchedulerService} to stopped.
	 * 
	 * @param s
	 * @return true if successful
	 */
	public boolean stopSchedulingService(SchedulerService s) {
		return changeSchedulingStandByValue(s, SchedulerService.STOPPED);
	}
	
	// Changes the status of a scheduler service to the given value
	private boolean changeSchedulingStandByValue(SchedulerService s,
			int standByValue) {
		lazyInit();
		s.setStandby(standByValue);
		try {
			this.manager.getTransaction().begin();
			this.manager.merge(s);
			this.manager.getTransaction().commit();
		} catch (IllegalArgumentException e) {
			return false;
		}
		System.out.println("Stand by value set to " + standByValue);
		return true;
	}

	/**
	 * Retrieves the first {@link SchedulerService} instance found in the database:
	 * 
	 * @return the found scheduler service
	 */
	public SchedulerService getSchedulingService() {
		lazyInit();
		this.manager.getTransaction().begin();
		Query q = this.manager.createQuery("SELECT s FROM SchedulerService s");
		q.setMaxResults(1);
		List<SchedulerService> schedulingServices = q.getResultList();
		this.manager.getTransaction().commit();
		if (schedulingServices.isEmpty()) {
			return null;
		}
		return schedulingServices.get(0);

	}

	// Initializes the entity manager if its not done yet
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

}
