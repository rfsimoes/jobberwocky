package io.avature.core.services;

import io.avature.api.SearchJobsResponse;
import io.avature.core.ExternalResource;
import io.avature.core.parsers.Parser;
import io.avature.db.daos.JobsDAO;
import io.avature.db.models.Job;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class JobSearchServiceImpl implements JobSearchService {
    private final JobsDAO jobsDAO;
    private HttpClient httpClient;
    private List<ExternalResource> externalResources;
    Logger logger = LoggerFactory.getLogger(JobSearchServiceImpl.class);


    public JobSearchServiceImpl(JobsDAO jobsDAO, HttpClient httpClient) {
        this.jobsDAO = jobsDAO;
        this.httpClient = httpClient;
    }

    public void addExternalResource(ExternalResource externalResource) {
        if (this.externalResources == null) {
            this.externalResources = new ArrayList<>();
        }
        this.externalResources.add(externalResource);
    }

    @Override public SearchJobsResponse searchJobs(String search) {
        List<Job> job = this.jobsDAO.findJob(search);
        List<JobDTO> jobDTOS = searchExternalSources(search);

        job.stream().map(j -> new JobDTO(j.getTitle(), j.getDescription(), j.getLocation(), j.getCompany())).forEach(
                jobDTOS::add);

        return new SearchJobsResponse(jobDTOS);
    }

    private List<JobDTO> searchExternalSources(String search) {
        List<JobDTO> jobs = new ArrayList<>();
        for (ExternalResource e : externalResources) {
            try {
                //TODO make it multithreaded
                //TODO pagination?
                HttpResponse response = httpClient.execute(new HttpGet(String.format(e.getSearchUrl(), search)));
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    logger.error("{}:{}", String.format(e.getSearchUrl(), search), response);
                    continue;
                }
                HttpEntity entity = response.getEntity();
                Parser parser = (Parser) Class.forName(e.getResponseParser()).getConstructor().newInstance();
                jobs.addAll(parser.parse(entity));

            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ioException) {
                logger.error(ioException.toString());
            }
        }
        return jobs;
    }
}
