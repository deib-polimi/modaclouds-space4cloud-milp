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
package it.polimi.modaclouds.space4cloud.milp.types;

//container for sorted information (by provider) from SQL database
public class SqlBaseParsMatrix {
	
	public int[][] ProviderId=null;
	public String[][] Provider=null;
	public String[][] ServiceName=null;//for example, EC2
	public double[][] processingRate=null;
	public double[][] numberOfReplicas=null;
	public double[][] cost=null;
	public double[][] MemorySize=null;
	public double SpeedNorm=0; //ethalon speed value (minimum value of speed between types)
	public String[][] TypeName = null;
	public double[][] Speed = null;
	public int y;// amount of types
	public int x;// amount of providers
	
	public String[][] Region = null;

	// constructor
	public SqlBaseParsMatrix(int county, int countx) {
		processingRate = new double[county][countx];
		numberOfReplicas = new double[county][countx];
		cost = new double[county][countx];
		Speed = new double[county][countx];
		MemorySize = new double[county][countx];
		Provider = new String[county][countx];
		ServiceName = new String[county][countx];
		TypeName = new String[county][countx];
		ProviderId = new int[county][countx];
		
		Region = new String[county][countx];
		
		y = county;
		x = countx;
		for (int i = 0; i < y; i++)
			for (int j = 0; j < x; j++) {
				ProviderId[i][j] = -1;
				Provider[i][j] = "";
				TypeName[i][j] = "";
				cost[i][j] = 1200;
				Speed[i][j] = 0;
				MemorySize[i][j] = 0;
				numberOfReplicas[i][j] = 0;
				processingRate[i][j] = 0;
				
				Region[i][j] = "";
			}
	}

	// selects minimum values of speed between types
	// then calculates capacity values by the types
	public double makeSpeedNorm() {
		for (int i = 0; i < y; i++)
			for (int j = 0; j < x; j++)
				Speed[i][j] = numberOfReplicas[i][j] * processingRate[i][j];
		double min = Double.MAX_VALUE;
		// selecting minimum value of VM type's speed
		for (int i = 0; i < y; i++)
			for (int j = 0; j < x; j++) {
				if ((Speed[i][j] > 0) && (min > Speed[i][j]))
					min = Speed[i][j];
			}
		// calculating capacities
		for (int i = 0; i < y; i++)
			for (int j = 0; j < x; j++) {
				Speed[i][j] = Speed[i][j] / min;
			}
		return SpeedNorm = min;
	}
}