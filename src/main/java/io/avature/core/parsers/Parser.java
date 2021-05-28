package io.avature.core.parsers;

import io.avature.core.services.JobDTO;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.List;

public interface Parser {
    List<JobDTO> parse(HttpEntity response) throws IOException;
}
