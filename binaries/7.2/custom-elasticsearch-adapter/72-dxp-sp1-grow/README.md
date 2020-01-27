# 72-DXP-SP-1-GROW

This custom Elasticsearch adapter version has been tested with following setup:

* DXP 7.2 SP 1
* Elasticsearch 6.5.0

## Requirements

A remote, standalone Elasticsearch instance. For testing purposes, you might get this running with the embedded engine, but there's no guarantee.

## Installation Instructions

1. Shutdown the portal
1. Download and copy the JARS in this folder to the deploy folder
1. Download and copy the `gsearch-query-api` JAR from the Liferay GSearch binaries folder to the deploy folder (required by the adapter)
1. Copy the `com.liferay.portal.bundle.blacklist.internal.BundleBlacklistConfiguration.config` to the `LIFERAY_HOME/osgi/configs` folder to disable the standard adapter 
1. Restart portal.
1. Check from logs or Gogo shell that it was installed correctly and the default adapter is not active. Do a test search with the standard search portlet.
1. Do a full reindex to update index settings and mappings.


