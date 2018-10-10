package fi.soveltia.liferay.gsearch.web.portlet;

import java.util.List;

public final class CategoryDTO {

    private final String name;
    private final List<CategoryDTO> children;

    private CategoryDTO(Builder builder) {
        name = builder.name;
        children = builder.children;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public List<CategoryDTO> getChildren() {
        return children;
    }


    public static final class Builder {
        private String name;
        private List<CategoryDTO> children;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder children(List<CategoryDTO> children) {
            this.children = children;
            return this;
        }

        public CategoryDTO build() {
            return new CategoryDTO(this);
        }
    }
}
