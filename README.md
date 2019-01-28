# Liferay GSearch

Liferay GSearch is a modular and highly configurable, Google-like search application for Liferay 7.0 and 7.1 CE and DXP bringing many [features](https://github.com/peerkar/liferay-gsearch/wiki/About) missing in the standard Liferay search portlet available. 

This application gives you full control over queries sent from Liferay to the search engine and makes it possible to consume user contextual information as well as create conditional subqueries, with the help of a rule engine. With this application you can, for example, bring certain result higher on the list based on user's Audience Targeting segments, location or keyword matching.

While this application can be used with Liferay embedded Elasticsearch or any search engine supported by Liferay, it's designed to be used with the provided, custom Elasticsearch adapter which extends the standard query type support.

The application has multiple extension points for customization and you can just choose to use the backend modules and create your own UI implementation.

![Liferay GSearch](https://github.com/peerkar/liferay-gsearch/raw/master/gsearch-doc/screenshots/gsearch.gif)

## News
* (2019-01-27) [Core version 6.0.0 with DXP 7.0 backport and lots of improvements now available](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)

## Documentation

* [About](https://github.com/peerkar/liferay-gsearch/wiki/About)
* [Project modules](https://github.com/peerkar/liferay-gsearch/wiki/Project-Modules)
* [Compatibility](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility)
* [Changelog](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)
* [Documentation Wiki](https://github.com/peerkar/liferay-gsearch/wiki)

## Installation

There are basically two options for using this application: with or without the provided custom Elasticsearch 6.1 adapter.

__With custom adapter:__

* Take use of all the configuration options and extra query types for improving relevancy
  * Use extra query types like function score queries to improve relevancy
  * Use all the configuration options for Query String query (without, for example per field configurations won't work)
* Take use of improved Elasticsearch index configuration and mappings
* Use keyword suggesters

__Without custom adapter__

* Limited functionalities but can be used it with any supported search engine, like SOLR
* Works on recent CE 7.0 and 7.1 versions

## Minimal Installation Guide

### Step 1

Download Liferay GSearch binaries for your portal version from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).

### Step 2

Deploy JARS and check from log and from Gogo shell that everything deployed correctly.

__Notice:__ If you don't plan to use the Audience Targeting or Geolocation integration, don't deploy those modules (check module names).

### Step 3

Deploy the GSearch portlet from Widgets menu to a portal page.

### Step 4

Create a page with friendly URL "/viewasset" on a same site as the search portlet and place an Asset Publisher on it.

__That's all.__

## Full Installation Guide

In addition to the steps before:

### Step 5

Install standalone Elasticsearch 6.1 server and configure the portal to use that. See server installation instructions [here](https://dev.liferay.com/en/discover/deployment/-/knowledge_base/7-0/installing-elasticsearch)

### Step 6

Dowload and deploy the custom Elasticsearch adapter binary (or binaries, depending on the portal version) from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries) for your portal version (see __adapter__ subfolder).

To avoid any conflicts with the standard Adapter, put the respective module on a module blackist in the System Configuration. Notably on DXP 7.0, you also have to deactivate or uninstall the ES 2.1 adapter (or reboot portal after blacklisting). 

### Step 8

__Do full reindex__ to create custom analyzers, mappings and settings. 

### Step 9

To enable Audience Targeting integration and support, install Liferay Audience Targeting plugin from Liferay Marketplace. 

This is also optional but greatly enchances possibilities to use user contextual information in query boosting by allowing to use integrate Audience Targeting user segments in query conditions as well as automatically boost segmented content.

### Step 10

Check configurations in Control Panel -> System Settings -> GSearch. There are lots of options and examples. For example, if you plan to use geolocation, you have to provide there Google and IPStack API keys.

__Notice__ that More Like This portlet has a portlet instance configuration.

### Step 11

If you are willing to contribute or have problems or questions, ping me filing a ticket. I'm doing the project almost completely as a hobby, so the installation or configuration instructions are at times minimal. Sorry for that.

## Known Issues (important)

1. For keyword suggester to work, you have to update the custom Elasticsearch adapter OSGi bundle. Run "update BUNDLE_ID" in Gogo shell and you're good.

1. There's currently a bug in 7.1 Soy framework generating lots of log data. For now please set a logging level for  com.liferay.portal.portlet.bridge.soy.internal.SoyPortlet to "OFF". Issue can be found here: https://issues.liferay.com/browse/LPS-85186 
 
## Important Note About Permissions

This solution, as it is, relies only on the content specific permissions which are indexed in the Elastisearch index. It doesn't take the role inherited permissions into account.

The standard Liferay search portlet relies on both the indexed permissions and on post permission filtering which happens after search results have been fetched. This approach has historically made features like paging and sorting problematic. 

So, is this application secure? Because Liferay permissioning only supports *grant* and not *deny* permissions, this application is more restrictive than standard search portlet and doesn't expose anything, users should not be allowed to see. However as it doesn't take the inherited roles and permissions into account, users might not see everything they should see. What this means is, that this application is currently suitable mostly for public websites or for private sites where these restrictions won't matter.

If you want to enhance the provided solution and extend the permissioning support, you can override the default fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder service implementation with a higher ranking implementation priority. The extension point has a dynamic reference option so that it'd be easily customizable.

## Disclaimer

This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to serve your own purposes. If you have good development ideas, please leave ticket or ping me. Also many thanks in advance for any bug findings.
