package datalogic;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = LoginManager.BEAN_NAME)
@RequestScoped
public class LoginManager {
	
	public static final String BEAN_NAME = "loginManager";
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;
	
	private String password;
	private String username;
	
	public boolean authenticate(){
		return this.session.authenticate(username,password);
	}
	
	public void validate() {
		if (this.session.getUser().isAuthenticated()) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler()
					.handleNavigation(ctx, null, ApplicationBean.UI_REDIRECT);
		}
	}
	
	// ==================== GETTERS & SETTERS ====================
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}
	
	
	
	
}
