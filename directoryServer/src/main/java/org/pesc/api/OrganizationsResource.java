package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.Organization;
import org.pesc.api.repository.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 3/22/16.
 */
@Component
@WebService
@Path("/organizations")
@Api("/organizations")
public class OrganizationsResource {

    private static final Log log = LogFactory.getLog(OrganizationsResource.class);

    @Autowired
    private OrganizationService organizationService;


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Search organizations based on the optional search parameters.  All organizations are returned" +
            " when no search criteria are provided.")
    public List<Organization> findOrganization(
            @QueryParam("id") @ApiParam("The directory identifier for the organization.") Integer id,
            @QueryParam("organizationCode") @ApiParam("A code such as ATP code that identifies the organization.") String organizationCode,
            @QueryParam("organizationCodeType") @ApiParam("Indicates the type of organization code and should be one of the following: ACT, ATP, FICE, IPEDS.") String organizationCodeType,
            @QueryParam("name") @ApiParam("The case insensitive organization name or partial name.") String name,
            @QueryParam("subcode") @ApiParam("A proprietary code used to identify an organization.") String subcode,
            @QueryParam("type") @ApiParam("The type of organization (1 = Institution, 2 = Service Provider).") Integer type,
            @QueryParam("ein") @ApiParam("The federal tax identification number (Employer Identification Number).") String ein,
            @QueryParam("createdTime") Long createdTime,
            @QueryParam("modifiedTime") Long modifiedTime
    ) {

        return organizationService.search(
                id,
                organizationCode,
                organizationCodeType,
                name,
                subcode,
                type,
                ein,
                createdTime,
                modifiedTime);
    }

    @GET
    @Path("/{id}")
    @ApiOperation("Return the organization with the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Organization> getOrganization(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id) {
        ArrayList<Organization> results = new ArrayList<Organization>();

        Organization organization = organizationService.findById(id);

        if (organization != null) {
            results.add(organization);
        }

        return results;
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create an organization.")
    public Organization createOrganization(Organization org) {

        return organizationService.create(org);
    }

    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the organization with the given ID.")
    public Organization saveOrganization(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id, Organization org) {
        return organizationService.update(org);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the organization with the given ID.")
    public void removeOrganization(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id) {

        organizationService.delete(id);

    }

    @Path("/{id}")
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Add the the endpoint with the given ID to the organization's list of endpoints.")
    public void updateEndpoints(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id,
                                @QueryParam("endpoint_id") @ApiParam(value="The identifier for the endpoint.", required = true) Integer endpointID,
                                @QueryParam("operation") @ApiParam(value="The operation to perform. Must by case insensitive 'add' or 'remove'.", required = true) String operation) {

        checkParameter(endpointID, "endpoint_id");
        checkParameter(operation, "operation");

        if ("add".equalsIgnoreCase(operation)) {
            organizationService.addEndpointToOrganization(id, endpointID);
        }

        else if ("remove".equalsIgnoreCase(operation)) {
            organizationService.removeEndpointToOrganization(id, endpointID);
        }




    }

    void checkParameter(Object param, String parameterName) {
        if (param == null) {
            throw new WebApplicationException(String.format("The %s parameter is required.", parameterName));
        }
    }




}
