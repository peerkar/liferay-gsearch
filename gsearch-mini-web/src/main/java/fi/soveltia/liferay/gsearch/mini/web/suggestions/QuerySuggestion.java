package fi.soveltia.liferay.gsearch.mini.web.suggestions;

public final class QuerySuggestion {
    private final String value;
    private final QuerySuggestionData data;

    private QuerySuggestion(Builder builder) {
        value = builder.value;
        data = builder.data;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String value;
        private QuerySuggestionData data;

        private Builder() {
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder data(QuerySuggestionData data) {
            this.data = data;
            return this;
        }

        public QuerySuggestion build() {
            return new QuerySuggestion(this);
        }
    }
}
