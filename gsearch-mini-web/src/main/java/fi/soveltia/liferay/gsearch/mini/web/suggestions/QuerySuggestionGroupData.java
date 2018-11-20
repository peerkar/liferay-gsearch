package fi.soveltia.liferay.gsearch.mini.web.suggestions;

import java.util.Map;

public final class QuerySuggestionGroupData {

    private final Map<String, Object> group;
    private int count;

    public Map<String, Object> getGroup() {
        return group;
    }

    public int getCount() {
        return count;
    }

    public void addOneCount() {
        this.count = this.count + 1;
    }

    private QuerySuggestionGroupData(Builder builder) {
        group = builder.group;
        count = builder.count;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private Map<String, Object> group;
        private int count = 1;

        private Builder() {
        }

        public Builder group(Map<String, Object> group) {
            this.group = group;
            return this;
        }

        public QuerySuggestionGroupData build() {
            return new QuerySuggestionGroupData(this);
        }
    }
}
