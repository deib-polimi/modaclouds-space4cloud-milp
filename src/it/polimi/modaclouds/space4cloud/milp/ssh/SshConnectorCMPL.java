package it.polimi.modaclouds.space4cloud.milp.ssh;

import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.Solver;

public class SshConnectorCMPL extends SshConnector {
	
	@Override
	public void execute() {
		exec(String.format("mkdir %s", Configuration.RUN_WORKING_DIRECTORY));
		
		sendFileToWorkingDir(Configuration.RUN_DATA_CMPL);
		sendFileToWorkingDir(Configuration.RUN_FILE_CMPL);
		sendFileToWorkingDir(Configuration.DEFAULTS_BASH_CMPL);
		sendFileToWorkingDir(Configuration.RUN_MODEL_STANDARD_CMPL);
		sendFileToWorkingDir(Configuration.RUN_MODEL_STARTING_SOLUTION_CMPL);
		
		exec(
				String.format("bash %s/%s",
						Configuration.RUN_WORKING_DIRECTORY,
						Configuration.DEFAULTS_BASH_CMPL));

		receiveFileFromWorkingDir(Configuration.RUN_LOG_CMPL);
		receiveFileFromWorkingDir(Configuration.RUN_RES_CMPL);
		
		if (Solver.removeTempFiles)
			exec(String.format("rm -rf %s", Configuration.RUN_WORKING_DIRECTORY));
	}
	
	public static void run() {
		SshConnectorCMPL ssh = new SshConnectorCMPL();
		ssh.execute();
	}
	
}
