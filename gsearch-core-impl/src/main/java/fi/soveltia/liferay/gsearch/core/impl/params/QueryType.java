package fi.soveltia.liferay.gsearch.core.impl.params;

class QueryType {

    private final String entryClassName;
    private final String ddmStructureKey;

    private QueryType(Builder builder) {
        entryClassName = builder.entryClassName;
        ddmStructureKey = builder.ddmStructureKey;
    }

    String getEntryClassName() {
        return entryClassName;
    }

    String getDDMStructureKey() {
        return ddmStructureKey;
    }

    static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String entryClassName;
        private String ddmStructureKey;

        private Builder() {
        }

        Builder entryClassName(String entryClassName) {
            this.entryClassName = entryClassName;
            return this;
        }

        Builder ddmStructureKey(String ddmStructureKey) {
            this.ddmStructureKey = ddmStructureKey;
            return this;
        }

        QueryType build() {
            return new QueryType(this);
        }
    }
}
