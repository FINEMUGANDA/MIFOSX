/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.jobs.api;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.SearchParameters;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.jobs.data.JobDetailData;
import org.mifosplatform.infrastructure.jobs.data.JobDetailHistoryData;
import org.mifosplatform.infrastructure.jobs.service.JobRegisterService;
import org.mifosplatform.infrastructure.jobs.service.SchedulerJobRunnerReadService;
import org.mifosplatform.infrastructure.security.exception.NoAuthorizationException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Path("/jobs")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Component
public class SchedulerJobApiResource {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerJobApiResource.class);

    private final SchedulerJobRunnerReadService schedulerJobRunnerReadService;
    private final JobRegisterService jobRegisterService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<JobDetailData> toApiJsonSerializer;
    private final ToApiJsonSerializer<JobDetailHistoryData> jobHistoryToApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PlatformSecurityContext context;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Autowired
    public SchedulerJobApiResource(final SchedulerJobRunnerReadService schedulerJobRunnerReadService,
            final JobRegisterService jobRegisterService, final ToApiJsonSerializer<JobDetailData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final ToApiJsonSerializer<JobDetailHistoryData> jobHistoryToApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final PlatformSecurityContext context) {
        this.schedulerJobRunnerReadService = schedulerJobRunnerReadService;
        this.jobRegisterService = jobRegisterService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.jobHistoryToApiJsonSerializer = jobHistoryToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.context = context;
    }

    @GET
    public String retrieveAll(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(SchedulerJobApiConstants.SCHEDULER_RESOURCE_NAME);
        final List<JobDetailData> jobDetailDatas = this.schedulerJobRunnerReadService.findAllJobDeatils();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, jobDetailDatas, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}")
    public String retrieveOne(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(SchedulerJobApiConstants.SCHEDULER_RESOURCE_NAME);
        final JobDetailData jobDetailData = this.schedulerJobRunnerReadService.retrieveOne(jobId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, jobDetailData, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}/" + SchedulerJobApiConstants.JOB_RUN_HISTORY)
    public String retrieveHistory(@Context final UriInfo uriInfo, @PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId,
            @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder) {
        this.context.authenticatedUser().validateHasReadPermission(SchedulerJobApiConstants.SCHEDULER_RESOURCE_NAME);
        final SearchParameters searchParameters = SearchParameters.forPagination(offset, limit, orderBy, sortOrder);
        final Page<JobDetailHistoryData> jobhistoryDetailData = this.schedulerJobRunnerReadService.retrieveJobHistory(jobId,
                searchParameters);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.jobHistoryToApiJsonSerializer.serialize(settings, jobhistoryDetailData,
                SchedulerJobApiConstants.JOB_HISTORY_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}")
    public Response executeJob(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId,
            @QueryParam(SchedulerJobApiConstants.COMMAND) final String commandParam) {
        // check the logged in user have permissions to execute scheduler jobs
        final boolean hasNotPermission = this.context.authenticatedUser().hasNotPermissionForAnyOf("ALL_FUNCTIONS", "EXECUTEJOB_SCHEDULER");
        if (hasNotPermission) {
            final String authorizationMessage = "User has no authority to execute scheduler jobs";
            throw new NoAuthorizationException(authorizationMessage);
        }
        Response response = Response.status(400).build();
        if (is(commandParam, SchedulerJobApiConstants.COMMAND_EXECUTE_JOB)) {
            this.jobRegisterService.executeJob(jobId);
            response = Response.status(202).build();
        } else {
            throw new UnrecognizedQueryParamException(SchedulerJobApiConstants.COMMAND, commandParam);
        }
        return response;
    }

    @POST
    @Path("batch")
    public Response executeJobs(@QueryParam(SchedulerJobApiConstants.JOB_IDS) final List<Long> jobIds,
                               @QueryParam(SchedulerJobApiConstants.COMMAND) final String commandParam) {
        // check the logged in user have permissions to execute scheduler jobs
        final boolean hasNotPermission = this.context.authenticatedUser().hasNotPermissionForAnyOf("ALL_FUNCTIONS", "EXECUTEJOB_SCHEDULER");
        if (hasNotPermission) {
            final String authorizationMessage = "User has no authority to execute scheduler jobs";
            throw new NoAuthorizationException(authorizationMessage);
        }
        Response response = Response.status(400).build();
        if (is(commandParam, SchedulerJobApiConstants.COMMAND_EXECUTE_JOB)) {
            final String dataSourceContext = ThreadLocalContextUtil.getDataSourceContext();
            final String authToken = ThreadLocalContextUtil.getAuthToken();
            final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    ThreadLocalContextUtil.setDataSourceContext(dataSourceContext);
                    ThreadLocalContextUtil.setAuthToken(authToken);
                    ThreadLocalContextUtil.setTenant(tenant);
                    for(Long jobId : jobIds) {
                        try {
                            logger.info("Job batch execution ID: {}", jobId);
                            jobRegisterService.executeJob(jobId);
                        } catch (Exception e) {
                            logger.warn(e.toString(), e);
                        }
                    }
                }
            });
            response = Response.status(202).build();
        } else {
            throw new UnrecognizedQueryParamException(SchedulerJobApiConstants.COMMAND, commandParam);
        }
        return response;
    }

    @PUT
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}")
    public String updateJobDetail(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId, final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateJobDetail(jobId) //
                .withJson(jsonRequestBody) //
                .build(); //
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        if (result.getChanges() != null
                && (result.getChanges().containsKey(SchedulerJobApiConstants.jobActiveStatusParamName) || result.getChanges().containsKey(
                        SchedulerJobApiConstants.cronExpressionParamName))) {
            this.jobRegisterService.rescheduleJob(jobId);
        }
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}