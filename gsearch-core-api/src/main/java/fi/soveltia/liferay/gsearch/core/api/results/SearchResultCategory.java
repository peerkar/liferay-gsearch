package fi.soveltia.liferay.gsearch.core.api.results;

public final class SearchResultCategory {
    private final String name;
    private final String colorCode;

    private SearchResultCategory(Builder builder) {
        name = builder.name;
        colorCode = builder.colorCode;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getColorCode() {
        return colorCode;
    }


    public static final class Builder {
        private String name;
        private String colorCode;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder colorCode(String colorCode) {
            this.colorCode = colorCode;
            return this;
        }

        public SearchResultCategory build() {
            return new SearchResultCategory(this);
        }
    }
}
