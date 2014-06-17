package it.polimi.modaclouds.space4clouds.milp.types;

//container for VM types information from SQL database
//(without sorting by provider)
//all information as in the database
public class SqlBaseParsElement {

	public int[] ProviderId = null;
	public String[] Provider = null;
	public String[] ServiceName = null;// for example, EC2
	public double[] processingRate = null;
	public double[] numberOfReplicas = null;
	public double[] cost = null;
	public double[] MemorySize = null;
	public String[] TypeName = null;
	public int ncount;// length of list
	
	public String[] Region = null;

	// constructor
	public SqlBaseParsElement(int count) {
		processingRate = new double[count];
		numberOfReplicas = new double[count];
		cost = new double[count];
		MemorySize = new double[count];
		Provider = new String[count];
		ServiceName = new String[count];
		TypeName = new String[count];
		ProviderId = new int[count];
		ncount = count;
		
		Region = new String[count];
	}
}
