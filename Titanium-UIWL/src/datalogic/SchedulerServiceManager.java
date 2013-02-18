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

/**
 * SchedulerServiceManager is for managing AuditTrail view. User can stop and
 * start Scheduler and view AuditTrail comments. SessionBean is used to connect
 * database and HttpConnector to make httpcalls.
 * 
 */
@ManagedBean(name = "schedulerServiceManager")
@ViewScoped
public class SchedulerServiceManager implements Serializable {

	// Injected bean for the current session
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	// Comment when starting/stopping scheduler
	private Comment commentStarting = new Comment();
	private Comment commentStopping = new Comment();

	// Scheduling comment are saved with number 0
	private static int SCHEDULING_SERVICE_COMMENT = 0;

	// All comments that belong to stopping or starting schedulings
	private List<Comment> auditTrail;

	private HttpConnector httpConnector = new HttpConnector();

	private boolean stopCommentError = false;
	private boolean startCommentError = false;

	// a Ae the schedulers stopped or running
	private boolean schedulerStopped = false;

	private boolean stop_dialog_visible = false;
	private boolean start_dialog_visible = false;

	// After bean creation retrieve the audit trail and the state of systems
	// scheduler service from the session
	@PostConstruct
	public void init() {
		this.auditTrail = this.session.getAuditTrail();
		this.refreshState();
	}

	/*
	 * Ask the scheduler service is the scheduler running or not and update
	 * the state.
	 */
	private void refreshState() {
		if (this.session.getSchedulingService().getStandby() == SchedulerService.RUNNING) {
			this.schedulerStopped = false;
		} else
			this.schedulerStopped = true;
	}

	 // Refresh comments in the audit trail
	private void refreshAuditTrail() {
		this.session.refreshAuditTrail();
		this.auditTrail = this.session.getAuditTrail();
	}

	// Methods for controlling dialogs
	public void openStartScheduling() {
		setStart_dialog_visible(true);
	}

	public void openStopScheduling() {
		setStop_dialog_visible(true);
	}

	public void closeStartScheduling() {
		setStart_dialog_visible(false);
	}

	public void closeStopScheduling() {
		setStop_dialog_visible(false);
	}

	/**
	 * Stop all schedulings. If comment is less than 6 or more than 500 chars
	 * open a error dialog. If comment is okay, change the value of
	 * {@link SchedulerService} -table and make http-call
	 */
	public void stopAllSchedules() {
		if (this.getCommentStopping().getText().length() < 6
				|| this.getCommentStopping().getText().length() > 500) {
			this.stopCommentError = true;
		}
		// if comment is okey make http call
		else if (session
				.stopSchedulingService(ApplicationBean.SCHEDULER_SERVICE)) {

			// Close dialog
			this.stopCommentError = false;
			closeStopScheduling();

			// Set state and refresh audit trail
			this.refreshState();
			this.refreshAuditTrail();

			// Create new comment
			this.commentStopping.setText("Scheduler was stopped. Reason: "
					+ this.commentStopping.getText());
			if (this.submitComment(this.getCommentStopping()))
				this.setCommentStopping(new Comment());
		}
	}

	/**
	 * Start all schedulings. If comment is less than 6 or more than 500 chars
	 * open a error dialog. If comment is okay, change the value of
	 * {@link SchedulerService} -table and make http-call
	 */
	public void startAllSchedules() {
		if (this.getCommentStarting().getText().length() < 6
				|| this.getCommentStarting().getText().length() > 500) {
			this.startCommentError = true;
		}
		// if comment is okey make http call
		else if (session
				.startSchedulingService(ApplicationBean.SCHEDULER_SERVICE)) {

			// close dialog
			this.startCommentError = false;
			closeStartScheduling();

			System.out.println("http success "
					+ httpConnector.runall(ApplicationBean.SCHEDULER_SERVICE
							.getUrl()));

			// refresh comments in view
			this.refreshState();
			this.refreshAuditTrail();

			// submit comment
			this.commentStarting.setText("Scheduler was started. Reason: "
					+ this.commentStarting.getText());
			if (this.submitComment(this.getCommentStarting()))
				this.setCommentStarting(new Comment());
		}

	}

	/*
	 * Add date and SCHEDULING_SERVICE_COMMENT -id to comment. If the database
	 * connector returns true from the persisting of the comment we refresh the 
	 */
	private boolean submitComment(Comment c) {
		c.setCreationDate(ApplicationBean.DATE_FORMAT.format(new Date()));
		c.setSchedulingID(SCHEDULING_SERVICE_COMMENT);

		if (this.session.addComment(c)) {
			refreshAuditTrail();
			return true;
		}
		return false;
	}

	/**
	 * Close dialog that is shown if user tries to submit too short or too long
	 * comment.
	 */
	public void closeComment() {
		this.startCommentError = false;
		this.stopCommentError = false;
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

	public Comment getCommentStopping() {
		return commentStopping;
	}

	public void setCommentStopping(Comment commentStopping) {
		this.commentStopping = commentStopping;
	}

	public Comment getCommentStarting() {
		return commentStarting;
	}

	public void setCommentStarting(Comment comment) {
		this.commentStarting = comment;
	}

	public boolean isSchedulerStopped() {
		return schedulerStopped;
	}

	public void setSchedulerStopped(boolean schedulerStopped) {
		this.schedulerStopped = schedulerStopped;
	}

	public boolean isStop_dialog_visible() {
		return stop_dialog_visible;
	}

	public void setStop_dialog_visible(boolean stop_dialog_visible) {
		this.stop_dialog_visible = stop_dialog_visible;
	}

	public boolean isStart_dialog_visible() {
		return start_dialog_visible;
	}

	public void setStart_dialog_visible(boolean start_dialog_visible) {
		this.start_dialog_visible = start_dialog_visible;
	}

	public boolean isStartCommentError() {
		return startCommentError;
	}

	public void setStartCommentError(boolean startCommentError) {
		this.startCommentError = startCommentError;
	}

	public boolean isStopCommentError() {
		return stopCommentError;
	}

	public void setStopCommentError(boolean stopCommentError) {
		this.stopCommentError = stopCommentError;
	}

}
