# Liferay GSearch Audience Targeting

Provides two functionalities:

1. Boost segmented contents in search results
1. Use Audience Targeting user segments as clause conditions

With the first functionality you can boost documents falling into current user's user segments. Boost level can be adjusted in the configuration. 
Create test user segments, target contents to those segments and play with the boost to see, how these contents raise higher up in the results.

Second functionality is a query clause condition handler. Add your clause to Liferay GSearch clause configuration and define handler in the conditions: 

``` 
"handler_name": "audience_targeting_user_segments".
```

In this version you have to manually define the user_segment_ids. In future this might be name based.

How do you find segment Ids? When you open a user segment from Audience Targeting user segments management you can get the ID from address bar, for example:

```
http://localhost:8080/group/guest/~/control_panel/manage/-/audience_targeting/admin/user-segment/56944/details/summary
```

Where the user segment id is: 56944

Example configuration:


```
{
        "conditions": [
                {
                        "handler_name": "audience_targeting_user_segments",
                        "occur": "must",
                        "handler_parameters": {
	                        "user_segment_ids": [
	                                221212,
	                                221232
	                        ]
                        }
                }
        ],
        "clauses": [
                {
                        "query_type": "term",
                        "occur": "should",
                        "query_configuration": {
                                "boost": "10",
                                "field_name": "title",
                                "query": "boostme"
                        }
                }
        ]
}
```


## Prerequisites

Liferay Audience Targeting plugin installed.

## Configuration

After the module has been deployed succesfully, please see the configuration options in Control Panel -> Configuration -> System Settings -> Liferay GSearch -> GSearch Audience Targeting.


