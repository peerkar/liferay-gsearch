package fi.soveltia.liferay.gsearch.mini.web.suggestions;

import java.util.List;
import java.util.Map;

public final class QuerySuggestionsResponse {
    private final List<QuerySuggestion> suggestions;
    private final Map<String, QuerySuggestionGroupData> groups;

    private QuerySuggestionsResponse(Builder builder) {
        suggestions = builder.suggestions;
        groups = builder.groups;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public List<QuerySuggestion> getSuggestions() {
        return suggestions;
    }

    public Map<String, QuerySuggestionGroupData> getGroups() {
        return groups;
    }


    public static final class Builder {
        private List<QuerySuggestion> suggestions;
        private Map<String, QuerySuggestionGroupData> groups;

        private Builder() {
        }

        public Builder suggestions(List<QuerySuggestion> suggestions) {
            this.suggestions = suggestions;
            return this;
        }

        public Builder groups(Map<String, QuerySuggestionGroupData> groups) {
            this.groups = groups;
            return this;
        }

        public QuerySuggestionsResponse build() {
            return new QuerySuggestionsResponse(this);
        }
    }
}
