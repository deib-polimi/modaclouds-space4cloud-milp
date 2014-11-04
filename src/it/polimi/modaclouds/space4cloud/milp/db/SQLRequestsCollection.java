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

//this class contains requests to the SQL database
public final class SQLRequestsCollection {

    // receives amount of providers
    public final static String CountProvidersRequest = "SELECT count(*) FROM cloudprovider;";
//    // receives amount of VM types
//    public final String CountTypesRequest = "SELECT count(*) FROM cost,cloudresource_cost,cloudresource_allocation,virtualhwresource,iaas_service,iaas_service_composedof WHERE cloudresource_cost.Cost_id=cost.id AND cost.unit=\"per_hour\" AND cloudresource_allocation.VirtualHWResource_id=virtualhwresource.id AND virtualhwresource.type=\"cpu\" AND cloudresource_cost.CloudResource_id=cloudresource_allocation.CloudResource_id AND cloudresource_allocation.CloudResource_id=iaas_service_composedof.CloudResource_id AND iaas_service_composedof.IaaS_id=iaas_service.id;";
//    // receives all parameter of VM types (without memory size)
//    public final String ProcessorRequest = "SELECT iaas_service.name,cloudprovider.name,iaas_service.CloudProvider_id,virtualhwresource.processingRate,virtualhwresource.numberOfReplicas,cost.value,cloudresource.name FROM cloudprovider,cloudresource,cost,cloudresource_cost,cloudresource_allocation,virtualhwresource,iaas_service,iaas_service_composedof WHERE cloudprovider.id=iaas_service.CloudProvider_id AND cloudresource_cost.Cost_id=cost.id AND cost.unit=\"per_hour\" AND cloudresource_allocation.VirtualHWResource_id=virtualhwresource.id AND virtualhwresource.type=\"cpu\" AND cloudresource_cost.CloudResource_id=cloudresource_allocation.CloudResource_id AND cloudresource.id=cloudresource_allocation.CloudResource_id AND cloudresource_allocation.CloudResource_id=iaas_service_composedof.CloudResource_id AND iaas_service_composedof.IaaS_id=iaas_service.id;";
//    // receives memory parameters for VM types
//    public final String MemoryRequest = "SELECT virtualhwresource.size FROM cost,cloudresource_cost,cloudresource_allocation,virtualhwresource,iaas_service,iaas_service_composedof WHERE cloudresource_cost.Cost_id=cost.id AND cost.unit=\"per_hour\" AND cloudresource_allocation.VirtualHWResource_id=virtualhwresource.id AND virtualhwresource.type=\"memory\" AND cloudresource_cost.CloudResource_id=cloudresource_allocation.CloudResource_id AND cloudresource_allocation.CloudResource_id=iaas_service_composedof.CloudResource_id AND iaas_service_composedof.IaaS_id=iaas_service.id;";
    // receives list of providers
    public final static String ProviderRequest = "SELECT * FROM cloudprovider;";
    
    
    // receives amount of VM types given the providers
    public final static String CountTypesRequest =
    		"SELECT count(*) " +
    		"FROM cloudprovider, cost,cloudresource_cost,cloudresource_allocation,virtualhwresource,iaas_service,iaas_service_composedof " +
    		"WHERE cloudresource_cost.Cost_id=cost.id AND cost.unit='per_hour' AND cloudresource_allocation.VirtualHWResource_id=virtualhwresource.id AND virtualhwresource.type='cpu' AND cloudresource_cost.CloudResource_id=cloudresource_allocation.CloudResource_id AND cloudresource_allocation.CloudResource_id=iaas_service_composedof.CloudResource_id AND iaas_service_composedof.IaaS_id=iaas_service.id AND cost.description NOT LIKE 'Reserved%%' AND cloudprovider.id = iaas_service.cloudprovider_id%s%s;";
    
    // receives all parameter of VM types (without memory size) given the providers
    public final static String ProcessorRequest =
    		"SELECT iaas_service.name,cloudprovider.name,iaas_service.CloudProvider_id,virtualhwresource.processingRate,virtualhwresource.numberOfReplicas,cost.value,cloudresource.name, cost.region " +
    		"FROM cloudprovider,cloudresource,cost,cloudresource_cost,cloudresource_allocation,virtualhwresource,iaas_service,iaas_service_composedof " +
    		"WHERE cloudprovider.id=iaas_service.CloudProvider_id AND cloudresource_cost.Cost_id=cost.id AND cost.unit='per_hour' AND cloudresource_allocation.VirtualHWResource_id=virtualhwresource.id AND virtualhwresource.type='cpu' AND cloudresource_cost.CloudResource_id=cloudresource_allocation.CloudResource_id AND cloudresource.id=cloudresource_allocation.CloudResource_id AND cloudresource_allocation.CloudResource_id=iaas_service_composedof.CloudResource_id AND iaas_service_composedof.IaaS_id=iaas_service.id AND cost.description NOT LIKE 'Reserved%%'%s%s;";
    
    public final static String ProcessorRequestNoRegion =
    		"SELECT iaas_service.name,cloudprovider.name,iaas_service.CloudProvider_id,virtualhwresource.processingRate,virtualhwresource.numberOfReplicas,cost.value,cloudresource.name, '' " +
    		"FROM cloudprovider,cloudresource,cost,cloudresource_cost,cloudresource_allocation,virtualhwresource,iaas_service,iaas_service_composedof " +
    		"WHERE cloudprovider.id=iaas_service.CloudProvider_id AND cloudresource_cost.Cost_id=cost.id AND cost.unit='per_hour' AND cloudresource_allocation.VirtualHWResource_id=virtualhwresource.id AND virtualhwresource.type='cpu' AND cloudresource_cost.CloudResource_id=cloudresource_allocation.CloudResource_id AND cloudresource.id=cloudresource_allocation.CloudResource_id AND cloudresource_allocation.CloudResource_id=iaas_service_composedof.CloudResource_id AND iaas_service_composedof.IaaS_id=iaas_service.id AND cost.description NOT LIKE 'Reserved%%'%s;";
    
    
    // receives memory parameters for VM types given the providers
    public final static String MemoryRequest =
    		"SELECT virtualhwresource.size " + 
    		"FROM cost,cloudresource_cost,cloudresource_allocation,virtualhwresource,iaas_service,iaas_service_composedof, cloudprovider " +
    		"WHERE cloudresource_cost.Cost_id=cost.id AND cost.unit='per_hour' AND cloudresource_allocation.VirtualHWResource_id=virtualhwresource.id AND virtualhwresource.type='memory' AND cloudresource_cost.CloudResource_id=cloudresource_allocation.CloudResource_id AND cloudresource_allocation.CloudResource_id=iaas_service_composedof.CloudResource_id AND iaas_service_composedof.IaaS_id=iaas_service.id AND cloudprovider.id = iaas_service.cloudprovider_id AND cost.description NOT LIKE 'Reserved%%'%s%s;";
    
    public final static String AvaliabilityRequest =
    		"SELECT id, name, 0.95 as value FROM cloudprovider WHERE true%s;";

}
