package entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "SCHEDULING_SERVICE")
public class Composite implements Serializable{

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int id;
	
	@Column(name="OUTPUTTEXT")
	private String outputText;
	
	@Column(name = "JAVAAGENTPOLLABLE")
	private Integer javaAgentPollable;
	
	@Column(name = "DESTINATION_URL")
	private String destinationURL;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOutputText() {
		return outputText;
	}

	public void setOutputText(String outputText) {
		this.outputText = outputText;
	}

	public Integer getJavaAgentPollable() {
		return javaAgentPollable;
	}

	public void setJavaAgentPollable(Integer javaAgentPollable) {
		this.javaAgentPollable = javaAgentPollable;
	}
	
	public String getDestinationURL() {
		return destinationURL;
	}

	public void setDestinationURL(String destinationURL) {
		this.destinationURL = destinationURL;
	}

	@Override
	public String toString(){
		return this.outputText;
	}
}
