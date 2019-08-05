# 2019-08-05

Liferay GSearch application binaries. This build has been tested with the following portal versions:

* DXP 7.1 fixpacks 10

It might work with previous fixpacks and CE, too. Just not tested.

# Changelog

## 2019-08-05

* Titles and description HTML cleanup fix.

## 2019-07-18

* Result layouts CSS configuration property typo fix.

## 2019-07-12

* Miniportlet styling 

## 2019-07-10

* Fixed MBMessage results links and titles not generating properly.
* Fixed "More" link on the results list not filtering the type correctly (Mini Search)
* Styling changes for GSearch portlet and More Like This

## 2019-06-19

* Fixed the Clause configuration not appearing in the system settings
* Fixed the recommender default configuration failing (workaround for a platform issue) 

## 2019-06-17

* Core is now headless ready.
* REST API module available. API implemented for search and recommendations
* Default configuration updates based on testing with about 4k article base.

## Installation Instructions

Please see the project main [README](https://github.com/peerkar/liferay-gsearch)

Get the custom Elasticsearch adapter for your portal version in folder "custom-elasticsearch-adapter"
 
