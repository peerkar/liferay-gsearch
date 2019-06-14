# Liferay GSearch More Like This

Liferay GSearch portlet to show contents similar to a content shown on Asset Publisher. It's also possible to manually define a content for which to base the similarity on.

## Requirements

The provided, custom Elasticsearch adapter.

## Configuration

This portlet has per instance configuration and you can access that from portlet's option menu. 

There are two clause configurations: 

* One for resolving the UID for the document to compare similarity against
* The actual recommendations query

The default configuration for resolving the UID:

```
	[
		{
			"description": "Find the UID for index document matching entryClassPK. You can use any clause or an array of clauses to resolve the UID",
			"conditions": [],
			"clauses": [
				{
					"query_type": "term",
					"occur": "must",
					"query_configuration": {
						"field_name": "entryClassPK",
						"query": "$_keywords_$"
					}
				}
			]
		}
	]
```

This configuration tries to resolve the asset entry id of a content shown on Asset Publisher and an entryClassPK for that. You can also set there a fixed entryClassPK or any other criteria to manually set a content to measure similarity against.

The recommendation clauses are configured like the general GSearch clause configuration. You can stack there conditions and clauses. 

Default configuration is provided but you have to save it once.

## Changelog

### 2019-06-13 (Version 3.0)

* Recommendations logic moved to a separate *gsearch-recommender* module.

