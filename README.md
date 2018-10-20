# Liferay GSearch

Liferay GSearch is a modular and highly configurable, Google-like search application for Liferay 7.0 and 7.1 CE and DXP bringing many [features](https://github.com/peerkar/liferay-gsearch/wiki/About) missing in the standard search portlet available.

## News

* __(2018-10-20)__ [Geolocation query contributor for 7.1 is now available](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest).
* __(2018-09-17)__ Custom adapter for __Elasticsearch 6.1.3__ on DXP 7.1 is now available.

## Documentation

* [About](https://github.com/peerkar/liferay-gsearch/wiki/About)
* [Screenshots](https://github.com/peerkar/liferay-gsearch/wiki/Screenshots)
* [About project modules](https://github.com/peerkar/liferay-gsearch/wiki/Project-Modules)
* [Compatibility matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix)
* [Changelog](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)
* [Documentation Wiki](https://github.com/peerkar/liferay-gsearch/wiki)

## Quick Installation Guide

__Important!__: The master branch and binaries in the *binaries/latest* folder (module major version 3) are __for Liferay 7.1 only__. Audience Targeting query contributor is not updated because Audience targeting for 7.1 is not yet released.
Find out the compatible module versions for your older portal versions in [compatibility matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix).

### Step 1 

Download and deploy following jars from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).

* fi.soveltia.liferay.gsearch.core-api-VERSION.jar
* fi.soveltia.liferay.gsearch.core-impl-VERSION.jar
* fi.soveltia.liferay.gsearch.query-VERSION.jar
* fi.soveltia.liferay.gsearch.web-VERSION.jar

Check that all the modules are deployed correctly.

### Step 2

Download the default core configuration for your core version from [configs folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/core-config) file and put it into osgi/configs. 

### Step 3 

You need to have an Asset Publisher to show the search result. By default there it has to be on a page with friendlyURL '/viewasset'. You can change that later in the configuration.
 
### Step 4

If you want to take advantage of all the goodies, including query suggestions and configurable query building and relevancy you need to install the custom Elasticsearch adapter (recommended). 

Find out the compatible adapter version for your portal versions in [compatibility matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix).

Notice, that when using custom adapter on __7.1__, a __standalone Elasticsearch server has to be used__ as the analyzers use stemmer filter which is not supported in the embedded server.

Download and deploy the jar from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).

Check that module installs correctly. When installing, this module tries to disable the standard adapter and might cause errors to log. Give it a minute to finish the process.

__Do full reindex__ to create custom analyzers, mappings and settings.

### Step 5

Place the GSearch portlet on some page and test.

For full installation guide, including add-ons, see [Installation Instructions](https://github.com/peerkar/liferay-gsearch/wiki/Installation-Instructions).

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
