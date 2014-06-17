package it.polimi.modaclouds.space4clouds.milp.types;

//lists of services with Ids and SEFF names (from Repository Diagram)
public class ServiceNames {
	public String[] Id = null;// Id
	public String[] SEFFName = null;// SEFF names
	public int count = 0; // amount of services

	// constructor
	public ServiceNames(int ncount) {
		Id = new String[ncount];
		SEFFName = new String[ncount];
		count = ncount;
	}
}
