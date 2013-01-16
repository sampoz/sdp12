package datalogic;

import java.util.ArrayList;
import java.util.List;

import entities.Comment;
import entities.Instance;
import entities.Scheduling;

public class SchedulingTab {
	
	private String name;
	
	private Scheduling scheduling;
	
	private String modeValue;
	private String sourceName;
	private String targetName;
	private String compositeText;
	
	private List<Instance> instances = new ArrayList<Instance>();
	private List<Comment> comments = new ArrayList<Comment>();
	
	private Comment addComment = new Comment();
	
	private SchedulingBuilder builder;
	
	

	public SchedulingTab(){}
	
	public Scheduling getScheduling() {
		return scheduling;
	}
	
	public void reset(){
		this.builder = new SchedulingBuilder(this.scheduling);
	}
	
	public void setScheduling(Scheduling scheduling) {
		this.scheduling = scheduling;
		this.name = scheduling.getName().substring(0,4);
		this.builder = new SchedulingBuilder(scheduling);
	}
	
	public List<Instance> getInstances() {
		return instances;
	}
	
	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Comment getAddComment() {
		return addComment;
	}

	public void setAddComment(Comment addComment) {
		this.addComment = addComment;
	}

	public SchedulingBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(SchedulingBuilder builder) {
		this.builder = builder;
	}
	
	public String getModeValue() {
		this.modeValue = ApplicationBean.MODES.get(this.scheduling.getStatusID()).getValue();
		return modeValue;
	}

	public void setModeValue(String modeValue) {
		this.modeValue = modeValue;
	}

	public String getSourceName() {
		if(this.scheduling.getSource() != null)
			this.sourceName = ApplicationBean.BACKENDS.get(this.scheduling.getSource()).getBackend();
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTargetName() {
		if(this.scheduling.getTarget() != null)
			this.targetName = ApplicationBean.BACKENDS.get(this.scheduling.getTarget()).getBackend();
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getCompositeText() {
		this.compositeText = ApplicationBean.COMPOSITES.get(this.scheduling.getServiceID()).getOutputText();
		return compositeText;
	}

	public void setCompositeText(String compositeText) {
		this.compositeText = compositeText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SchedulingTab other = (SchedulingTab) obj;
		if (scheduling == null) {
			if (other.scheduling != null)
				return false;
		} else if (!scheduling.equals(other.scheduling))
			return false;
		return true;
	}
}
