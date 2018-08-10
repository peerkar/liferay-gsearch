# Liferay GSearch

Liferay GSearch is a modular and highly configurable, Google-like search application for Liferay 7 CE and DXP bringing many [features](https://github.com/peerkar/liferay-gsearch/wiki/About) missing in the standard search portlet available.

# Documentation

* [About](https://github.com/peerkar/liferay-gsearch/wiki/About)
* [Screenshots](https://github.com/peerkar/liferay-gsearch/wiki/Screenshots)
* [Compatibility Matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix)
* [Changelog](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)
* [Documentation Wiki](https://github.com/peerkar/liferay-gsearch/wiki)

# Quick Installation Guide

Find out the right module versions for your portal version in [compatibility matrix](https://github.com/peerkar/liferay-gsearch/wiki/Compatibility-Matrix).

Download and deploy following jars from [binaries folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries).

* com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar (optional)
* fi.soveltia.liferay.gsearch.core-api-VERSION.jar
* fi.soveltia.liferay.gsearch.core-impl-VERSION.jar
* fi.soveltia.liferay.gsearch.query-VERSION.jar
* fi.soveltia.liferay.gsearch.web-VERSION.jar

Check that all the modules were deployed correctly.

Download the default core configuration **fi.soveltia.liferay.gsearch.core.configuration.GSearchCore.config** for your core version from [configs folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/core-config) file and put it into osgi/configs. 

Do full reindex.

Done.

For full installation guide (including add-ons) see [Installation Instructions](https://github.com/peerkar/liferay-gsearch/wiki/Installation-Instructions).

# Important Note About Permissions

This solution, as it is, relies only on the content specific permissions which are indexed in the Elastisearch index.

The standard Liferay search portlet relies on both the indexed permissions and on post permission filtering which happens after search results have been fetched. This approach has historically made features like paging and sorting problematic. 

So, is this application secure? Because Liferay permissioning only understands *grant* and not *deny* permissions, this application is more restrictive than standard search portlet and doesn't expose anything, users should not be allowed to see. However as it doesn't take the inherited roles and permissions into account, users might not see everything they should see. What this means is, that this application is currently suitable mostly for public websites or for private sites where these restrictions won't matter.

To extend this solution to fully support inherited role permissions, it's suggested to:

1. Extend the index schema with custom permission fields
1. Depending on the use case, develop a module to sync the inherited role permissions in the index, for example with a resource permission listener or with some scheduler based mechanism
1. Create a custom fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder service implementation with a higher service priority to add the custom permission clauses. This extension point has a dynamic reference option so that it'd be easily customizable.

In a large scale system, this approach would have to be designed carefully to avoid performance problems.

# Disclaimer

This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to serve your own purposes. If you have good development ideas, please leave ticket or ping me. Also many thanks in advance for any bug findings.
