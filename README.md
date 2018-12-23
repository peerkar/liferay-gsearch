# Liferay GSearch

Liferay GSearch is a modular and highly configurable, Google-like search application for Liferay 7.0 and 7.1 CE and DXP bringing many of the [features](https://github.com/peerkar/liferay-gsearch/wiki/About) missing in the standard Liferay search portlet available. 

This application gives you full control over queries sent from Liferay to Elasticsearch and makes it possible to use user's contextual information in constructing the query. Its query rule engine makes it possible to create conditions for clauses. The application has multiple extension points for customization. You can also just choose to use the backend modules and create your own UI implementation.

![Liferay GSearch](https://github.com/peerkar/liferay-gsearch/raw/master/gsearch-doc/screenshots/gsearch.gif)

## News
* __(2018-12-23)__ Major version update, including:
	* Streamlined custom Elasticsearch adapter installation: no more flooding to log
	* Configuration modularization. Added a possibility to override facets, query and assettypes configuration from  client calling the core
	* New default item builder. Now support for most asset types can added just by adding a configuration entry without a need to write a new result item builder.
	* More query type specific configuration options
	* New, extensible query condition rule support. In the first phase there's support for keyword matches and Audience Targeting segment conditions.
	* New content suggestions mode for Mini web portlet
	* Added more configuration options for More Like This portlet
	* New Audience Targeting integration. Now it's possible to use just the user segmenting as query conditions.
	* Changed configuration syntax more Elasticsearch configuration like.
	* Default configurations with examples are now set automatically on new install. Copying configuration files is not more needed
* __(2018-11-15)__ Audience Targeting query contributor for 7.1 now [available](https://github.com/peerkar/liferay-gsearch/tree/master/binaries)
* __(2018-11-05)__ New More Like This portlet using Elasticsearch MLT query. Google Maps result view. New facet selections bar.
* __(2018-10-20)__ [Geolocation query contributor for 7.1 is now available](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).
* __(2018-09-17)__ Custom adapter for __Elasticsearch 6.1.3__ on DXP 7.1 is now available.

## Documentation

* [About](https://github.com/peerkar/liferay-gsearch/wiki/About)
* [About project modules](https://github.com/peerkar/liferay-gsearch/wiki/Project-Modules)
* [Compatibility matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix)
* [Changelog](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)
* [Documentation Wiki](https://github.com/peerkar/liferay-gsearch/wiki)

## Quick Full Installation Guide

__Important!__: The master branch (module major version 3) are __for Liferay 7.1 only__. 

Instructions below only apply for __7.1 DXP Fixpack 2 and above__.

Find out the compatible module versions for your older portal versions in [compatibility matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix).

For more detailed installation guide, see [Installation Instructions](https://github.com/peerkar/liferay-gsearch/wiki/Installation-Instructions).

### Step 1 

Install standalone Elasticsearch server and configure the portal to use that. This is optional but without it most of the features and configuration won't be available because of limitations of embedded server. See installation instructions at https://dev.liferay.com/en/discover/deployment/-/knowledge_base/7-0/installing-elasticsearch

### Step 2

__Do full reindex__ to create custom analyzers, mappings and settings. 

### Step 3

Install Audience Targeting plugin. This is optional but allows to use integrate Audience Targeting user segmenting in query conditions as well as automatically boost segmented content.

### Step 4

Download and deploy all the modules for your portal version from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).

Check that all the modules are deployed correctly. Some modules, like geolocation and web need additional configuration, like Google Maps API key and IPStack key but those can be added later.

### Step 5

Add a page with friendly URL "/viewasset" and add Asset Publisher on it. You can change that later in the configuration.
 
### Step 6

Place the GSearch portlet on some page and test.

## Known Issues (important)

For keyword suggester to work, you have to update the custom Elasticsearch adapter OSGi bundle. Run "update BUNDLE_ID" in Gogo shell and you're good.

There's currently a bug in 7.1 Soy framework generating lots of log data. For now please set a logging level for  com.liferay.portal.portlet.bridge.soy.internal.SoyPortlet to "OFF". Issue can be found here: https://issues.liferay.com/browse/LPS-85186 
 
## Important Note About Permissions

This solution, as it is, relies only on the content specific permissions which are indexed in the Elastisearch index.

The standard Liferay search portlet relies on both the indexed permissions and on post permission filtering which happens after search results have been fetched. This approach has historically made features like paging and sorting problematic. 

So, is this application secure? Because Liferay permissioning only understands *grant* and not *deny* permissions, this application is more restrictive than standard search portlet and doesn't expose anything, users should not be allowed to see. However as it doesn't take the inherited roles and permissions into account, users might not see everything they should see. What this means is, that this application is currently suitable mostly for public websites or for private sites where these restrictions won't matter.

To extend this solution to fully support inherited role permissions, it's suggested to:

1. Extend the index schema with custom permission fields
1. Sync the inherited role permissions to the index
1. Create a custom fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder service implementation with a higher service priority to add the custom permission clauses. This extension point has a dynamic reference option so that it'd be easily customizable.

In a large scale system, this approach would have to be designed carefully to avoid performance problems.

## Disclaimer

This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to serve your own purposes. If you have good development ideas, please leave ticket or ping me. Also many thanks in advance for any bug findings.
