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
package it.polimi.modaclouds.space4clouds.milp.types;

//container for data which should be printed into data.dat file (for AMPL)
public class DataCollection {

	public int CountContainers=0;//amount of containers
	public int CountProviders=0;//amount of providers
	public int CountClasses=0;//amount of classes
	public int CountTypeVMs=0;//amount of VM types
	public int CountTimeInts=0;//amount of Time Intervals
	public int CountComponents=0;//amount of Components
	
	public double[][] ProbabilityToBeInComponent=null;//probabilities that classes will have ways cross components
	public double[] ArrRate=null;//arrival rate
	public double[][] MaximumSR=null;//maximum service rate
	public int[][] PartitionComponents=null;//binary variable which shows that two components are in the same container
	public double[][][] Speed=null;//Speed of VM type
	public double[][][] Cost=null;//Cost of VM type
	public double[][] MaxResponseTime=null;//Maximum Response time for component
	public double[] MinArrRate=null;//minimum arrival rate per provider
	public double[] Alpha=null;//proportion of arrival rate on class k
	public int MaxVMPerContainer=5000;//maximum amount of VMs in container
	public int MinProv=0;//minimum amount of providers
	
	//container for default values of AMPL parameters
	public DefaultDataCollection defaultvalues=null;
	
	//creates all arrays and sub-containers
	public void initialization()
	{
		ProbabilityToBeInComponent=new double [CountClasses][CountComponents];
		ArrRate=new double [CountTimeInts];
		MaximumSR=new double [CountClasses][CountComponents];
		PartitionComponents=new int [CountComponents][CountComponents];	
		Speed=new double [CountTypeVMs][CountProviders][CountContainers];
		Cost=new double [CountTypeVMs][CountProviders][CountContainers];
		MaxResponseTime=new double [CountClasses][CountComponents];
		MinArrRate=new double [CountProviders];
		Alpha=new double [CountClasses];
		defaultvalues=new DefaultDataCollection();
	}
}
