package fi.soveltia.liferay.gsearch.core.impl.params;

import java.util.ArrayList;
import java.util.List;

class QueryType {

    private final String entryClassName;
    private final List<String> ddmStructureKeys;

    private QueryType(Builder builder) {
        entryClassName = builder.entryClassName;
        ddmStructureKeys = builder.ddmStructureKeys;
    }

    String getEntryClassName() {
        return entryClassName;
    }

    List<String> getDDMStructureKeys() {
        return ddmStructureKeys;
    }

    static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String entryClassName;
        private List<String> ddmStructureKeys;

        private Builder() {
        }

        Builder entryClassName(String entryClassName) {
            this.entryClassName = entryClassName;
            return this;
        }

        Builder ddmStructureKeys(List<String> ddmStructureKeys) {
            if (ddmStructureKeys != null) {
                this.ddmStructureKeys = new ArrayList<>(ddmStructureKeys);
            }
            return this;
        }

        QueryType build() {
            return new QueryType(this);
        }
    }
}
