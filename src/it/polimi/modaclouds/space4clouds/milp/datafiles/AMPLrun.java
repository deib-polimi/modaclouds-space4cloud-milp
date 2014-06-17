package it.polimi.modaclouds.space4clouds.milp.datafiles;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//this class creates local copy of AMPL.run file
//it is created and uploaded each time to have possibility to set CPLEX solver time limit
public class AMPLrun {

	// main function
	// file saves in AMPLrunFilePath
	// TimeLimit is used to set CPLEX solver time limit
	// UploadPath - directory om AMPL server (as it is required to use command
	// "cd UploadPath" in AMPL
	public void AMPLrunToFile(String AMPLrunFilePath, String TimeLimit,
			String UploadPath, String FilePathStartingSolution) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(AMPLrunFilePath));
			out.println("cd "
					+ UploadPath.substring(0, UploadPath.length() - 1) + ";");
			out.println("reset;");
			out.println("option log_file 'log.tmp';");
			if (FilePathStartingSolution != null) { 
				out.println("model modelstartingsolution.mod;");
//				out.println("option presolve 0;");
			} else
				out.println("model model.mod;");
			out.println("data data.dat;");
			out.println("option solver '/usr/optimization/ILOG2/cplex/bin/x86-64_sles10_4.1/cplexamp';");
			out.println("option show_stats 1;");
			if (!TimeLimit.equalsIgnoreCase(""))
				out.println("option timelimit " + TimeLimit + ";");
			out.println("option cplex_options 'timing=1';");
			out.println("solve;");
			out.println("display X,PartialArrRate,AmountVM > rez.out;");
			out.println("display {v in TYPE_VM, p in PROVIDER, i in CONTAINER:W[v,p,i]>0} (W[v,p,i]), W >> rez.out;");
			out.println("display DiffResponseTime >> rez.out;");
			out.println("display sum{t in TIME_INT, p in PROVIDER, i in CONTAINER, v in TYPE_VM} (Cost[v, p, i]*AmountVM[v, p, i, t]) > shortrez.out;");
			out.println("display {p in PROVIDER, t in TIME_INT, i in CONTAINER, v in TYPE_VM:AmountVM[v,p,i,t]>0} (AmountVM[v,p,i,t],PartialArrRate[p,t]) >> shortrez.out;");
			out.println("option log_file '';");
			out.println("close shortrez.out;");
			out.println("close rez.out;");
			out.println("close log.tmp;");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
