package entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "SOA_COMPOSITE_LOGS")
public class Instance implements Serializable {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	// @ManyToOne
	// @JoinColumn(name="SERVICEID", insertable=false, updatable=false)
	// private SchedulingService service;
	//
	// @ManyToOne
	// @JoinColumn(name="STATUSID", insertable=false, updatable=false)
	// private SchedulingStatus status;

	@Column(name = "NAME")
	private String name;

	@Column(name = "PROCESS")
	private String process;

	@Column(name = "INSTANCE")
	private String instance;

	@Column(name = "START_DATE")
	private String startDate;

	@Column(name = "END_DATE")
	private String endDate;

	@Column(name = "STATUSID")
	private Integer statusID;

	@Column(name = "MESSAGE")
	private String message;

	@Column(name = "INPUT_FILENAME")
	private String inputFileName;

	@Column(name = "OUTPUT_FILENAME")
	private String outputFileName;

	@Column(name = "INSTANCE_DELETED")
	private Integer instanceDeleted;

	@Column(name = "INSTANCE_HIDDEN_UI")
	private Integer instanceHiddenUI;

	public Instance() {
	}

	public Instance(String name, String process, String instance,
			String startDate, String endDate, int statusID, String message,
			String inputFileName, String outputFileName, int instanceDeleted,
			int instanceHiddenUI) {
		super();
		this.name = name;
		this.process = process;
		this.instance = instance;
		this.startDate = startDate;
		this.endDate = endDate;
		this.statusID = statusID;
		this.message = message;
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
		this.instanceDeleted = instanceDeleted;
		this.instanceHiddenUI = instanceHiddenUI;
	}
	
	public int getInputFileAmmount() {
		try {
			String[] files = this.inputFileName.split(";");
			return files.length;	
		} catch (NullPointerException e) {
			return -1;
		}
	}
	
	public int getOutputFileAmmount() {
		try {
			String[] files = this.outputFileName.split(";");
			return files.length;
		} catch (NullPointerException e) {
			return -1;
		}	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getStatusID() {
		return statusID;
	}

	public void setStatusID(Integer statusID) {
		this.statusID = statusID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public Integer getInstanceDeleted() {
		return instanceDeleted;
	}

	public void setInstanceDeleted(Integer instanceDeleted) {
		this.instanceDeleted = instanceDeleted;
	}

	public Integer getInstanceHiddenUI() {
		return instanceHiddenUI;
	}

	public void setInstanceHiddenUI(Integer instanceHiddenUI) {
		this.instanceHiddenUI = instanceHiddenUI;
	}

}
