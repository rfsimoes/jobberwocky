package io.avature.core.parsers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avature.core.services.JobDTO;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GithubParser implements Parser {

    @Override public List<JobDTO> parse(HttpEntity entity) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String string = EntityUtils.toString(entity);
        List<GithubJob> githubJobs = objectMapper.readValue(string, new TypeReference<List<GithubJob>>() {
        });
        return githubJobs.stream()
                         .map(githubJob -> new JobDTO(githubJob.getTitle(),
                                                      githubJob.getDescription(),
                                                      githubJob.getLocation(),
                                                      githubJob.getCompany()))
                         .collect(Collectors.toList());
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GithubJob {
        @JsonProperty("type")
        private String type;
        @JsonProperty("company")
        private String company;
        @JsonProperty("location")
        private String location;
        @JsonProperty("title")
        private String title;
        @JsonProperty("description")
        private String description;

        @JsonProperty("type")
        public String getType() {
            return type;
        }

        @JsonProperty("type")
        public void setType(String type) {
            this.type = type;
        }

        @JsonProperty("company")
        public String getCompany() {
            return company;
        }

        @JsonProperty("company")
        public void setCompany(String company) {
            this.company = company;
        }

        @JsonProperty("location")
        public String getLocation() {
            return location;
        }

        @JsonProperty("location")
        public void setLocation(String location) {
            this.location = location;
        }

        @JsonProperty("title")
        public String getTitle() {
            return title;
        }

        @JsonProperty("title")
        public void setTitle(String title) {
            this.title = title;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

    }
}
