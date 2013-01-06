package users;

public class Administrator extends AbstractUser {
	
	Administrator(){
		this.authenticated = true;
		this.addRights = true;
		this.editRights = true;
		this.startRights = true;
		this.stopRights = true;
		this.viewRights = true;
	}
	
}
