package edu.umkc.da;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

//Allocations happen in this class.
public class Mapping {

	private Course[] courses;
	private Professor[] professors;
	private double[][] costMatrix;
	private int[] finalMapping;

	public Mapping() {
		Utility utility = new Utility();

		List<Course> crs = utility.loadCourses();
		this.courses = new Course[crs.size()];
		this.courses = crs.toArray(this.courses);

		List<Professor> prcs = utility.loadProfessors();
		this.professors = new Professor[prcs.size()];
		this.professors = prcs.toArray(this.professors);

		this.costMatrix = new double[this.courses.length][this.professors.length];
	}

	// Compute Score
	public double calculateScoreOfProfessor(Course course, Professor professor) {
		double score = 0.0;
		for (Topic topic : professor.getTopicExpertise().keySet()) {
			if (course.getTopics().containsKey(topic)) {
				score = score + (professor.getTopicExpertise().get(topic) / 5.0) * course.getTopics().get(topic);
			}
		}
		return score;
	}
	/**
	 * This method generates costmatrix*/
	public void mapCourseToProfessor() throws IOException {
		double[][] dupCostMatrix = new double[this.courses.length][this.professors.length];
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(".\\cost_matrix.csv"));
		
		for(int i=0;i<professors.length;i++){
			writer.write(","+professors[i].getName());
		}
		writer.write("\n");
		for (int i = 0; i < courses.length; i++) {
			writer.write(courses[i].getCourseName()+",");
			for (int j = 0; j < professors.length; j++) {
				costMatrix[i][j] = calculateScoreOfProfessor(courses[i], professors[j]);
				dupCostMatrix[i][j] = 1.0 - costMatrix[i][j];
				writer.write(costMatrix[i][j]+",");
			}
			writer.write("\n");
		}
		writer.close();

		// duplicate matix where each cell is {maxPossibleValue - currentValue}
		HungImp hungImp = new HungImp(dupCostMatrix);
		// index - Course, value - Professor
		finalMapping = hungImp.execute();
	}

	public void getMappings() throws IOException {
		// calling this method updates final mappings.
		mapCourseToProfessor();
		String str = "";
		BufferedWriter writer = new BufferedWriter(new FileWriter(".\\Output.csv"));
		writer.write("Course,Professor,Score\n");
		for (int i = 0; i < finalMapping.length; i++) {
			str = courses[i].getCourseName() + "," + professors[finalMapping[i]].getName() + ","
					+ costMatrix[i][finalMapping[i]] + "\n";
			writer.write(str);
		}
		writer.close();

	}
}
