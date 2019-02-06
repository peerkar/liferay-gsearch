# Liferay GSearch More Like This

Liferay GSearch recommendation engine enabling to show contents similar to content shown on Asset Publisher or to a manually defined content.

## Prerequisites

None.

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

The recommendations query is configured like the general GSearc clause configuration. You can stack there conditions and clauses. The default configuration has a MLT query and also a sample boosting clause for results having a sample tag.
