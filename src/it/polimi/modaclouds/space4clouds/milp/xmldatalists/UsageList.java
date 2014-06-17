package it.polimi.modaclouds.space4clouds.milp.xmldatalists;

//container for the information from UsageModel Diagram
//classes are formed as Internal Actions
public class UsageList {
	
	//probability distribution between classes (calculates according to amount of calls of classes)
	public double[] probdistr=null;
	//array of Ids of first services in Repository Diagram from each class
	public String[] ClassRepositoryId=null;
	//amount of classes
	public int ncount;
	
	//constructors
	public UsageList(){}
	public UsageList(int count)
	{
		ncount=count;
		probdistr=new double [count];
		ClassRepositoryId=new String [count];
	}
}
