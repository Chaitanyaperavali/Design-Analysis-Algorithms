package edu.umkc.da;

import java.util.Map;

/**
 * This class holds professor ID, name and list of professors' expertise in corresponding topic*/
public class Professor {
	private int professorID;
	private String name;
	private Map<Topic, Integer> topicExpertise;
	
	public int getProfessorID() {
		return professorID;
	}
	public void setProfessorID(int professorID) {
		this.professorID = professorID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<Topic, Integer> getTopicExpertise() {
		return topicExpertise;
	}
	public void setTopicExpertise(Map<Topic, Integer> topicExpertise) {
		this.topicExpertise = topicExpertise;
	}
	
}
