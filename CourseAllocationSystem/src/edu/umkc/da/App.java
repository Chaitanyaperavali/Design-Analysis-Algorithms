package edu.umkc.da;

import java.io.IOException;

//Class from where application runs
public class App {
	public static void main(String[] args) {
		Mapping mapping = new Mapping();
		try {
			mapping.getMappings();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
