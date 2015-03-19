package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;


public abstract class Model {

	public abstract boolean print(String file1, String file2);

	public static void print() {
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			ModelAMPL.print();
			break;
		case CMPL:
			ModelCMPL.print();
			break;
		}
	}
}
