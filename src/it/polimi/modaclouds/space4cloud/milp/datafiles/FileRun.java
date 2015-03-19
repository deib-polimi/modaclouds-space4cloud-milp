package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

public abstract class FileRun {
	public abstract void print(String AMPLrunFilePath, String TimeLimit, String UploadPath, String FilePathStartingSolution);
	
	public static void print() {
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			FileRunAMPL.print();
			break;
		case CMPL:
			FileRunCMPL.print();
			break;
		}
	}
}
