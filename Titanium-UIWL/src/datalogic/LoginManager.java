package datalogic;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 * Simple, short lived bean that handles the login screen data and passes them
 * to the injected session.
 * 
 */
@ManagedBean(name = "loginManager")
@RequestScoped
public class LoginManager {

	// Injected bean for the current session
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean session;

	private String password;
	private String username;

	/**
	 * Passes the given user name and password for the session bean to be
	 * validated and if given the permission, returns true which is handled by
	 * the navigation rules in faces-config.xml and the browser is redirected to
	 * the UI.
	 * 
	 * @return true if successful
	 */
	public boolean authenticate() {
		return this.session.authenticate(username, password);
	}

	/**
	 * Called each time a user accesses the login page. If the user is already
	 * authenticated, they are redirected back to the UI.
	 */
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
