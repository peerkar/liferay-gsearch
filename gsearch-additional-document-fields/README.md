# Liferay Additional Document Fields

Adds following fields into index:

* gsearch_version_count: number of versions
* gsearch_content_length: length of content field

Using function score query these fields can be used to boost relevant results.

## Prerequisites

The Liferay GSearch custom Elasticsearch adapter.

## Configuration

After the module has been deployed succesfully, please see the configuration options in Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Additional Index Fields

This feature is disabled by default. 

