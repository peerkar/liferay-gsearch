# Liferay GSearch Geolocation Query Contributor

This is the GSearch geolocation query contibutor module.

With this feature you can boost documents which smaller geodistance. This module is meant to be used with gsearch-indexer-postprocessor-module.

# How it works

when saving a portal asset, the indexer postprocessor extracts current user's geolocation data and stores it to index.

When searching this plugin can be used to boost documents having smaller geodistance to the user doing the search.

## Installation

Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) and deploy:

* fi.soveltia.liferay.gsearch.query.contributor.audiencetargeting-[VERSION].jar

## Configuration

After the module has been deployed succesfully, please see the configuration options in Control Panel -> Configuration -> System Settings -> Other -> GSearch Geolocation Query Contributor. This feature is disabled by default.


