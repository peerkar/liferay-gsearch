# DXP-7.0-SP8-SP9

This distribution and custom Elasticsearch adapter has been tested with following portal versions:

* DXP 7.0 Service pack 8
* DXP 7.0 Service pack 9

Without the custom adapter, it works additionally at least on:

* DXP 7.0 Service pack 7
* CE 7.0 GA5

## Application Installation Instructions

Please see the project main [README](https://github.com/peerkar/liferay-gsearch)

## Installing Custom Adapter

1. Download the binaries from es-adapter subfolder
1. Deploy the two adapter binaries
1. Go to control panel App Manager
1. Find the standard Elasticsearch 2 module and deactivate it.
1. Add the standard adapter to module Blacklisting configuration. See instructions [here](https://dev.liferay.com/en/discover/portal/-/knowledge_base/7-0/blacklisting-osgi-modules)

If you enable keyword suggestions and have problems with them, try updating the adapter bundle from Gogo shell. This is a known issue in some scenarios, portal standard adapter not releasing the standard suggester reference.



 