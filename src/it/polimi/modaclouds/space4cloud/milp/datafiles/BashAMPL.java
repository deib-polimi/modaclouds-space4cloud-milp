package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

public class BashAMPL extends Bash {
	
	@Override
	public boolean print(String file) {
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(Paths.get(Configuration.LOCAL_TEMPORARY_FOLDER, file).toFile()));
			
			String baseFile = ""; //new String(Files.readAllBytes(Paths.get(Configuration.DEFAULTS_FOLDER, Configuration.RUN_FILE))); //, Charset.defaultCharset()); // StandardCharsets.UTF_8);
			
//			String baseFile = new String(Files.readAllBytes(Paths.get(this.getClass().getResource(Configuration.RUN_FILE).toURI())));
			
			Scanner sc = new Scanner(Configuration.getStream(Configuration.DEFAULTS_BASH));
			
			while (sc.hasNextLine())
				baseFile += sc.nextLine() + "\n";
			
			sc.close();
			
			out.printf(baseFile,
					Configuration.RUN_WORKING_DIRECTORY,
					Configuration.RUN_LOG,
					Configuration.RUN_RES,
					Configuration.RUN_AMPL_FOLDER,
					Configuration.RUN_FILE);
			
			out.flush();
			out.close();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static void print() {
		BashAMPL b = new BashAMPL();
		b.print(Configuration.DEFAULTS_BASH);
	}

}
