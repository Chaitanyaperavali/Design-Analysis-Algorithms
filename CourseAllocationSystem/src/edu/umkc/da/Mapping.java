package edu.umkc.da;

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
	}
	
	//Compute Score
	public void calculateScoreOfProfessor(){
		
	}
	
	public void mapCourseToProfessor(){
		
	}
}
