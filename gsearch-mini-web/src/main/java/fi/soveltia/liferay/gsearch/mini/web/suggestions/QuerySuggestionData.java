package fi.soveltia.liferay.gsearch.mini.web.suggestions;

public final class QuerySuggestionData {
    private final String type;
    private final String typeKey;
    private final String icon;
    private final String url;
    private final String description;
    private final String date;

    private QuerySuggestionData(Builder builder) {
        type = builder.type;
        typeKey = builder.typeKey;
        icon = builder.icon;
        url = builder.url;
        description = builder.description;
        date = builder.date;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getType() {
        return type;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }


    public static final class Builder {
        private String type;
        private String typeKey;
        private String icon;
        private String url;
        private String description;
        private String date;

        private Builder() {
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder typeKey(String typeKey) {
            this.typeKey = typeKey;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
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

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public QuerySuggestionData build() {
            return new QuerySuggestionData(this);
        }
    }
}

