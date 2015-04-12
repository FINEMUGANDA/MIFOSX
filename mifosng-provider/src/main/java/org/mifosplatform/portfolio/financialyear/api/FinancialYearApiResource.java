/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.api;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.financialyear.data.FinancialYearData;
import org.mifosplatform.portfolio.financialyear.service.FinancialYearReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/financial_years")
@Component
@Scope("singleton")
public class FinancialYearApiResource {

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "startYear", "endYear", "startDate", "endDate", "current", "closed"));

    private final String resourceNameForPermission = "FINANCIALYEAR";

    private final FinancialYearReadPlatformService financialYearReadPlatformService;
    private final DefaultToApiJsonSerializer<FinancialYearData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PlatformSecurityContext context;

    @Autowired
    public FinancialYearApiResource(final PlatformSecurityContext context, final FinancialYearReadPlatformService financialYearReadPlatformService,
                                    final DefaultToApiJsonSerializer<FinancialYearData> toApiJsonSerializer,
                                    final ApiRequestParameterHelper apiRequestParameterHelper,
                                    final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiJsonSerializerService = toApiJsonSerializer;
        this.financialYearReadPlatformService = financialYearReadPlatformService;
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveFinancialYears(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        final List<FinancialYearData> financialYearDatas = this.financialYearReadPlatformService.retrieveFinancialYears();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.apiJsonSerializerService.serialize(settings, financialYearDatas, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{financialYearId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveFinancialYearDetails(@Context final UriInfo uriInfo, @PathParam("financialYearId") final Long financialYearId) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        FinancialYearData financialYearData = this.financialYearReadPlatformService.retrieveFinancialYear(financialYearId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, financialYearData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String createFinancialYear(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createFinancialYear().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    @PUT
    @Path("{financialYearId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updateFinancialYear(@PathParam("financialYearId") final Long financialYearId,
                                      final String jsonRequestBody) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateFinancialYear(financialYearId).withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    @DELETE
    @Path("{financialYearId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteFinancialYear(@PathParam("financialYearId") final Long financialYearId) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteFinancialYear(financialYearId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }
}