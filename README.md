# Liferay GSearch

This is the Google like search project for Liferay DXP. The code is originally created for the blog series:

 * [Part 1: Creating a google like search](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search)
 * [Part 2: Filter by structure and document type](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-ii-filter-by-structure-and-document-type)
 * [Part 3: Autocompletion and suggestions](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iii-autocompletion-suggestions)
 * [Part 4: Query tuning and Lucene syntax](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iv-query-tuning-and-lucene-syntax)
 * [Part 5: Finale](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-v-finale)


## Why?
This project originally served for two purposes: experimenting with SOY & Metal.JS (which are a really great combination) but more importantly: to make a fast, bookmarkable, easily configurable and tunable, dynamic Liferay search.

## Features

* Google like  appearance and UX (at least I hope so)
* Completely ajaxed interface (no page transitions)
* 3 selectable search result layouts (for example Google like image search layout)
* Sortable search results (not available in default Liferay search)
* Bookmarkable searches with short urls which can easily be collected to Google Analytics
* Autocompletion & query suggestions
* Automatic alternate search 
* Support for Boolean operators and Lucene syntax
* Selectable (configurable):
 * Asset types to search for
 * Facets to retrieve
 * Sort fields
 * Fields to search for and their boosting
* Experimental machine learning features to improve relevancy by means of:
 * Audience targeting
 * Geodistance information of the asset
* Ability to include non-Liferay resources in the search results

* Speed ; It's fast

# Screenshots

## Autocomplete

## Image Layout

## Configuration

## Requirements
This application is written on Liferay DXP fixpack level 28. It requires version 2.1.0 of the Soy portlet bridge. If you are using Liferay DXP service pack bundles, the minimum requirement is SP5.

You can also get it to work on 7 CE GA3 but some changes to the dependencies are needed. For your convenience, there are downloadable Jar-files in the Installation section. Also please note that you cannot use the Audience Targeting -feature, as it's not available in CE. 

# About Project Modules

## gsearch-core
This is the backend code for the portlet.

## gsearch-web
This is the portlet UI module. It's written using SOY & Metal.js

## gsearch-localization
This is the localization module.

## gsearch-query-api
This module contains contains the extension for portal search API's StringQuery type, the QueryStringQuery query type.

## gsearch-permission-handler
This module takes care of updating the role permissions to index when changed.

The custom search adapter is in a separate repository...

# Installation

## Step 1 / Option 1 (Easy Way)
Download the latest jars and just deploy them - except of the custom adapter (Step 2). Before this, please see the requirements and after deploying, please see that all the modules have been properly activated. The modules to install are:

 * fi.soveltia.liferay.gsearch.core-VERSION.jar
 * fi.soveltia.liferay.gsearch.web-VERSION.jar
 * fi.soveltia.liferay.gsearch.query-api-VERSION.jar
 * fi.soveltia.liferay.gsearch.permission-VERSION.jar
 * com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar

Get the files for DXP (FP 28+) [here](https://github.com/peerkar/liferay-gsearch/tree/master/gsearch-web/latest/dxp) and for Liferay 7 CE GA 3 [here](https://github.com/peerkar/liferay-gsearch/tree/master/latest/ce)

After installing the easiest way to check that modules have been properly installed, is to use Gogo shell. For example: "telnet localhost 11311" and then "lb -s GSearch"

Check that status of all the modules is active.


## Step 1 / Option 2
If you want to build everything by yourself, you need to have:

1. Liferay workspace 
2. Clone this repository in the modules directory
3. Do a Gradle refresh (in the context menu of Eclipse) for the modules directory
4. Build and deploy to you Liferay (DXP) installation

If you need to build the Elasticsearch adapter please see the NOTES.md in gsearch-doc folder


## Step 2 - Installing the Custom Elasticsearch Adapter

You can uninstall the Elasticsearch adapter from control panel apps management or, preferably, from Gogo shell:

> telnet localhost 11311
> 
> lb -s com.liferay.portal.search.elasticsearch (to get the bundle id)
> 
> uninstall bundle_id

After that you can deploy the custom adapter. Please see from Gogo shell that it's deployed properly.

One importan thing to remember: the default adapter gets installed on every reboot. 

## Configuration

After succesfully deploying the modules, there are quite a few configuration options available but only one mandatory thing to do, to get this to work: a page with an asset publisher to show the contents that are not bound to a display portlet. To configure:

1. Create a page and put there an Asset Publisher portlet. Typically, you would configure this page to be hidden from navigation menu.
2. In the portlet configuration, in Control Panel -> Configuration -> System Settings -> Other -> Gsearch display configuration, point "Asset Publisher page friendly URL" to the friendly of of the page you just created.

By default the portlet searches for a page with friendlyURL "/viewasset" but you can change that in the  configuration.

### Search types Sample Configuration 
[
	{
		"key": "web-content",
		"entryClassName": "com.liferay.journal.model.JournalArticle"
	},
	{
		"key": "file",
		"entryClassName": "com.liferay.document.library.kernel.model.DLFileEntry"
	},
	{
		"key": "discussion",
		"entryClassName": "com.liferay.message.boards.kernel.model.MBMessage"
	},
	{
		"key": "blog",
		"entryClassName": "com.liferay.blogs.kernel.model.BlogsEntry"
	},
	{
		"key": "wikipage",
		"entryClassName": "com.liferay.wiki.model.WikiPage"
	},
	{
		"key": "non-liferay-type-example",
		"entryClassName": "non-liferay-type"
	}
]

### Facets Sample configuration

[
	{
		"fieldName": "entryClassName",
		"icon": "icons/icon-ddm-structure.png"
	},
	{
		"fieldName": "ddmStructureKey",
		"icon": "icons/icon-ddm-structure.png"
	},
	{
		"fieldName": "fileEntryTypeId",
		"icon": "icons/icon-file-entry-type.png"
	},
	{
		"fieldName": "extension",
		"icon": "icons/icon-file-extension.png",
		"aggregations": [
			{
				"key": "Image",
				"values": "png,jpg,gif"
			},
			{
				"key": "MS Word",
				"values": "doc,docx"
			},
			{
				"key": "MS Excel",
				"values": "xls,xlsx"
			},
			{
				"key": "MS Powerpoint",
				"values": "ppt,pptx"
			},
			{
				"key": "Video",
				"values": "mp4,avi"
			},
			{
				"key": "Audio",
				"values": "mp3"
			}
		]
	}, 
	{
		"fieldName": "userName",
		"icon": "icons/icon-user.png"
	},
	{
		"fieldName": "assetCategoryTitles",
		"icon": "icons/icon-category.png"
	},
	{
		"fieldName": "assetTagNames",
		"icon": "icons/icon-tag.png"
	}
]

### Sortfields Sample Configuration

[
	{
		"key": "score",
		"fieldType":  0,
		"default": true,
		"localized": false
	},
	{
		"key": "title",
		"fieldName": "title",
		"fieldPrefix": "localized_",
		"fieldSuffix": "_sortable",
		"fieldType":  3,
		"localized": true
	},
	{
		"key": "modified",
		"fieldName": "modified",
		"fieldSuffix": "_sortable",
		"fieldType":  6,
		"localized": false
	}
]	


### Search Fields Sample Configuration
[
{
		"fieldName": "title",
		"localized": true,
		"boost": 2,
		"boostForLocalizedVersion": 3
	},
	{
		"fieldName": "description",
		"localized": true,
		"boost": 1,
		"boostForLocalizedVersion": 1.5
	},
	{
		"fieldName": "content",
		"localized": true,
		"boost": 1,
		"boostForLocalizedVersion": 1.5
	}
]

#FAQ



## How Can I Make Non-Liferay Assets Findable in Index

If you want to make this search to spread search over multiple indexes you have to modify the custom search adapter a little further.

With this solution you only have to take care of following prequisites:
- External assets have to be indexed in the same index as Liferay documents.
- External assets have to be defined in the configuration and they have to have following fields:
	- title which can be localized title_en_US etc.
	- entryClassName
	- modified

Well actually you can just write you own result item handler to provide these.

Also take care that you the fields you want to sort on

curl -XPOST 'localhost:9200/liferay-20116/LiferayDocumentType/?pretty' -H 'Content-Type: application/json' -d'
{
  "title": "Lucifer Simpsons Southpark Non Liferay Asset In Elasticsearch Index Hey",
  "description": "This is definitely not a Liferay Asset. Check it out by yourself.",
  "content":  "Still trying this out...",
  "modified":  "20171028095357",
	"entryClassName": "non-liferay-type",
	"treePath": "http://www.google.fi",
	"companyId": "20116",
	"stagingGroup": "false",
	"status": "0",
	"roleId": "20123",
	"groupRoleId": "20143-20131",
	"entryClassPK": "0"
}
'




## How Do I Connect a Search Field in a Theme to This Portlet?
That's easy. Just put there a form / field and link it to the page, you put the GSearch portlet.
You can find the parameter list in the source code but only q parameter is required.


## Installing Elasticsearch Server
For tweaking, debugging and monitoring purposes it's highly recommended to run this application against separate Elasticsearch server (i.e. not embedded mode).

Installation instructions are [here](https://dev.liferay.com/discover/deployment/-/knowledge_base/7-0/installing-elasticsearch).

ElasticHQ is an excellent lightweight Elasticsearch plugin for managing and monitoring indexes and Elastic cluster. Installation instructions [here](http://www.elastichq.org/support_plugin.html). After installation point your browser (by default) to http://localhost:9200/_plugin/hq/

## Roadmap
Upcoming:

 * Integration tests

## Credits
Thanks to Tony Tawk for the Arabic translation!

## Disclaimer
This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to serve your own purposes. If you have good ideas and would like me to implement those, please leave ticket or ping me. Also many thanks in advance for any bug findings.
	



