/**
 * Copyright ${year} deib-polimi
 * Contact: deib-polimi <giovannipaolo.gibilisco@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.space4cloud.milp.xmldatalists;

import it.polimi.modaclouds.space4cloud.milp.types.SystemTreeDemandArray;

//container for the information from Repository Diagram
public class RepositoryList {
	
	//class Ids (Id of first service for each class)
	public String[] ClassIds=null;
	public String[] ServiceIds=null;
	//probability distribution between classes (calculated from Usage diagram)
	public double[] ProbVectorClasses=null;
	//amount of classes
	public int ncountClasses=0;
	
	//Ids of components
	public String[] ComponentIds=null;
	//Names of Components
	public String[] ComponentNames=null;
	//probability distributions of classes to be in the components
	//calculated by SEFF
	public double[][] ProbMatrClassesComponents=null;
	//relative Demand for the component (used to calculate maximum response times for components)
	public double[][] relativeComponentDemand=null;
	//Resource Demand for the component
	public double[] ComponentDemand=null;
	//array of Maximum service rates
	public double[][] MaximumSR=null;
	//array of maximum response times
	public double[][] MaxResponseTime=null;
	//amount of components
	public int ncountComponents=0;
	
	//maximum system response time
	public double MaxSystemResponseTime=0;
	//minimum speed of VM types
	public double VMtypesProcessingRateNorm=1;
	
	//Ids in System Diagram for corresponding elements (received by ComponentIds)
	public String[] SystemIds=null;
	//Names in System Diagram for corresponding elements (received by SystemIds)
	public String[] SystemNames=null;
	
	//Ids in corresponding Elements in Allocation Diagram (received by SystemIds)
	public String[] AllocationId=null;
	
	//Ids of corresponding containers in Resource Environment Diagram (received by AllocationIds)
	public String[] ContainerId=null;
	
	//array of allocation components between containers
	public int[] CorrespondContainerByComponent=null;
	//array with binary variable that components in the same container
	public int[][] ComponentsInTheSameContainer=null;

	//container for the information from Resource environment Diagram
	public ResEnvList ContainerList=null;
	
	public double[] DemandperContainer=null;
	
	public int effictype[][] =null;
	public int cheaptype[][] =null;
	public double efficrate[][] =null;
	public double cheapcost[][] =null;
	public double efficspeed[][] =null;
	
	public int countproviders =0;
	
	public int finalefficprov =0;
	public int finalcheapprov =0;
	
	public double U=0;

	//constructor
	public RepositoryList(){}
	
	public void DemandperContainerCalc()
	{
		DemandperContainer=new double [ContainerList.ncount];
		for (int i=0;i<ContainerList.ncount;i++)
			DemandperContainer[i]=0;
		for (int i=0;i<ncountComponents;i++)
		{
			int k=CorrespondContainerByComponent[i];
			DemandperContainer[k]+=DemandComponentsperClasses(i)/MaximumSR[0][i];
		}
	}
	
	public int DemandComponentsperClasses(int i)
	{
		int rez=0;
		for (int j=0;j<ncountClasses;j++)
			if (ProbMatrClassesComponents[j][i]>0) rez++;
		return rez;
	}
	
	//creates all required arrays
	public void Init(int countComponents)
	{
		ComponentIds=new String [countComponents];
		ComponentNames=new String [countComponents];
		ProbMatrClassesComponents=new double [ncountClasses][countComponents];
		relativeComponentDemand=new double [ncountClasses][countComponents];
		ComponentDemand=new double [countComponents];
		MaximumSR=new double [ncountClasses][countComponents];
		MaxResponseTime=new double [ncountClasses][countComponents];
		ncountComponents=countComponents;
		
		SystemIds=new String [countComponents];
		SystemNames=new String [countComponents];
		
		AllocationId=new String [countComponents];
		
		ContainerId=new String [countComponents];
		
		CorrespondContainerByComponent=new int [countComponents];
		ComponentsInTheSameContainer=new int [countComponents][countComponents];
	}
	
	//sets Maximum System Response time
	public void setMaxSystemResponseTime(double MRtime)
	{
		MaxSystemResponseTime=MRtime;
	}
	
	//sets minimum value for speed of VM types
	public void setVMtypesProcessingRateNorm(double ProcessingRate)
	{
		VMtypesProcessingRateNorm=ProcessingRate;
	}
	
	//Transfers information from container for data from UsageModel diagram
	public void setClasses(UsageList CUList)
	{
		ncountClasses=CUList.ncount;
		ProbVectorClasses=new double [ncountClasses];
		ClassIds=new String [ncountClasses];
		ServiceIds=new String [ncountClasses];
		for (int i=0;i<ncountClasses;i++)
		{
			ProbVectorClasses[i]=CUList.probdistr[i];
			ClassIds[i]=CUList.ClassRepositoryId[i];
		}
	}
	
	//Transfers information from container for data from System diagram
	public void setSystemIdsandNames(SystemList CSList)
	{
		for (int i=0;i<ncountComponents;i++)
		{
			//System.out.println(ComponentIds[i]);
			int k=CSList.getNumByRepositoryId(ComponentIds[i]);
			SystemIds[i]=CSList.Id[k];
			SystemNames[i]=CSList.Name[k];
		}
	}
	
	//Transfers information from container for data from Allocation diagram
	public void setAllocationandResEnvIds(AllocationList CAList)
	{
		for (int i=0;i<ncountComponents;i++)
		{
			int k=CAList.getNumBySystemId(SystemIds[i]);
			AllocationId[i]=CAList.Id[k];
			ContainerId[i]=CAList.ResourceEnvironment[k];
		}
	}
	
	//Transfers information from container for data from Resource Environment diagram
	public void setContainers(ResEnvList CREList)
	{
		ContainerList=CREList;
		for (int i=0;i<ncountComponents;i++)
			CorrespondContainerByComponent[i]=getContainerNumById(ContainerId[i]);
		for (int i=0;i<ncountComponents;i++)
			for (int j=0;j<ncountComponents;j++)
			{
				if (CorrespondContainerByComponent[i]==CorrespondContainerByComponent[j])
					ComponentsInTheSameContainer[i][j]=1;
				else
					ComponentsInTheSameContainer[i][j]=0;
			}
	}
	
	//returns number of container (in program memory) by its Id
	private int getContainerNumById(String newContainerId)
	{
		int res=0;
		while (!((res==ContainerList.ncount) || (ContainerList.Id[res].equalsIgnoreCase(newContainerId))))
			res++;
		if (res==ContainerList.ncount) 
			return -1;
		return res;
	}
	
	//calculates Maximum System Response time and Maximum Service Rate
	public void calcMaximumServiceRateAndMaxResponseTime()
	{
		for (int i=0;i<ncountClasses;i++)
			for (int j=0;j<ncountComponents;j++)
			{
				MaximumSR[i][j]=VMtypesProcessingRateNorm/ComponentDemand[j];
				MaxResponseTime[i][j]=MaxSystemResponseTime*relativeComponentDemand[i][j];
			}
	}
	
	//calculates relative demands for the components (see chapter 3 of the thesis)
	public void setRelativeDemand(int classindex,SystemTreeDemandArray CArray)
	{
		for (int i=0;i<ncountComponents;i++)
		{
			if (CArray.Demand[i]>0)
				relativeComponentDemand[classindex][i]=ComponentDemand[i]*CArray.RDcount[i]/CArray.Demand[i];
			else
				relativeComponentDemand[classindex][i]=0;
		}
	}
	
	public void initefficcheaparrays(int nproviders)
	{
		effictype=new int [nproviders][ContainerList.ncount];
		cheaptype=new int [nproviders][ContainerList.ncount];
		efficrate=new double [nproviders][ContainerList.ncount];
		cheapcost=new double [nproviders][ContainerList.ncount];
		efficspeed=new double [nproviders][ContainerList.ncount];
		countproviders=nproviders;
		for (int i=0;i<nproviders;i++)
			for (int j=0;j<ContainerList.ncount;j++)
			{
				effictype[i][j]=0;
				cheaptype[i][j]=0;
				efficrate[i][j]=0;
				cheapcost[i][j]=2000;
				efficspeed[i][j]=10000;
			}
	}
	public void updateefficcheap(int typem,int provm,int contm,double costm,double speedm)
	{
		if (costm<=cheapcost[provm][contm])
		{
			cheapcost[provm][contm]=costm;
			cheaptype[provm][contm]=typem;
		}	
		
		double rate=speedm/costm;
		if ((rate>efficrate[provm][contm]) || ((rate==efficrate[provm][contm]) && (speedm<efficspeed[provm][contm])))
		{
			efficrate[provm][contm]=rate;
			effictype[provm][contm]=typem;
			efficspeed[provm][contm]=speedm;
		}
	}
	public void findefficandcheapbetweenproviders()
	{
		double tempcost=2000,temprate=0,tempspeed=10000;
		for (int i=0;i<countproviders;i++)
			for (int j=0;j<ContainerList.ncount;j++)
			{
				if (tempcost>=cheapcost[i][j])
				{
					finalcheapprov=i;
					tempcost=cheapcost[i][j];
				}
				if ((temprate<efficrate[i][j]) || ((temprate==efficrate[i][j]) && (tempspeed>efficspeed[i][j])))
				{
					finalefficprov=i;
					temprate=efficrate[i][j];
					tempspeed=efficspeed[i][j];
				}
			}
	}
}
