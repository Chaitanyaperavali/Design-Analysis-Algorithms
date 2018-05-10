package edu.umkc.da;

import java.util.List;

//Allocations happen in this class.
public class Mapping {
	
	private Course[] courses;
	private Professor[] professors;
	private double[][] costMatrix;
	private int[] finalMapping;
	
	
	public Mapping() {
		Utility utility = new Utility();
		
		List<Course> crs= utility.loadCourses();
		this.courses = new Course[crs.size()];
		this.courses = crs.toArray(this.courses);
		
		List<Professor> prcs = utility.loadProfessors();
		this.professors = new Professor[prcs.size()];
		this.professors = prcs.toArray(this.professors);
		
		this.costMatrix = new double[this.courses.length][this.professors.length];
	}
	
	//Compute Score
	public double calculateScoreOfProfessor(Course course,Professor professor){
		double score = 0.0;
		for (Topic topic : professor.getTopicExpertise().keySet()) {
			if(course.getTopics().containsKey(topic)){
				score = score + (professor.getTopicExpertise().get(topic)/5.0) * course.getTopics().get(topic);
			}
		}
		return score;
	}
	
	
	public void mapCourseToProfessor(){
		double[][] dupCostMatrix = new double[this.courses.length][this.professors.length];
		for (int i = 0; i < courses.length; i++) {
			for(int j = 0;j < professors.length;j++){
				costMatrix[i][j] = calculateScoreOfProfessor(courses[i],professors[j]);
				dupCostMatrix[i][j] = 1.0 - costMatrix[i][j];
			}
		}
		
		//duplicate matix where each cell is {maxPossibleValue - currentValue}
		HungImp hungImp = new HungImp(dupCostMatrix);
		//index - Course, value - Professor
		finalMapping = hungImp.execute();
	}
	
	public void getMappings(){
		//calling this method updates final mappings.
		mapCourseToProfessor();
		System.out.println("Course                Professor             Score");
		for(int i=0;i<finalMapping.length;i++){
			System.out.println(courses[i].getCourseName() + " -- " + professors[finalMapping[i]].getName() + " -- " + costMatrix[i][finalMapping[i]]);
		}
	}
}
