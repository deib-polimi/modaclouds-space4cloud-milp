package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileRunCMPL extends FileRun {
	
	@Override
	public void print(String AMPLrunFilePath, String TimeLimit,
			String UploadPath, String FilePathStartingSolution) {
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(AMPLrunFilePath));
			
			String baseFile = ""; //new String(Files.readAllBytes(Paths.get(Configuration.DEFAULTS_FOLDER, Configuration.RUN_FILE))); //, Charset.defaultCharset()); // StandardCharsets.UTF_8);
			
//			String baseFile = new String(Files.readAllBytes(Paths.get(this.getClass().getResource(Configuration.RUN_FILE).toURI())));
			
			Scanner sc = new Scanner(Configuration.getStream(Configuration.RUN_FILE_CMPL));
			
			while (sc.hasNextLine())
				baseFile += sc.nextLine() + "\n";
			
			sc.close();
			
			out.printf(baseFile,
					Configuration.RUN_WORKING_DIRECTORY,
					Configuration.RUN_CMPL_FOLDER,
					FilePathStartingSolution != null ? Configuration.RUN_MODEL_STARTING_SOLUTION_CMPL : Configuration.RUN_MODEL_STANDARD_CMPL,
					Configuration.RUN_LOG_CMPL
					);
			
			out.flush();
			out.close();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void print() {
		FileRunCMPL newCMPLrun = new FileRunCMPL();
		newCMPLrun.print(Configuration.RUN_FILE_CMPL,
				Configuration.SolverTimeLimit, Configuration.RUN_WORKING_DIRECTORY, Configuration.FilePathStartingSolution);
	}
}
