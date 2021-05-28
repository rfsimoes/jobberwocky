package io.avature.core.services;

import io.avature.api.SearchJobsResponse;

public interface JobSearchService {
    SearchJobsResponse searchJobs(String search);
}
