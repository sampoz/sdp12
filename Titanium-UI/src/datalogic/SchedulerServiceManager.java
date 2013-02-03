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
	
	private Comment comment = new Comment();
	private Comment comment2 = new Comment();
    
	private Boolean validate = true;


	public Boolean getValidate() {
		return validate;
	}

	public void setValidate(Boolean validate) {
		this.validate = validate;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	
	private static int SCHEDULINGSERVICECOMMENT;

	private List<Comment> auditTrail;
	
	private HttpConnector httpConnector = new HttpConnector();
	
	@PostConstruct
	public void init(){
		this.auditTrail = this.session.getAuditTrail();
	}

	public void stopAllSchedules(){
		if (this.getComment2().getText().equals("")){
			System.out.println("Comment cant be empty");
			return;
		}
		if (session.getConnector().stopSchedulingService(ApplicationBean.SCHEDULERSERVICE)){
			System.out.println("http succes"+ httpConnector.standby(ApplicationBean.SCHEDULERSERVICE.getUrl()));
			if(this.submitComment(this.getComment2()))
				this.setComment2(new Comment());
		}
	}
	public void startAllSchedules(){
		if (this.getComment2().getText().equals("")){
			System.out.println("Comment cant be empty");
			return;
		}
		if (session.getConnector().startSchedulingService(ApplicationBean.SCHEDULERSERVICE)){
			System.out.println("http succes"+ httpConnector.runall(ApplicationBean.SCHEDULERSERVICE.getUrl()));	
			if(this.submitComment(this.getComment()))
				this.setComment(new Comment());
		}
		
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
	public void validate(){
		validate = true;
	}
	// ==================== GETTERS & SETTERS ====================
	public List<Comment> getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(List<Comment> auditTrail) {
		this.auditTrail = auditTrail;
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
	public Comment getComment2() {
		return comment2;
	}

	public void setComment2(Comment comment2) {
		this.comment2 = comment2;
	}

}
