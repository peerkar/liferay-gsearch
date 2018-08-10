# Liferay GSearch

Liferay GSearch is a modular and highly configurable, Google-like search application for Liferay 7 CE and DXP bringing many [features](https://github.com/peerkar/liferay-gsearch/wiki/About) missing in the standard search portlet available.

# Documentation

* [About](https://github.com/peerkar/liferay-gsearch/wiki/About)
* [Screenshots](https://github.com/peerkar/liferay-gsearch/wiki/Screenshots)
* [Compatibility Matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix)
* [Changelog](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)
* [Documentation Wiki](https://github.com/peerkar/liferay-gsearch/wiki)

# Quick Installation Guide

1) Find out the module versions compatible to your portal version in [compatibility matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix).

2) Download the following jars from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).

* fi.soveltia.liferay.gsearch.core-api-VERSION.jar
* fi.soveltia.liferay.gsearch.core-impl-VERSION.jar
* fi.soveltia.liferay.gsearch.query-VERSION.jar
* fi.soveltia.liferay.gsearch.web-VERSION.jar

3) Deploy the jars and check that all the modules are deployed correctly (active state).

4) Download the default core configuration **fi.soveltia.liferay.gsearch.core.configuration.GSearchCore.config** for your core version from [configs folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/core-config) file and put it into osgi/configs. 

5) Create a page with friendlyURL */viewasset* and put an Asset Publisher on it (Can be changed later in the configuration).

Additionally, if you want to take advantage of query suggestions and fully configurable query building and relevancy you need to install additionally the custom Elasticsearch adapter. For that:

6) Download the following jar from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).

* com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar 

7) Deploy and check that module installs correctly (there might be errors in the log when standard adapter gets uninstalled).

8) Do full reindex

9) Place the GSearch portlet on some page and test.

Done.

For full installation guide, including add-ons, see [Installation Instructions](https://github.com/peerkar/liferay-gsearch/wiki/Installation-Instructions).

# Important Note About Permissions

This solution, as it is, relies only on the content specific permissions which are indexed in the Elastisearch index.

The standard Liferay search portlet relies on both the indexed permissions and on post permission filtering which happens after search results have been fetched. This approach has historically made features like paging and sorting problematic. 

So, is this application secure? Because Liferay permissioning only understands *grant* and not *deny* permissions, this application is more restrictive than standard search portlet and doesn't expose anything, users should not be allowed to see. However as it doesn't take the inherited roles and permissions into account, users might not see everything they should see. What this means is, that this application is currently suitable mostly for public websites or for private sites where these restrictions won't matter.

To extend this solution to fully support inherited role permissions, it's suggested to:

1. Extend the index schema with custom permission fields
1. Sync the inherited role permissions to the index
1. Create a custom fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder service implementation with a higher service priority to add the custom permission clauses. This extension point has a dynamic reference option so that it'd be easily customizable.

In a large scale system, this approach would have to be designed carefully to avoid performance problems.

# Disclaimer

This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to serve your own purposes. If you have good development ideas, please leave ticket or ping me. Also many thanks in advance for any bug findings.
