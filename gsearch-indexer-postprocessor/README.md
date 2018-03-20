# Liferay GSearch Indexer Postprocessor

This is the GSearch indexer post processor. It adds the following fields to the searchindex:

* Geolocation data for the indexed asset

## Installation

Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) and deploy:

* fi.soveltia.liferay.gsearch.indexer.postprocessor-[VERSION].jar

## Configuration

After the module has been deployed succesfully, please see the configuration options in Control Panel -> Configuration -> System Settings -> Other -> GSearch Indexer Postprocessor. 

__Options__:

* Elasticsearch field name to store the geolocation data to
* Test IP address for extracting the geospatial data



