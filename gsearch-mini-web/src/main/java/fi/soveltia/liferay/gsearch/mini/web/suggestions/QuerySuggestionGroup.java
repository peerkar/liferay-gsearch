package fi.soveltia.liferay.gsearch.mini.web.suggestions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum QuerySuggestionGroup {
    CONTENT(5, new String[] { "content", "news", "file" }),
    TOOL(3, new String[] { "tool" }),
    PERSON(3, new String[] { "person" });

    private int maxSuggestions;
    private String[] types;

    QuerySuggestionGroup(int maxSuggestions, String[] types) {
        this.maxSuggestions = maxSuggestions;
        this.types = types;
    }

    public int getMaxSuggestions() {
        return this.maxSuggestions;
    }

    // for gson serialize to get all fields as json (hack to overcome the fact that all enum fields not easily serialized)
    public Map<String, Object> getAsMap() {
        Map<String, Object> bag = new HashMap<>();
        bag.put("maxSuggestions", this.maxSuggestions);
        bag.put("types", this.types);
        return bag;
    }

    public static QuerySuggestionGroup getGroupForTypeKey(String typeKey) {
        for (QuerySuggestionGroup group : values()) {
            List<String> types = Arrays.asList(group.types);
            if (types.contains(typeKey)) {
                return group;
            }
        }
        throw new IllegalArgumentException(String.format("Cannot find suggestion group for type key '%s'", typeKey));
    }
}