package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Model {

	public boolean print(String file) {
		
		try {
			Files.copy(this.getClass().getResourceAsStream(file), Paths.get(file), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			Files.copy(this.getClass().getResourceAsStream(file), Paths.get(file), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public static void print() {
		Model m = new Model();
		m.print(Configuration.RUN_MODEL_STANDARD);
		
		m = new Model();
		m.print(Configuration.RUN_MODEL_STARTING_SOLUTION);
	}
}
