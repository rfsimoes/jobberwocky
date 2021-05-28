package io.avature.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.avature.core.services.JobDTO;

import java.util.List;

public class SearchJobsResponse {
    private List<JobDTO> jobs;

    public SearchJobsResponse() {
    }

    public SearchJobsResponse(List<JobDTO> jobs) {
        this.jobs = jobs;
    }


    @JsonProperty
    public List<JobDTO> getJobs() {
        return jobs;
    }

    @JsonProperty
    public void setJobs(List<JobDTO> jobs) {
        this.jobs = jobs;
    }
}
