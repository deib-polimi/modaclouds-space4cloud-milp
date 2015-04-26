package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ModelAMPL extends Model {
	
	@Override
	public boolean print(String file1, String file2) {
		
		try {
			Files.copy(Configuration.getStream(Configuration.RUN_MODEL_STANDARD), Paths.get(Configuration.LOCAL_TEMPORARY_FOLDER, file1), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			Files.copy(Configuration.getStream(Configuration.RUN_MODEL_STARTING_SOLUTION), Paths.get(Configuration.LOCAL_TEMPORARY_FOLDER, file2), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public static void print() {
		ModelAMPL m = new ModelAMPL();
		m.print(Configuration.RUN_MODEL_STANDARD, Configuration.RUN_MODEL_STARTING_SOLUTION);
	}
}
