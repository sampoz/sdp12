package users;

public interface User {
	
	public static final User UNAUTHORISED = new Unauthorised();
	public static final User ADMINISTRATOR = new Administrator();
	public static final User BUSINESS = new Business();

	boolean isAuthenticated();

	void setAuthenticated(boolean authenticated);

	boolean isViewRights();

	void setViewRights(boolean viewRights);

	boolean isAddRights();

	void setAddRights(boolean addRights);

	boolean isEditRights();

	void setEditRights(boolean editRights);

	boolean isRunRights();

	void setRunRights(boolean stopRights);
	
}
