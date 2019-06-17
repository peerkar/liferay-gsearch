# 2019-05-11

Liferay GSearch application binaries. This build has been tested with the following portal versions:

* DXP 7.1 fixpacks 10

It might work with previous fixpacks and CE, too. Just not tested.

# Changelog

* Core is now headless ready. Dependencies to portletrequest / portlet removed.
* REST API module available. API implemented for search and recommendations
* Default configuration updates based on testing with about 4k article base.

## Installation Instructions

Please see the project main [README](https://github.com/peerkar/liferay-gsearch)

Get the custom Elasticsearch adapter for your portal version in folder "custom-elasticsearch-adapter"
 