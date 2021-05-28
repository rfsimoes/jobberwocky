package io.avature.resources;

import io.avature.api.CreateJob;
import io.avature.api.SearchJobsResponse;
import io.avature.core.services.JobSearchServiceImpl;
import io.avature.db.daos.JobsDAO;
import io.avature.db.models.Job;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/jobs")
public class JobsResource {

    private final JobsDAO jobsDAO;
    private final JobSearchServiceImpl jobSearchService;

    public JobsResource(JobsDAO jobsDAO, JobSearchServiceImpl jobSearchService) {
        this.jobsDAO = jobsDAO;
        this.jobSearchService = jobSearchService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response createJob(CreateJob job) {
        Job job1 = this.jobsDAO.create(job);
        return Response.created(URI.create("jobs/" + job1.getId())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public SearchJobsResponse getJobs(@QueryParam("search") String search) {
        SearchJobsResponse searchJobsResponse = jobSearchService.searchJobs(search);
        return searchJobsResponse;
    }
}
