package datalogic;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import entities.Comment;

@ManagedBean(name="schedulerServiceManager")
@ViewScoped
public class SchedulerServiceManager {
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;
	
	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}

	public HttpConnector getHttpConnector() {
		return httpConnector;
	}

	public void setHttpConnector(HttpConnector httpConnector) {
		this.httpConnector = httpConnector;
	}

	private List<Comment> auditTrail;
	
	private HttpConnector httpConnector = new HttpConnector();
	
	@PostConstruct
	public void init(){
		this.auditTrail = this.session.getAuditTrail();
	}
	
	public void stopAllSchedules(){
		if (session.getConnector().stopSchedulingService(ApplicationBean.SCHEDULERSERVICE)){
			System.out.println("http succes"+ httpConnector.standby(ApplicationBean.SCHEDULERSERVICE.getUrl()));
		}
	}
	public void startAllSchedules(){
		if (session.getConnector().startSchedulingService(ApplicationBean.SCHEDULERSERVICE)){
			System.out.println("http succes"+ httpConnector.runall(ApplicationBean.SCHEDULERSERVICE.getUrl()));
		}
		
		
	}
	
	// ==================== GETTERS & SETTERS ====================
	public List<Comment> getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(List<Comment> auditTrail) {
		this.auditTrail = auditTrail;
	}

}
