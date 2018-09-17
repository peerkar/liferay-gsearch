package fi.soveltia.liferay.gsearch.core.impl.params;

public class QueryType {

    private final String entryClassName;
    private final String ddmStructureKey;

    private QueryType(Builder builder) {
        entryClassName = builder.entryClassName;
        ddmStructureKey = builder.ddmStructureKey;
    }

    public String getEntryClassName() {
        return entryClassName;
    }

    public String getDdmStructureKey() {
        return ddmStructureKey;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String entryClassName;
        private String ddmStructureKey;

        private Builder() {
        }

        public Builder entryClassName(String entryClassName) {
            this.entryClassName = entryClassName;
            return this;
        }

        public Builder ddmStructureKey(String ddmStructureKey) {
            this.ddmStructureKey = ddmStructureKey;
            return this;
        }

        public QueryType build() {
            return new QueryType(this);
        }
    }
}
