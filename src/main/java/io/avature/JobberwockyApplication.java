package io.avature;

import io.avature.core.ExternalResource;
import io.avature.core.services.JobSearchServiceImpl;
import io.avature.db.daos.JobsDAO;
import io.avature.db.models.Job;
import io.avature.resources.JobsResource;
import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;

public class JobberwockyApplication extends Application<JobberwockyConfiguration> {

    private final HibernateBundle<JobberwockyConfiguration>
            hibernate = new HibernateBundle<JobberwockyConfiguration>(Job.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(JobberwockyConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new JobberwockyApplication().run(args);
    }

    @Override
    public String getName() {
        return "Jobberwocky";
    }

    @Override
    public void initialize(final Bootstrap<JobberwockyConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final JobberwockyConfiguration configuration,
                    final Environment environment) {
        // TODO create tests for this application
        JobsDAO jobsDAO = new JobsDAO(hibernate.getSessionFactory());
        final HttpClient
                httpClient =
                new HttpClientBuilder(environment).using(configuration.getHttpClientConfiguration())
                                                  .using(new StandardHttpRequestRetryHandler())
                                                  .using(new DefaultServiceUnavailableRetryStrategy())
                                                  .build(getName());
        JobSearchServiceImpl jobSearchService = new JobSearchServiceImpl(jobsDAO, httpClient);
        //TODO move this to config file if possible.
        jobSearchService.addExternalResource(new ExternalResource("https://jobs.github.com/positions.json?search=%s",
                                                                  "io.avature.core.parsers.GithubParser"));
        JobsResource jobPostingResource = new JobsResource(jobsDAO, jobSearchService);
        environment.jersey().register(jobPostingResource);
    }

}
