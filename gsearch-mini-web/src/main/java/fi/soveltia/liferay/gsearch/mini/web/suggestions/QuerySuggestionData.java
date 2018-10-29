package fi.soveltia.liferay.gsearch.mini.web.suggestions;

public final class QuerySuggestionData {
    private final String type;
    private final String url;
    private final String description;

    private QuerySuggestionData(Builder builder) {
        type = builder.type;
        url = builder.url;
        description = builder.description;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }


    public static final class Builder {
        private String type;
        private String url;
        private String description;

        private Builder() {
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public QuerySuggestionData build() {
            return new QuerySuggestionData(this);
        }
    }
}
