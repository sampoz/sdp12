package users;

public abstract class AbstractUser implements User {

	boolean authenticated = false;
	boolean viewRights = false;
	boolean addRights = false;
	boolean editRights = false;
	boolean stopRights = false;
	boolean startRights = false;

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		throw new IllegalAccessError();
	}

	@Override
	public boolean isViewRights() {
		return viewRights;
	}

	@Override
	public void setViewRights(boolean viewRights) {
		throw new IllegalAccessError();
	}

	@Override
	public boolean isAddRights() {
		return addRights;
	}

	@Override
	public void setAddRights(boolean addRights) {
		throw new IllegalAccessError();
	}

	@Override
	public boolean isEditRights() {
		return editRights;
	}

	@Override
	public void setEditRights(boolean editRights) {
		throw new IllegalAccessError();
	}

	@Override
	public boolean isStopRights() {
		return stopRights;
	}

	@Override
	public void setStopRights(boolean stopRights) {
		throw new IllegalAccessError();
	}

	@Override
	public boolean isStartRights() {
		return startRights;
	}

	@Override
	public void setStartRights(boolean startRights) {
		throw new IllegalAccessError();
	}

}
