



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.Map;


/**
 * <p>The MenuBarBean class determines which menu item fired the ActionEvent and
 * stores the modified id information in a String. MenuBarBean also controls the
 * orientation of the Menu Bar.</p>
 */
public class MenuBarBean {

    // records the param value for the menu item which fired the event
    public String param;
    public String param1;
    public String param2;
    public String param3;
    
    private int count = 0;
    // orientation of the menubar ("horizontal" or "vertical")
    private String orientation = "horizontal";

    

    /**
     * Set the param value.
     */
    public String getParam() {
        return this.param;
    }
    public void setParam(String param) {
        this.param = param;
    }
    public String getParam1() {
        return this.param1;
    }
    public void setParam1(String param) {
        this.param1 = param;
    }

    public String getParam2() {
        return this.param2;
    }
    public void setParam2(String param) {
        this.param2 = param;
    }
    public String getParam3() {
        return this.param3;
    }
    public void setParam3(String param) {
        this.param3 = param;
    }


    /**
     * Identify the ID of the element that fired the event and return it in a
     * form suitable for display.
     *
     * @param e the event that fired the listener
     */
    public void listener(ActionEvent e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();
        String myParam = (String) params.get("myParam");
        if (myParam != null && myParam.length() > 0) {
            setParam(myParam);
        } else {
            setParam("not defined");
        }

    }

    /**
     * Get the orientation of the menu ("horizontal" or "vertical")
     *
     * @return the orientation of the menu
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation of the menu ("horizontal" or "vertical").
     *
     * @param orientation the new orientation of the menu
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}