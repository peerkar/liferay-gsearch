package fi.soveltia.liferay.gsearch.mini.web.suggestions;

public final class QuerySuggestionData {
    private final String type;

    private QuerySuggestionData(Builder builder) {
        type = builder.type;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String type;

        private Builder() {
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public QuerySuggestionData build() {
            return new QuerySuggestionData(this);
        }
    }
}
