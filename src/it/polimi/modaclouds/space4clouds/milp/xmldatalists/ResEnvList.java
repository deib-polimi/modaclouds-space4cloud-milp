package it.polimi.modaclouds.space4clouds.milp.xmldatalists;

//contains information from Resource Environment Diagram
public class ResEnvList {

	public String[] Id=null;// id of container
	public String[] Name=null;// name of container
	
	//amount of containers
	public int ncount=0;
	
	//constructors
	public ResEnvList(){}
	public ResEnvList(int count)
	{
		ncount=count;
		Name=new String [count];
		Id=new String [count];
	}
}
