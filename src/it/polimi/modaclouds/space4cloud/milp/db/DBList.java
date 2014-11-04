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
package it.polimi.modaclouds.space4cloud.milp.db;

import it.polimi.modaclouds.space4cloud.milp.xmldatalists.AddInfList;

import java.util.ArrayList;
import java.util.List;

//class contains information from AddInfData and SQL database
//receives information from AddInfDataList
//uses to have more comfortable interaction with values of min arrival rates per provider
public class DBList {
	// amount of providers
	public int countProviders = 0;

	// array of provider ids
	public int[] ProviderId = null;

	// array of provider names
	public String[] ProviderName = null;

	// array of minimum arraival rates per providers
	public double[] MinArrRate = null;

	// amount of time intervals
	public int countTimeIntervals = 0;

	// array of arrival rates
	public double[] arrivalrate = null;

	// array of time interval indexes
	public int[] TimeIndex = null;
	
	public double[] thinkTimes = null;

	// minimum amount of providers
	public int MinProv = 0;
	
	public double[] availabilities;

	// constructor
	public DBList(int count) {
		countProviders = count;
		ProviderId = new int[count];
		ProviderName = new String[count];
		MinArrRate = new double[count];
		availabilities = new double[count];
	}

	// index - provider's number in the program
	// Id - provider's Id from SQL database
	// this function allows to receive provider's index by its id
	public int getProviderIndexById(int id) {
		int i = 0;
		while (!((i == countProviders) || (ProviderId[i] == id)))
			i++;
		if (i == countProviders)
			return -1;
		return i;
	}

	// allows to transfer data from AddInfList to this
	public void setAddInfData(AddInfList CAIList) {
		countTimeIntervals = CAIList.countTimeIntervals;
		arrivalrate = new double[countTimeIntervals];
		
		thinkTimes = new double[countTimeIntervals];
		
		TimeIndex = new int[countTimeIntervals];
		for (int i = 0; i < countTimeIntervals; i++) {
			arrivalrate[i] = CAIList.arrivalrate[i];
			TimeIndex[i] = CAIList.TimeIndex[i];
			
			thinkTimes[i] = CAIList.thinkTimes[i];
		}
		for (int i = 0; i < countProviders; i++)
			MinArrRate[i] = CAIList
					.getMinArrRateByProviderName(ProviderName[i]);
		MinProv = CAIList.MinProv;
	}

	// creates list of provider names
	public List<String> getProviderList() {
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < countProviders; i++)
			res.add(ProviderName[i]);
		return res;
	}
}
