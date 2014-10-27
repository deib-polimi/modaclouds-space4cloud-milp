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
package it.polimi.modaclouds.space4clouds.milp.db;

import it.polimi.modaclouds.space4clouds.milp.types.ClassOptions;
import it.polimi.modaclouds.space4clouds.milp.types.SqlBaseParsElement;
import it.polimi.modaclouds.space4clouds.milp.types.SqlBaseParsMatrix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//class for interaction with database
public class SQLParser {

	// amount of providers
	public int countproviders = 0;

	// main container for received parameters of VM types
	public SqlBaseParsMatrix resMatrix = null;

	// container for information from AddInfData file
	// used to simple interaction with values minimum arrival rates per provider
	// firstly information from AddInfData files is saved in AddInfList and then
	// transfered to this container
	public DBList newDBList = null;

	// array of provider ids
	private int[] ProviderId = null;

	// array of provider names
	private String[] ProviderName = null;

	// amounts of VM types of each provider
	private int[] CountTypesByProviders = null;

	// Database contains list of providers and only subset of them provides IaaS
	// this array is used to select them
	private boolean[] ProviderIsUsed = null;

	// connection and statement to SQL database
	private static Connection conn = null;
//	private Statement st = null;

	private ArrayList<String> allowedProviders = null;
	private ArrayList<String> allowedRegions = null;
	
	private static String driver;
	private static String url;
	private static String dbName;
	private static String userName;
	private static String password;
	
	public SQLParser(ClassOptions currOptions) {
		allowedProviders = currOptions.AllowedProviders;
		allowedRegions = currOptions.AllowedRegions;
		driver =currOptions.DBDriver;
		url = currOptions.SqlDBUrl;
		dbName = currOptions.DBName;
		userName = currOptions.DBUserName;
		password = currOptions.DBPassword;
		parseit();
	}

	// this function is used for requests which receives information about
	// amount
	// i.e. "SELECT count(*) FROM..."
	private int getTableCount(String SQLRequest) {
		int res = 0;
		try {
			Connection conn = getConnection();
			Statement st = conn.createStatement();
			ResultSet temprs = st.executeQuery(SQLRequest);
			if (temprs.next()) {
				String Temp = temprs.getString(1);
				res = Integer.parseInt(Temp);
			}
			if (temprs != null)
				temprs.close();
			if (st != null)
				st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private String researchSqlProviders = "";
	private String researchSqlRegions = "";
	
	private static void connect() throws SQLException {
		try {
			Class.forName(driver).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		conn = DriverManager
				.getConnection(url + dbName, userName, password);
		if(conn == null)
			System.err.println("Error in connecting to the database");
	}

	/**
	 * Returns the Connection to the MySQL database.
	 * 
	 * @return the Connection instance.
	 * @throws SQLException 
	 */
	private static Connection getConnection() {
		try {
			if (conn == null || conn.isClosed() || !conn.isValid(10000))
				connect();
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	// main function for interaction with database
	// driver - the jdbc driver
	// url - the url of the db
	// base - name of database
	// user - database login
	// password - database password
	public int parseit() {
		try {

			// creates connection
			conn = getConnection();
			
			Statement st = conn.createStatement();

			if (allowedProviders == null) {
				// receives amount of providers
				countproviders = getTableCount(SQLRequestsCollection.CountProvidersRequest);
			} else {
				// receives amount of providers
				countproviders = allowedProviders.size();
				
//				researchSqlProviders = " AND (";
//				int i;
//				for (i = 0; i < allowedProviders.size()-1; ++i)
//					researchSqlProviders += "cloudprovider.name = '" + allowedProviders.get(i) + "' OR ";
//				researchSqlProviders += "cloudprovider.name = '" + allowedProviders.get(i) + "')";
				
				researchSqlProviders = " AND cloudprovider.name IN (";
				int i;
				for (i = 0; i < allowedProviders.size()-1; ++i)
					researchSqlProviders += "'" + allowedProviders.get(i) + "', ";
				researchSqlProviders += "'" + allowedProviders.get(i) + "')";
			}
			
			if (allowedRegions != null) {
				researchSqlRegions = " AND cost.region IN (";
				int i;
				for (i = 0; i < allowedRegions.size()-1; ++i)
					researchSqlRegions += "'" + allowedRegions.get(i) + "', ";
				researchSqlRegions += "'" + allowedRegions.get(i) + "')";
			}
			
			// receives amount of all VM types
			int fullcounttypes = getTableCount(String.format(SQLRequestsCollection.CountTypesRequest, researchSqlProviders, researchSqlRegions));

			ProviderId = new int[countproviders];
			ProviderName = new String[countproviders];
			CountTypesByProviders = new int[countproviders];

			// receives list of providers
			ResultSet rp = st.executeQuery(SQLRequestsCollection.ProviderRequest);
			int schet = 0;
			while (rp.next()) {
				if (allowedProviders == null || allowedProviders.contains(rp.getString(2))) {
					String Fstr = rp.getString(1);
					ProviderId[schet] = Integer.parseInt(Fstr);
					Fstr = rp.getString(2);
					ProviderName[schet] = Fstr;
					schet++;
				}
			}
			if (rp != null)
				rp.close();

			// call constructor of temp container of VM types
			SqlBaseParsElement newSqlBaseParsElement = new SqlBaseParsElement(
					fullcounttypes);

			// receives parameters of VM types (without memory)
			// saves it in newSqlBaseParsElement
			// newSqlBaseParsElement contains list of all VM types without
			// partition by providers
			ResultSet rs = null;
			try {
				rs = st.executeQuery(String.format(SQLRequestsCollection.ProcessorRequest, researchSqlProviders, researchSqlRegions));
			} catch (Exception e) {
				rs = st.executeQuery(String.format(SQLRequestsCollection.ProcessorRequestNoRegion, researchSqlProviders));
			}
			schet = 0; // simple counter by number of VM types
			while (rs.next()) {
				String Fstr = rs.getString(1);
				newSqlBaseParsElement.ServiceName[schet] = Fstr;
				Fstr = rs.getString(2);
				newSqlBaseParsElement.Provider[schet] = Fstr;
				Fstr = rs.getString(3);
				newSqlBaseParsElement.ProviderId[schet] = Integer
						.parseInt(Fstr);
				Fstr = rs.getString(4);
				newSqlBaseParsElement.processingRate[schet] = Double
						.parseDouble(Fstr);
				Fstr = rs.getString(5);
				newSqlBaseParsElement.numberOfReplicas[schet] = Double
						.parseDouble(Fstr);
				Fstr = rs.getString(6);
				newSqlBaseParsElement.cost[schet] = Double.parseDouble(Fstr);
				Fstr = rs.getString(7);
				newSqlBaseParsElement.TypeName[schet] = Fstr;
				
				Fstr = rs.getString(8);
				newSqlBaseParsElement.Region[schet] = Fstr;
				
				// increase amount of VM types for provider with id
				// ProviderId[schet]
				IncrementTypesByProviderId(newSqlBaseParsElement.ProviderId[schet]);
				schet++;
			}
			if (rs != null)
				rs.close();

			// receives memory parameters for VM types
			ResultSet rm = st.executeQuery(String.format(SQLRequestsCollection.MemoryRequest, researchSqlProviders, researchSqlRegions));
			schet = 0;
			while (rm.next()) {
				String Fstr = rm.getString(1);
				newSqlBaseParsElement.MemorySize[schet] = Double
						.parseDouble(Fstr);
				// System.out.println(newSqlBaseParsElement.MemorySize[schet]);
				schet++;
			}
			if (rm != null)
				rm.close();

			// close statement and connection
			if (st != null)
				st.close();
			if (conn != null)
				conn.close();

			// selects IaaS providers from full list of providers
			int newcount = 0;
			ProviderIsUsed = new boolean[countproviders];
			for (int i = 0; i < countproviders; i++)
				if (CountTypesByProviders[i] > 0) {
					ProviderIsUsed[i] = true;
					newcount++;
				} else
					ProviderIsUsed[i] = false;

			// creates DBList
			newDBList = new DBList(newcount);

			// transfers Ids and names of IaaS providers in DBList container
			int nschet = 0;
			for (int i = 0; i < countproviders; i++)
				if (ProviderIsUsed[i]) {
					newDBList.ProviderId[nschet] = ProviderId[i];
					newDBList.ProviderName[nschet] = ProviderName[i];
					nschet++;
				}

			// creates final container for parsed data
			resMatrix = new SqlBaseParsMatrix(newDBList.countProviders,
					getCountTypes());
			int[] IndexTypeOfProvider = new int[newDBList.countProviders];

			for (int i = 0; i < newDBList.countProviders; i++)
				IndexTypeOfProvider[i] = 0;

			// sorts VM types by providers
			// IndexTypeOfProvider[j] - amount of VM types for provider with
			// index j (on current step of sorting algorithm)
			for (int i = 0; i < fullcounttypes; i++) {
				int k = newSqlBaseParsElement.ProviderId[i];
				int j = newDBList.getProviderIndexById(k);
				resMatrix.ProviderId[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.ProviderId[i];
				resMatrix.ServiceName[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.ServiceName[i];
				resMatrix.Provider[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.Provider[i];
				resMatrix.cost[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.cost[i];
				resMatrix.MemorySize[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.MemorySize[i];
				resMatrix.numberOfReplicas[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.numberOfReplicas[i];
				resMatrix.processingRate[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.processingRate[i];
				resMatrix.TypeName[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.TypeName[i];
				
				resMatrix.Region[j][IndexTypeOfProvider[j]] = newSqlBaseParsElement.Region[i];
				
				IndexTypeOfProvider[j]++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	// increases amount of VM type for provider with id
	private void IncrementTypesByProviderId(int id) {
		int i = 0;
		while (!((i == countproviders) || (ProviderId[i] == id)))
			i++;
		if (i < countproviders)
			CountTypesByProviders[i]++;
	}

	// calculates maximum amount of VM types between providers
	private int getCountTypes() {
		int maxTypes = 0;
		for (int i = 0; i < countproviders; i++)
			if (maxTypes < CountTypesByProviders[i])
				maxTypes = CountTypesByProviders[i];
		return maxTypes;
	}
}
