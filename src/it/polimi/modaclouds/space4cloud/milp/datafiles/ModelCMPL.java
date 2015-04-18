package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class ModelCMPL extends Model {

	@Override
	public boolean print(String file1, String file2) {
		
		return singlePrint(Configuration.RUN_MODEL_STANDARD_CMPL, file1) && singlePrint(Configuration.RUN_MODEL_STARTING_SOLUTION_CMPL, file2);
	}
	
	private boolean singlePrint(String origFile, String destFile) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(destFile));
			
			String baseFile = "";
			
			Scanner sc = new Scanner(Configuration.getStream(origFile));
			
			while (sc.hasNextLine())
				baseFile += sc.nextLine() + "\n";
			
			sc.close();
			
			int threads = Configuration.CMPL_THREADS;
			if (Configuration.isRunningLocally()) {
				threads = Runtime.getRuntime().availableProcessors() - 1;
				if (threads <= 0)
					threads = 1;
			}
			
			out.print(String.format(baseFile,
					Configuration.RUN_SOLVER_CMPL,
					Configuration.RUN_RES_CMPL,
					Configuration.RUN_DATA_CMPL,
					threads));
			
			out.flush();
			out.close();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void print() {
		ModelCMPL m = new ModelCMPL();
		m.print(Configuration.RUN_MODEL_STANDARD_CMPL, Configuration.RUN_MODEL_STARTING_SOLUTION_CMPL);
	}
}
