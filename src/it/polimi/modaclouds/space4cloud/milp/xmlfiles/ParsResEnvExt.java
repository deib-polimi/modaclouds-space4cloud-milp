package it.polimi.modaclouds.space4cloud.milp.xmlfiles;

import it.polimi.modaclouds.qos_models.schema.CloudService;
import it.polimi.modaclouds.qos_models.schema.Replica;
import it.polimi.modaclouds.qos_models.schema.ReplicaElement;
import it.polimi.modaclouds.qos_models.schema.ResourceContainer;
import it.polimi.modaclouds.qos_models.schema.ResourceModelExtension;
import it.polimi.modaclouds.qos_models.util.XMLHelper;
import it.polimi.modaclouds.space4cloud.milp.types.SqlBaseParsMatrix;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.SolutionList;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * 
 * Parses the Resourse Environment Extension file.
 *
 */
public class ParsResEnvExt {
	
	public SolutionList solution = null;
	
	public ParsResEnvExt(String file, SqlBaseParsMatrix resMatrix, RepositoryList CRList) {
		
		if (file != null && file.length() > 0 && new File(file).exists())
			init(file, resMatrix, CRList);
		
	}

	private void init(String file, SqlBaseParsMatrix resMatrix, RepositoryList CRList) {
		solution = new SolutionList(resMatrix.Provider.length, CRList.ContainerList.Id.length);
		
		try {
			ResourceModelExtension rme = XMLHelper.deserialize(Paths.get(file).toUri().toURL(),ResourceModelExtension.class);
			
			int value = 0, valuew = 0;
			
			for (ResourceContainer rc : rme.getResourceContainer()) {
				String provider = rc.getProvider();
				String tierId = rc.getId();
				
				CloudService resource = rc.getCloudElement();
//				String serviceType = resource.getServiceType();
//				String serviceName = resource.getServiceName();
				String resourceName = resource.getResourceSizeID();
				
//				Location location = resource.getLocation();
//				String region = null;
//				if (location != null)
//					region = location.getRegion();
				
				int iProvider = 1;
				int iTier = 1;
				int iResource = 1;
				
				for (int w = 0; w < resMatrix.Provider.length; ++w) {
					if (resMatrix.Provider[w][0].equals(provider)) {
						iProvider = w + 1;
						w = resMatrix.Provider.length;
					}
				}
				
				for (int w = 0; w < CRList.ContainerList.Id.length; ++w) {
					if (CRList.ContainerList.Id[w].equals(tierId)) {
						iTier = w + 1;
						w = CRList.ContainerList.Id.length;
					}
				}
				
				for (int w = 0; w < resMatrix.TypeName[iProvider - 1].length; ++w) {
					if (resMatrix.TypeName[iProvider - 1][w].equals(resourceName)) {
						iResource = w + 1;
						w = resMatrix.TypeName[iProvider - 1].length;
					}
				}
				
				Replica replicas = resource.getReplicas();
				
				if (replicas != null) {
					for (ReplicaElement replica : replicas.getReplicaElement()) {
						int hour = replica.getHour();
						int allocation = replica.getValue();
						
						solution.amounts[value].hour = hour + 1;
						solution.amounts[value].allocation = allocation;
						solution.amounts[value].provider = iProvider;
						solution.amounts[value].tier = iTier;
						solution.amounts[value++].resource = iResource;
					}
				}
				
				solution.xs[iProvider - 1].provider = iProvider;
				solution.xs[iProvider - 1].taken = 1;
				
				solution.ws[valuew].provider = iProvider;
				solution.ws[valuew].tier = iTier;
				solution.ws[valuew].resource = iResource;
				solution.ws[valuew++].taken = 1;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			solution = null;
		}
		
	}
	
	public static ArrayList<String> getProviders(File f) {
		ArrayList<String> res = new ArrayList<String>();
		
		if (f != null && f.exists()) {
			
			try {
				ResourceModelExtension rme = XMLHelper.deserialize(f.toURI().toURL(),ResourceModelExtension.class);
				
				for (ResourceContainer rc : rme.getResourceContainer()) {
					String provider = rc.getProvider();
					
					if (provider != null && !res.contains(provider))
						res.add(provider);
				}
			} catch (Exception e) {
				res = new ArrayList<String>();
			}
		}
		
		return res;
	}
}
