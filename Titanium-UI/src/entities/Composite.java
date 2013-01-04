package entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "SCHEDULING_SERVICE")
public class Composite implements Serializable{

	@Id
	private int id;
	
	@Column(name="OUTPUTTEXT")
	private String outputText;
	
	@Column(name = "JAVAAGENTPOLLABLE")
	private Integer javaAgentPollable;

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
}
