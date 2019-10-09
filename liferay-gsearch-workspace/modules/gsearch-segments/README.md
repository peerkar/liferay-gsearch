# Liferay GSearch Segments

Provides a Segments clause condition handler and a query context contributor.

## Clause Condition Handler

Compares user segment attributes to the given match value(s). See  `fi.soveltia.liferay.gsearch.segments.constants.SegmentsConfigurationVariables` for possible match properties.

How do you find segment Ids? The easiest way is to open the desired segment for editing. You can see the id on the address bar:

```
http://localhost:8080/group/guest/~/control_panel/manage/-/segments/entry/49810
```

Example clause configuration using the `segments` handler. If the user belongs to the user segment 123456, contents having a tag *finland* are boosted:

```
{ 
   "description":"An example of using the segments condition.",
   "enabled":true,
   "conditions":[ 
      { 
         "handler_name":"segments",
         "occur":"must",
         "configuration":{ 
            "match_property":"user_segment_id",
            "match_type":"any",
            "match_values":[ 
               123456
            ]
         }
      }
   ],
   "clauses":[ 
      { 
         "query_type":"term",
         "occur":"should",
         "query_configuration":{ 
            "boost":"100",
            "field_name":"assetTagNames.raw",
            "query":"finland"
         }
      }
   ]
}
```

## Segments Configuration Variables

See `fi.soveltia.liferay.gsearch.segments.constants.SegmentsConfigurationVariables` for available variables.

Example configuration using the `$_segments.ids_$` variable:


```
{
  "description": "An example.",
  "enabled": true,
  "conditions": [],
  "clauses": [
    {
      "query_type": "match",
      "occur": "should",
      "configuration": {
        "boost": "10",
        "field_name": "some_field_containing_segment_ids",
        "query": "$_segments.ids_$"
      }
    }
  ]
}
```

## Configuration

After the module has been deployed succesfully, please see the configuration options in `Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Segments`.

This feature is disabled by default.

## Changelog

(Major changes only)

### 2019-10-01 (Version 1.0.0)

* Initial version
* Possibility to automatically boost targeted contents has been removed because since Liferay 7.2 invidual contents cannot directly be targeted to user segments.