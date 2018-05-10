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
	
	/**
	 * Loads data, pre-process, normalize and save into respective objects. */
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

	/**
	 * Function to calculate matching score of professor to course. This
	 * computes score based on course content and professor expertise on the
	 * course content.
	 */
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
	 * This method generates costMatrix. Each cell(i,j) in cost matrix holds
	 * matching score for i th course and j th professor Duplicate cost matrix
	 * is a new matrix with each cell updated with {maxPosiibleValue -
	 * currentValue} maxPossibleValue = 1.0 in our case. Creates an object of
	 * Hungarian matrix algorithm with duplicate cost matrix as parameter. calls
	 * excute method of this object and finalMapping are updated with returned
	 * value.
	 */
	public void mapCourseToProfessor() throws IOException {
		double[][] dupCostMatrix = new double[this.courses.length][this.professors.length];

		BufferedWriter writer = new BufferedWriter(new FileWriter(".\\cost_matrix.csv"));

		for (int i = 0; i < professors.length; i++) {
			writer.write("," + professors[i].getName());
		}
		writer.write("\n");
		for (int i = 0; i < courses.length; i++) {
			writer.write(courses[i].getCourseName() + ",");
			for (int j = 0; j < professors.length; j++) {
				costMatrix[i][j] = calculateScoreOfProfessor(courses[i], professors[j]);
				dupCostMatrix[i][j] = 1.0 - costMatrix[i][j];
				writer.write(costMatrix[i][j] + ",");
			}
			writer.write("\n");
		}
		writer.close();

		// duplicate matix where each cell is {maxPossibleValue - currentValue}
		HungImp hungImp = new HungImp(dupCostMatrix);
		// index -> Course, value -> Professor
		finalMapping = hungImp.execute();
	}

	/**
	 * This method gets called when the program is executed. It calls a method (
	 * mapCourseToProfessor ) responsible to compute cost matrix and implement
	 * algorithm on the same. Finally the returned integer array, which is an
	 * allocation of professor to course, is processed output is saved to csv
	 * file.
	 */
	public void getMappings() throws IOException {
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
