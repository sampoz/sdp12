import java.io.Serializable;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name=LinkButtonBean.BEAN_NAME)
@CustomScoped(value = "#{session}")
public class LinkButtonBean implements Serializable {

    public static final String BEAN_NAME = "linkButton";
    private String staticNavigation;
    
    public LinkButtonBean() {
        staticNavigation  = "lol";
    }
    
    public String buttonPressHandler() {
        //application logic can be added here
    	System.out.println("lolo lolo");
        return "succes";
    }
    

}