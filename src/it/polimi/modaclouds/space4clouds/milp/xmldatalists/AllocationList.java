package it.polimi.modaclouds.space4clouds.milp.xmldatalists;

//container for allocation pairs from Allocation Diagram
public class AllocationList {

	//Id of the pair in Allocation Diagram
	public String[] Id=null;
	//Id of corresponding Element in Resorce Environment Diagram
	public String[] ResourceEnvironment=null;
	//Id of corresponding element in System Diagram
	public String[] System=null;
	//amount of pairs
	public int ncount=0;
	
	//constructors
	public AllocationList(){}
	public AllocationList(int count)
	{
		ncount=count;
		Id=new String [count];
		ResourceEnvironment=new String [count];
		System=new String [count];
	}
	
	//returns number of Allocation pair by its Id
	public int getNumBySystemId(String newSystemId)
	{
		int res=0;
		while (!((res==ncount) || (System[res].equalsIgnoreCase(newSystemId))))
			res++;
		if (res==ncount) 
			return -1;
		return res;
	}
}
