package io.avature.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateJob {
    private String title;
    private String description;
    private String location;
    private String company;

    public CreateJob() {
    }

    public CreateJob(String title, String description, String location, String company) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.company = company;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public String getLocation() {
        return location;
    }

    @JsonProperty
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public String getCompany() {
        return company;
    }

    @JsonProperty
    public void setCompany(String company) {
        this.company = company;
    }
}
