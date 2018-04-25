package edu.umkc.da;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Allocations happen in this class.
public class Mapping {
	
	private List<Course> courses;
	private List<Professor> professors;
	private Map<Integer,Integer> courseProfessorMapping;
	
	
	public Mapping() {
		Utility utility = new Utility();
		this.courses = utility.loadCourses();
		this.professors = utility.loadProfessors();
		this.courseProfessorMapping = new HashMap<>();
	}
	
	//Compute Score
	public double calculateScoreOfProfessor(Professor professor,Course course){
		double score = 0.0;
		for (Topic topic : professor.getTopicExpertise().keySet()) {
			if(course.getTopics().containsKey(topic)){
				score = score + professor.getTopicExpertise().get(topic) * course.getTopics().get(topic);
			}
		}
		return score;
	}
	
	
	public void mapCourseToProfessor(){
		
		for (Course course : courses) {
			double oldScore = 0.0;
			int matchingProfessorId = -1;
			for (Professor professor : professors) {
				double newScore = calculateScoreOfProfessor(professor, course);
				if(newScore > oldScore){
					matchingProfessorId = professor.getProfessorID();
					oldScore = newScore;
				}
			}
			courseProfessorMapping.put(course.getCourseID(), matchingProfessorId);
		}
		
	}
	
	public void getAllocations(){
		mapCourseToProfessor();
		System.out.println("Professor           :            Course");
		for (Course course : courses) {
			System.out.println(courseProfessorMapping.get(course.getCourseID())+" -> "+course.getCourseName());
		}
	}
}
