package it.polimi.modaclouds.space4clouds.milp.xmldatalists;

//container for information from AddInfData file
public class AddInfList {
	
	//amount of time intervals
	public int countTimeIntervals=0;
	//array of arrival rate
	public double[] arrivalrate=null;
	//array of time indexes for arrival rate
	public int[] TimeIndex=null;
	
	public double[] thinkTimes = null;
	
	//amount of providers
	public int countProviders=0;
	//array of minimum arrival rate per provider
	public double[] MinArrRate=null;
	//array of provider names
	public String[] ProviderNames=null;
	
	//minimum amount of providers
	public int MinProv=0;
	//maximum system response time
	public double MaxSystemResponseTime=0;
	
	//creates arrays for arrival rate
	public void setTimeIntervalsCount(int count)
	{
		countTimeIntervals=count;
		arrivalrate=new double [count];
		TimeIndex=new int [count];
		
		thinkTimes = new double[count];
	}
	
	//creates arrays for minimum arrival rate per provider
	public void setProvidersCount(int count)
	{
		countProviders=count;
		MinArrRate=new double [count];
		ProviderNames=new String [count];
	}
	
	//returns minimum arrival rate per provider by provider name
	public double getMinArrRateByProviderName(String ProvName)
	{
		int i=0;
		while (!((i==countProviders) || (ProviderNames[i].equalsIgnoreCase(ProvName)))) i++;
		if (i==countProviders)
			return -1;
		return MinArrRate[i];
	}
}
