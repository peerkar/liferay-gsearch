# 72-DXP-SP2

This custom Elasticsearch adapter version has been tested with following setup:

* DXP 7.2 SP3 (FP8)
* Elasticsearch 6.8.6

## Requirements

A remote, standalone Elasticsearch instance. For testing purposes, you might get this running with the embedded engine, but there's no guarantee.

## Installation Instructions

1. Shutdown the portal
1. Download and copy the JARS in this folder to the deploy folder
1. Download and copy the `gsearch-query-api` JAR from the Liferay GSearch binaries folder to the deploy folder (required by the adapter)
1. Copy folder gsearch-synonyms to every active Elasticsearch server node under the *config* folder. If you use the embedded engine, copy these to `LIFERAY_HOME/data/elasticsearch6/config`.
1. Copy the `com.liferay.portal.bundle.blacklist.internal.BundleBlacklistConfiguration.config` to the `LIFERAY_HOME/osgi/configs` folder to disable the standard adapter 
1. Restart portal.
1. Check from logs or Gogo shell that it was installed correctly and the default adapter is not active. Do a test search with the standard search portlet.
1. Do a full reindex to update index settings and mappings.

## Updating Synonym Dictionaries

1. Edit the synonym files as required
1. Close and open the index to refresh the analyzers. This can be done directly from cURL or Kibana. From Kibana console run (replace the index name with the right one):

```
	POST /liferay-20099/_close
	POST /liferay-20099/_open
```

For more information about the synonym filter, see Elasticsearch documentation at https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-synonym-tokenfilter.html 

