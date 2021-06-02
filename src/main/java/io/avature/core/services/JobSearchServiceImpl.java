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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobSearchServiceImpl implements JobSearchService {
    private final JobsDAO jobsDAO;
    private HttpClient httpClient;
    private List<ExternalResource> externalResources;
    Logger logger = LoggerFactory.getLogger(JobSearchServiceImpl.class);
    private ExecutorService executor;

    @PostConstruct
    public void onCreate() {
        this.executor = Executors.newFixedThreadPool(10);
    }

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
            String uri = String.format(e.getSearchUrl(), search);
            //TODO make it multithreaded
            //TODO pagination?
            CompletableFuture<HttpResponse>
                    httpResponseCompletableFuture =
                    CompletableFuture.supplyAsync(() -> callExternalResource(uri));
            httpResponseCompletableFuture.thenAccept(response -> handleResponse(search, jobs, e, response));
        }
        return jobs;
    }

    private void handleResponse(String search, List<JobDTO> jobs, ExternalResource e, HttpResponse response) {
        if (response == null) {} else if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            logger.error("{}:{}", String.format(e.getSearchUrl(), search), response);
        } else {
            HttpEntity entity = response.getEntity();
            Parser parser;
            try {
                parser = (Parser) Class.forName(e.getResponseParser()).getConstructor().newInstance();
                jobs.addAll(parser.parse(entity));
            } catch (InstantiationException | IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException exception) {
                logger.error(exception.toString());
            }

        }
    }

    private HttpResponse callExternalResource(String uri) {
        try {
            return httpClient.execute(new HttpGet(uri));
        } catch (IOException ioException) {
            logger.error(ioException.toString());
        }
        return null;
    }
}
