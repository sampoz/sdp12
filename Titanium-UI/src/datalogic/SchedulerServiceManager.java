package datalogic;

import java.util.Date;
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
	
	private Comment addComment = new Comment();
	
	private static int SCHEDULINGSERVICECOMMENT;

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
	
	public void submitNewComment() {
		if(this.submitComment(this.getAddComment()))
			this.setAddComment(new Comment());
	}
	

	private boolean submitComment(Comment c){
		c.setCreationDate(ApplicationBean.DATE_FORMAT.format(new Date()));
		c.setSchedulingID( SCHEDULINGSERVICECOMMENT);

		// If the database connector returns true from the persisting of the
		// comment we can safely add it to the table
		
		if(this.session.getConnector().addComment(c)){
			refreshComments();
			return true;
		}
		return false;
	}
	private void refreshComments(){
		this.session.refreshAuditTrail();
		this.auditTrail = this.session.getAuditTrail();

	}
	
	// ==================== GETTERS & SETTERS ====================
	public List<Comment> getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(List<Comment> auditTrail) {
		this.auditTrail = auditTrail;
	}

	public Comment getAddComment() {
		return addComment;
	}

	public void setAddComment(Comment addComment) {
		this.addComment = addComment;
	}
	
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
}
