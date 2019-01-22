# Liferay GSearch Geolocation Query Contributor

Allows to index content editing users coordinates to content asset (based on the IP address) and in searching, to boost contents geographically closer to the searching user. 

It also enables using the Google Maps result layout. Sample configuration is provided in the GSearch Portlet configuration.

Module consist of geolocation indexer postprocessor and a geolocation query contributor, using Liferay GSearch QueryContributor API.

## Prerequisites

Internet connection for both having and resolving IP addresses.

## Configuration

After the module has been deployed succesfully, please see the configuration options in Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Geolocation

