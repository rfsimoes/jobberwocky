package io.avature.db.daos;

import io.avature.api.CreateJob;
import io.avature.db.models.Job;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class JobsDAO extends AbstractDAO<Job> {
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public JobsDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public Job create(CreateJob job) {
        return persist(new Job(job.getTitle(), job.getDescription(), job.getLocation(), job.getCompany()));
    }

    public List<Job> findJob(String keyword) {
        Query<Job> query = query(
                "SELECT j FROM Job j WHERE j.title LIKE ?1 OR j.description LIKE ?1 OR j.location LIKE ?1");
        query.setParameter(1, "%" + keyword + "%");
        return list(query);
    }
}
