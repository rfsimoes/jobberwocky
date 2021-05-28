package io.avature.core;

public class ExternalResource {
    private final String searchUrl;
    private final String responseParser;

    public ExternalResource(String searchUrl, String responseParser) {
        this.searchUrl = searchUrl;
        this.responseParser = responseParser;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public String getResponseParser() {
        return responseParser;
    }
}
