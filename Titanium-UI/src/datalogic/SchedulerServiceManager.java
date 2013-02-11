package datalogic;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import entities.Comment;
import entities.SchedulerService;

@ManagedBean(name = "schedulerServiceManager")
@ViewScoped
public class SchedulerServiceManager implements Serializable{

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	private Comment comment = new Comment();
	private Comment comment2 = new Comment();

	private Boolean confirmButtonDisabled = false;

	private static int SCHEDULINGSERVICECOMMENT;

	private List<Comment> auditTrail;

	private HttpConnector httpConnector = new HttpConnector();

	private boolean commentError = false;

	private boolean schedulerStopped = false;

	public boolean isCommentError() {
		return commentError;
	}

	public void setCommentError(boolean commentError) {
		this.commentError = commentError;
	}

	@PostConstruct
	public void init() {
		this.auditTrail = this.session.getAuditTrail();
		this.refreshState();
	}
	
	private void refreshState(){
		if(this.session.getConnector().getSchedulingService().getStandby() == SchedulerService.RUNNING){
			this.schedulerStopped = false;
		} else
			this.schedulerStopped  = true;
	}
	
	private void refreshAuditTrail(){
		this.session.refreshAuditTrail();
		this.auditTrail = this.session.getAuditTrail();
	}

	public void stopAllSchedules() {
		if (this.getComment2().getText().length() < 6
				|| this.getComment2().getText().length() > 500) {
			System.out.println("Comment cant be less than 6 or over 500");
			this.commentError = true;
			return;
		}
		if (session.getConnector().stopSchedulingService(
				ApplicationBean.SCHEDULERSERVICE)) {
			this.commentError = false;
			
			System.out.println("http success "
					+ httpConnector.standby(ApplicationBean.SCHEDULERSERVICE
							.getUrl()));
			
			this.refreshState();
			this.refreshAuditTrail();
			if (this.submitComment(this.getComment2()))
				this.setComment2(new Comment());
		}
	}

	public void startAllSchedules() {
		if (this.getComment().getText().length() < 6
				|| this.getComment().getText().length() > 500) {
			System.out.println("Comment cant be less than 6 or over 500");
			this.commentError = true;
			return;
		}
		if (session.getConnector().startSchedulingService(
				ApplicationBean.SCHEDULERSERVICE)) {
			this.commentError = false;
			System.out.println("http success "
					+ httpConnector.runall(ApplicationBean.SCHEDULERSERVICE
							.getUrl()));
			
			this.refreshState();
			this.refreshAuditTrail();
			if (this.submitComment(this.getComment()))
				this.setComment(new Comment());
		}

	}

	public void ValidateInput(FacesContext context, UIComponent component,
			Object value) {
		if (value.toString().length() >= 6 || value.toString().length() > 500) {
			this.confirmButtonDisabled = false;
		} else {
			this.confirmButtonDisabled = true;
			FacesMessage msg = new FacesMessage(
					"Text must be at least 6 chars long but less than 500");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}

	private boolean submitComment(Comment c) {
		c.setCreationDate(ApplicationBean.DATE_FORMAT.format(new Date()));
		c.setSchedulingID(SCHEDULINGSERVICECOMMENT);

		// If the database connector returns true from the persisting of the
		// comment we can safely add it to the table

		if (this.session.getConnector().addComment(c)) {
			refreshComments();
			return true;
		}
		return false;
	}

	private void refreshComments() {
		this.session.refreshAuditTrail();
		this.auditTrail = this.session.getAuditTrail();

	}

	public void closeComment() {
		this.commentError = false;
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

	public Boolean getConfirmButtonDisabled() {
		return confirmButtonDisabled;
	}

	public void setConfirmButtonDisabled(Boolean confirmButtonDisabled) {
		this.confirmButtonDisabled = confirmButtonDisabled;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public boolean isSchedulerStopped() {
		return schedulerStopped;
	}

	public void setSchedulerStopped(boolean schedulerStopped) {
		this.schedulerStopped = schedulerStopped;
	}

}
