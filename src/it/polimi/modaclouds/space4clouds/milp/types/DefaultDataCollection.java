package it.polimi.modaclouds.space4clouds.milp.types;

//default values of AMPL data
//they are not used in computation process
// and are added to data.dat file to notation reasons
public class DefaultDataCollection {

	public double ProbabilityToBeInComponent=0.1;//probabilities that classes will have ways cross components
	public double ArrRate=100;//arrival rate
	public double MaximumSR=1;//maximum service rate
	public int PartitionComponents=1;//binary variable which shows that two components are in the same container
	public double Speed=1;//Speed of VM type
	public double Cost=0.06;//Cost of VM type
	public double MaxResponseTime=0.04;//Maximum Response time for component
	public double MinArrRate=0.2;//minimum arrival rate per provider
	public double Alpha=0.1;//proportion of arrival rate on class k

}
