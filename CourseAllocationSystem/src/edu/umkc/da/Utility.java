package edu.umkc.da;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class provides methods to read data from CSV files   
 */                                            
public class Utility {
    String s =null;
	String s2=null; 
	public  List<Course> loadCourses() {
		List<Course> l = new ArrayList<>();
// create a map of course id and course object for each course
		try (BufferedReader br = new BufferedReader(new FileReader("/Users/nikhilyanamadala/Git-projects/Design-Analysis-Algorithms/CourseAllocationSystem/src/edu/umkc/da/data/Courses.csv"))) {
			String headerLine = br.readLine();
			while ((s = br.readLine()) != null) {
				String[] s1 = s.split("\\,");
				Course c = new Course();
				c.setCourseID(Integer.parseInt(s1[0]));
				c.setCourseName(s1[1]);
                l.add(c);
			}
			for (Course course : l) {
				loadCourseTopics(course.getCourseID());
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return l;
	}

	public List<Professor> loadProfessors() {
		// create a list of profess id and professor object for each course
		List<Professor> l = new ArrayList<>();
				try (BufferedReader br = new BufferedReader(new FileReader("/Users/nikhilyanamadala/Git-projects/Design-Analysis-Algorithms/CourseAllocationSystem/src/edu/umkc/da/data/Professors.csv"))) {
					String headerLine = br.readLine();
					while ((s = br.readLine()) != null) {
						String[] s1 = s.split("\\,");
						Professor p = new Professor();
						p.setProfessorID(Integer.parseInt(s1[0]));
						p.setName(s1[1]);
		                l.add(p);
					}
					for (Professor p : l) {
						loadProfessorExpertise(p.getProfessorID());
					}
				} catch (FileNotFoundException e) {
					System.out.println(e);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return l;
			}

	public  Map<Topic, Double> loadCourseTopics(int courseId) {
		Map<Topic,Double> m = new HashMap<>();
		// create a map of topic and weight in % for each course.
		// Hint - iterate over list of courses and perform operation for each course.
		try (BufferedReader br1 = new BufferedReader(new FileReader("/Users/nikhilyanamadala/Git-projects/Design-Analysis-Algorithms/CourseAllocationSystem/src/edu/umkc/da/data/CourseTopics.csv"))) {
						String headerLine1 = br1.readLine();
						while ((s = br1.readLine()) != null) {
							String[] s1 = s.split("\\,");
							if(courseId==Integer.parseInt(s1[0])) {
								Topic t = new Topic();
								t.setTopicID(Integer.parseInt(s1[1]));
								try (BufferedReader br = new BufferedReader(new FileReader("/Users/nikhilyanamadala/Git-projects/Design-Analysis-Algorithms/CourseAllocationSystem/src/edu/umkc/da/data/Topics.csv"))) 
								{
									String headerLine = br.readLine();
									while ((s2 = br.readLine()) != null) {
										String[] s3 = s2.split("\\,");
										if(t.getTopicID()==Integer.parseInt(s3[0]))
										 t.setTopicName(s3[1]); 
									}
								Double d=Double.parseDouble(s1[2]);
								m.put(t, d);
	}
	}
}
						
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return m;
	}

	public Map<Topic, Integer> loadProfessorExpertise(int professorID) {
		// Same as courseTopic
		Map<Topic,Integer> m1 = new HashMap<>();
		// create a map of topic and weight in % for each course.
		// Hint - iterate over list of courses and perform operation for each course.
		try (BufferedReader br1 = new BufferedReader(new FileReader("/Users/nikhilyanamadala/Git-projects/Design-Analysis-Algorithms/CourseAllocationSystem/src/edu/umkc/da/data/Expertise.csv"))) 
		{
						String headerLine1 = br1.readLine();
						while ((s = br1.readLine()) != null) {
							String[] s1 = s.split("\\,");
							if(professorID==Integer.parseInt(s1[1])) {
								Topic t = new Topic();
								t.setTopicID(Integer.parseInt(s1[0]));
								try (BufferedReader br = new BufferedReader(new FileReader("/Users/nikhilyanamadala/Git-projects/Design-Analysis-Algorithms/CourseAllocationSystem/src/edu/umkc/da/data/Topics.csv"))) 
								{
									String headerLine = br.readLine();
									while ((s2 = br.readLine()) != null) {
										String[] s3 = s2.split("\\,");
										if(t.getTopicID()==Integer.parseInt(s3[0]))
										 t.setTopicName(s3[1]); 
									}
								int d=Integer.parseInt(s1[2]);
								m1.put(t, d);
								}
							}
						}
		
	} catch (FileNotFoundException e) {
		System.out.println(e);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		for(Topic t:m1.keySet()) {
			System.out.println(t.getTopicID()+"..."+t.getTopicName()+"..."+m1.get(t));
		}
		return m1;
	}
	public static void main(String[] args) {
Utility u = new Utility();
  u.loadProfessors();
	}
	}