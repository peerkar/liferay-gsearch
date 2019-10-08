# Liferay Additional Document Fields

This module adds two fields to the indexed documents:

* `gsearch_numeric_version`:  a numeric version count (as the LR platform default is string type)
* `gsearch_content_length_[LANGUAGE_ID]`: content length, localized

These fields can be used in the clause configuration to improve relevancy as contents having more frequent updates and/or more length could be regarded as better maintained, and eventually, more relevant.

Consume the fields for example with function score queries. Examples (disabled by default) are included in the default GSearch clause configuration.

See the `GsearchIndexerPostProcessor` source code for which asset types are supported.

## Requirements

The provided, custom Liferay 7.2 Elasticsearch adapter. 

The custom field mapping have to be available before enabling the module. Re-index after enabling the module.

## Configuration

After the module has been deployed succesfully, enable it in `Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Additional Index Fields`.

This feature is disabled by default.

## Changelog

(Major changes only)

### 2019-10-01 (Version 2.0.0)

* Upgrade to Liferay 7.2
* Added content length fields