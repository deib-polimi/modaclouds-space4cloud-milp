package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

public abstract class Bash {
	
	public abstract boolean print(String file);

	public static void print() {
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			BashAMPL.print();
			break;
		case CMPL:
			BashCMPL.print();
			break;
		}
	}
}
