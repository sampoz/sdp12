package datalogic;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import entities.Comment;

@ManagedBean(name="schedulerManager")
@ViewScoped
public class SchedulerDataManager {
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;
	
	private List<Comment> auditTrail;
	
	@PostConstruct
	public void init(){
		this.auditTrail = this.session.getAuditTrail();
	}
	
	// ==================== GETTERS & SETTERS ====================
	public List<Comment> getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(List<Comment> auditTrail) {
		this.auditTrail = auditTrail;
	}
}
