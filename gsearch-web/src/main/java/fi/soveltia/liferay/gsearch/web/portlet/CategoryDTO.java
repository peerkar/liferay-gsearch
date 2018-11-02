package fi.soveltia.liferay.gsearch.web.portlet;

import java.util.List;

public final class CategoryDTO {

    private final String name;
    private final long categoryId;

    private CategoryDTO(Builder builder) {
        name = builder.name;
        categoryId = builder.categoryId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public static final class Builder {
        private String name;
        private long categoryId;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder categoryId(long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public CategoryDTO build() {
            return new CategoryDTO(this);
        }
    }
}
