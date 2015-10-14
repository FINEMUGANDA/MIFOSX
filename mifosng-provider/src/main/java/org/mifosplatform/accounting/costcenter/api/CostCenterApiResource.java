/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.api;

import org.mifosplatform.accounting.common.AccountingConstants;
import org.mifosplatform.accounting.common.AccountingDropdownReadPlatformService;
import org.mifosplatform.accounting.costcenter.data.CostCenterData;
import org.mifosplatform.accounting.costcenter.service.CostCenterReadPlatformService;
import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.accounting.glaccount.domain.GLAccountType;
import org.mifosplatform.accounting.glaccount.service.GLAccountReadPlatformService;
import org.mifosplatform.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.organisation.staff.service.StaffReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.*;

@Path("/costcenters")
@Component
@Scope("singleton")
public class CostCenterApiResource {

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("staff", "glAccounts", "staffOptions", "glAccountOptions"));

    private final String resourceNameForPermission = "COSTCENTER";

    private final GLAccountReadPlatformService glAccountReadPlatformService;
    private final DefaultToApiJsonSerializer<CostCenterData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final CostCenterReadPlatformService costCenterReadPlatformService;

    @Autowired
    public CostCenterApiResource(final PlatformSecurityContext context, final GLAccountReadPlatformService glAccountReadPlatformService,
                                 final DefaultToApiJsonSerializer<CostCenterData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
                                 final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                 final StaffReadPlatformService staffReadPlatformService,
                                 final CostCenterReadPlatformService costCenterReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiJsonSerializerService = toApiJsonSerializer;
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.costCenterReadPlatformService = costCenterReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveNewAccountDetails(@Context final UriInfo uriInfo, @QueryParam("type") final Integer type) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        CostCenterData costCenterData = this.costCenterReadPlatformService.retrieveNewCostCenterDetails();
        costCenterData = handleTemplate(costCenterData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, costCenterData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllAccounts(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        Collection<CostCenterData> costCenterDatas = this.costCenterReadPlatformService.retrieveAllCostCenters();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, costCenterDatas, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{staffId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveAccount(@PathParam("staffId") final Long staffId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        CostCenterData costCenterData = this.costCenterReadPlatformService.retrieveCostCenterDetails(staffId);

        if (settings.isTemplate()) {
            costCenterData = handleTemplate(costCenterData);
        }

        return this.apiJsonSerializerService.serialize(settings, costCenterData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createGLAccount(final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCostCenter().withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    @PUT
    @Path("{staffId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateGLAccount(@PathParam("staffId") final Long staffId, final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateCostCenter(staffId).withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    @DELETE
    @Path("{staffId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteGLAccount(@PathParam("staffId") final Long staffId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteCostCenter(staffId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    private CostCenterData handleTemplate(final CostCenterData costCenterData) {

        JournalEntryAssociationParametersData associationParametersData = new JournalEntryAssociationParametersData();
        final List<GLAccountData> glAccountOptions = this.glAccountReadPlatformService.retrieveAllGLAccounts(null, null, null, null, false, associationParametersData);
        final List<StaffData> staffOptions = (List<StaffData>) this.staffReadPlatformService.retrieveAllStaff(null, null, false, "active");

        return new CostCenterData(costCenterData, glAccountOptions, staffOptions);
    }

    private List<GLAccountData> defaultIfEmpty(final List<GLAccountData> list) {
        List<GLAccountData> returnList = null;
        if (list != null && !list.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }
}
