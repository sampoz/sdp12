package entities;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import datalogic.ApplicationBean;

@FacesConverter("entityConverter")
public class EntityConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		for(Mode m: ApplicationBean.MODES.values()){
			if(m.getValue().equals(value))
				return m;
		}	
		
		for(Composite c: ApplicationBean.COMPOSITES.values()){
			if(c.getOutputText().equals(value))
				return c;
		}
		
		for(Backend b: ApplicationBean.BACKENDS.values()){
			if(b.getBackend().equals(value))
				return b;
		}
		
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}
	
}
