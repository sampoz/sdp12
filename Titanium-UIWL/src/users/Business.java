package users;

public class Business extends AbstractUser {

	Business(){
		this.authenticated = true;
		this.viewRights = true;
	}

}
