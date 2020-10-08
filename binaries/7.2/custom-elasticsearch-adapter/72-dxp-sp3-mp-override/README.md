# 72-DXP-SP3-MP-OVERRIDE

Adapter for a Marketplace override deployment method, which is the preferred one.

This version has been tested with following setup:

* DXP 7.2 SP3 (FP8)
* Elasticsearch 6.8.6

## Requirements

A remote, standalone Elasticsearch instance. For testing purposes, you might get this running with the embedded engine, but there's no guarantee.

## Installation Instructions

*Important*: the `fi.soveltia.liferay.gsearch.query-VERSION.jar` should *NOT* be installed with this deployment method.

If you are upgrading the custom adapter:

1. Remove the standard adapter entries from `com.liferay.portal.bundle.blacklist.internal.BundleBlacklistConfiguration.config` in `LIFERAY_HOME/osgi/configs` folder
1. Remove the `fi.soveltia.liferay.gsearch.query-VERSION.jar` installation from `LIFERAY_HOME/osgi/modules`

Then:

1. Download and copy the JARS to `LIFERAY_HOME/osgi/osgi/marketplace/override`
1. Do a full reindex to update index settings and mappings.

