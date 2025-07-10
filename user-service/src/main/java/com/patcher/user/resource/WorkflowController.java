package com.drm.rcsa.controller;

import java.util.List;
import java.util.Map;

import com.workflow.engine.dto.ActionOptionDto;
import com.workflow.engine.dto.WorkflowStatusDto;
import com.workflow.engine.service.WorkflowRuntimeService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/rcsa/workflow")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WorkflowController {

	@Inject
	WorkflowRuntimeService workflowRuntimeService;

	  /**
     * Start a new workflow
     */
    @POST
    @Path("/start")
    public Response startWorkflow(Map<String, String> request) {
        String workflowCode = request.get("workflowCode");
        String role = request.get("role");
        String initiatedBy = request.get("initiatedBy");
        String comments = request.getOrDefault("comments", "");

        if (workflowCode == null || role == null || initiatedBy == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "workflowCode, role, and initiatedBy are required"))
                    .build();
        }

        Long instanceId = workflowRuntimeService.startWorkflow(workflowCode, role, initiatedBy, comments);

        return Response.ok(Map.of(
                "workflowInstanceId", instanceId,
                "status", "Workflow initiated successfully"
        )).build();
    }

    /**
     * Progress workflow to the next stage based on actionId
     */
    @POST
    @Path("/progress")
    public Response progressWorkflow(Map<String, String> request) {
        String role = request.get("role");
        String actedBy = request.get("actedBy");
        String comments = request.getOrDefault("comments", "");
        Long instanceId;
        Long actionId;

        try {
            instanceId = Long.valueOf(request.get("workflowInstanceId"));
            actionId = Long.valueOf(request.get("actionId"));
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "workflowInstanceId and actionId must be valid numbers"))
                    .build();
        }

        if (role == null || actedBy == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "role and actedBy are required"))
                    .build();
        }

        workflowRuntimeService.progressWorkflow(instanceId, role, actedBy, actionId, comments);

        return Response.ok(Map.of(
                "workflowInstanceId", instanceId,
                "status", "Workflow progressed successfully"
        )).build();
    }

    /**
     * Get current workflow status
     */
    @GET
    @Path("/status/{workflowInstanceId}")
    public Response getWorkflowStatus(@PathParam("workflowInstanceId") Long instanceId) {
        WorkflowStatusDto status = workflowRuntimeService.getWorkflowStatus(instanceId);
        if (status == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Workflow instance not found"))
                    .build();
        }
        return Response.ok(status).build();
    }

    /**
     * Get available actions for a role in the current stage
     */
    @GET
    @Path("/actions")
    public Response getAvailableActions(@QueryParam("workflowInstanceId") Long instanceId,
                                        @QueryParam("role") String role) {
        if (instanceId == null || role == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "workflowInstanceId and role are required"))
                    .build();
        }

        List<ActionOptionDto> actions = workflowRuntimeService.getAvailableActions(instanceId, role);

        return Response.ok(actions).build();
    }
}
