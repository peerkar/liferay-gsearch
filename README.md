# Liferay GSearch
The Google like search for Liferay 7 CE and Liferay DXP.


# Table of contents

1. [Whats New](#Whats_New)
1. [Background](#Background)
1. [Features](#Features)
1. [Screenshots](#Screenshots)
1. [Requirements](#Requirements)
1. [Project Modules](#Modules)
1. [Quick Installation Guide](#Quick_Installation_Guide)
1. [Full Installation Guide](#Full_Installation_Guide)
1. [Enabling Audience Targeting Contributor](#Audience_Targeting_Contributor)
1. [Enabling Geolocation Contributor](#Geolocation_Contributor)
1. [Enabling Result Item Highlighter](#Result_Item_Highlighter)
1. [Embedding Search Field into a Theme](#Search_Field)
1. [Sample Configurations](#Configurations)
1. [Adding Support for Asset Types](#Adding_Asset_Type_Support)
1. [Adding a New Query Contributor, in Google terms "signal"](#Adding_Query_Contributor)
1. [Adding a Result Item Processor](#Adding_Result_Item_Processor)
1. [Custom querySuggestion Mapping](#querySuggestion)
1. [Important Notes](#Important)
1. [FAQ](#FAQ)
1. [Project Roadmap](#Roadmap)
1. [Credits](#Credits)
1. [Disclaimer](#Disclaimer)
1. [Changelog](#Changelog)


# What's New <a name="Whats_New"></a>

## 2018-03-20

* Added Geolocation query contributor and indexer post processor for that. With these modules you can increase the relevancy for document geographically closer to you. 

## 2018-03-17

__Major API changes and streamlining__. Added new interfaces to significantly ease extending and customizing this solution to your needs.

* Added __QueryContributor__ service interface. With this service you can easily add any custom clauses, or in Google terms "signals" to the main query to improve relevancy. Audience Targeting module is using this as of now.
* Added __ResultItemProcessor__ service interface. With this you can process result items before they are sent to the user interface.  See the gsearch-hightlight-item-by-tag sample.
* Created new __QueryPostProcessor__ service interface. QueryIndexer and QuerySuggester processors are there by default but you can add your custom processors by creating a new module and creating a service component for the interface.
* Added __ResultItemBuilder__ service interface if you need to create result item parsers for not out of the box supported asset types (or override the existing ones).
* Added __ClauseBuilder__ service interface. With this you can create implementations for not out of the box supported query types or override the existing ones.
* Splitted the bloated core configuration to the relevant module configurations

See more instructions for these new interfaces below in this document.

__Important__

* Notice that the main configuration file name has been changed. Deploy the new one and remove the old one to avoid confusion.
* New module versions. Remove the old ones from osgi/modules before deployin the new ones.

# Background <a name="Background"></a>

This is the Google like search project for Liferay CE & DXP. The code is originally created for the blog series:

 * [Part 1: Creating a google like search](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search)
 * [Part 2: Filter by structure and document type](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-ii-filter-by-structure-and-document-type)
 * [Part 3: Autocompletion and suggestions](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iii-autocompletion-suggestions)
 * [Part 4: Query tuning and Lucene syntax](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iv-query-tuning-and-lucene-syntax)
 * [Part 5: Finale](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-v-finale)

This project served many purposes for me. I wanted to experiment with SOY & Metal.JS (which are a really great combination), practise writing OSGI compliant code but most importantly: I wanted to have a highly configurable, alternative search portlet for Liferay having support for Boolean operators and Lucene syntax and many other features currently missing in the standard search portlet, like autocompletion. I wanted to have a search portlet with better control over hits relevancy.

# Features <a name="Features"></a>

* Google like appearance and UX
* Completely ajaxed interface (no page transitions)
* 3 selectable search result layouts (image card layout available for files / image)
* Sortable search results (not available in default Liferay search)
* Bookmarkable searches with short urls which can easily be collected to Google Analytics
* Autocompletion using aggregate query suggestions (phrase and completion suggesters)
* Automatic alternate search 
* Support for Boolean operators and Lucene syntax
* Configurable:
    * Asset types to search for
    * Facets to retrieve
    * Sort fields
    * Fields to search for and their boosting
    * Suggesters
    * etc.
* Possibility to use contextual information to improve relevancy. Current modules:
	*  Audience targeting
	*  Geolocation
* Possibility to highlight result items based on your criteria.
* Ability to include non-Liferay resources in the search results
* Easy and fast extendability
* Speed ; It's fast

# Screenshots <a name="Screenshots"></a>

## Basic Usage
![Basic Usage](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/basic-usage.gif)

## Image Layout
![Image Layout](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/image-layout.gif)

## Configuration
![Configuration ](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/configuration.gif)

## Non-Liferay Assets in the Search Results
![Non Liferay Assets](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/non-liferay-asset.gif)

# Requirements <a name="Requirements"></a>

## Mandatory <a name="Requirements_Mandatory"></a>

* At least Liferay __DXP SP5__ / fixpack version 28 OR Liferay __CE GA5__

## Optional <a name="Requirements_Optional"></a>

Some of the features require the custom Elasticsearch adapter. If you plan to use that, please also install a standalone Elasticsearch server to be able to configure custom type mappings. Installing a standalone server is recommended anyways.


# Project Modules <a name="Modules"></a>

## gsearch-core-api 
Core API module for the backend logic.

## gsearch-core-impl 
Core Implementation module for the backend logic.

## gsearch-query-api 
This module contains contains the extension for portal search API's StringQuery type, the QueryStringQuery query type.  

## gsearch-indexer-postprocessor
Indexer post processor for the Liferay GSearch

## gsearch-web 
UI module written using SOY & Metal.js.

## gsearch-mini-web 
Miniportlet for adding a search field into a theme. Please see the gsearch-test-theme for an example.

## gsearch-test-theme
A test theme embedding miniportlet into it.

## gsearch-highligh-result-item-by-tag 
An item highlighter example module adding possibility to highlight items on the result list if they have certain tags. This module is utilizing the GSearch  ResultItemProcessor API. 

## gsearch-query-contributor-audience-targeting
Audience Targeting module enabling result items boosting if they match current user's segments. This module is using the GSearch QueryContributor API

## gsearch-query-contributor-geolocation
This module helps to improve relevancy for contents geographically closer to you. This module is using the GSearch QueryContributor API

## gsearch-elasticsearch-adapter
A custom Elasticsearch adapter implementing Elasticsearch QueryStringQuery translator into Liferay portal search API. It has also index setting customizations: custom analyzers for keyword suggester and ascii folding filter for the title, description and content fields to better support non-ascii characters (for us non English speaking people).

Please see the adapter in its' [own repo](https://github.com/peerkar/gsearch-elasticsearch-adapter).

# Quick Installation Guide <a name="Quick_Installation_Guide"></a>

This installs the basic functionality. If you're having problems or want to install all the features, see full instructions below.

For choosing the right version of custom Elasticsearch adapter, com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar, please see [Step 2 - Install the Custom Elasticsearch Adapter](#Installation_2) below.

There's no fixpack / CE GA -compatibility matrix currently available but initially try to use the newest module in the [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest). Older versions can be found in the [archive folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/archive).

When updating, please remove the old modules before deploying the new ones.

1) Download and deploy following jars from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest)

* com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar
* fi.soveltia.liferay.gsearch.core-api-VERSION.jar
* fi.soveltia.liferay.gsearch.core-impl-VERSION.jar
* fi.soveltia.liferay.gsearch.query-VERSION.jar
* fi.soveltia.liferay.gsearch.web-VERSION.jar

2) Download the default configuration **fi.soveltia.liferay.gsearch.core.configuration.GSearchCore.config** from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) file and put it into osgi/configs.

3) Do full reindex.

# Full Installation Guide <a name="Full_Installation_Guide"></a>

__If you are updating the modules__:

* Please remember to remove the older versions from osgi/modules and clean up osgi/state folder first.
* Please check the configuration file for changes. Preferably put the sample configuration file in the [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest/) to the osgi/configs folder.


## Step 1 <a name="Installation_1"></a>

### Option 1 (The Easy Way) 

Download following jars from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest) and deploy:

* fi.soveltia.liferay.gsearch.core-api-VERSION.jar
* fi.soveltia.liferay.gsearch.core-impl-VERSION.jar
* fi.soveltia.liferay.gsearch.query-VERSION.jar
* fi.soveltia.liferay.gsearch.web-VERSION.jar

After installing the easiest way to check that modules have been properly installed, is to use Gogo shell. For example: 

```
> telnet localhost 11311
> lb -s gsearh

```

Check that status of all the modules is active.

### Option 2 (The Advanced Way)
If you want to build everything by yourself, you need to have:

1. Liferay workspace 
2. Clone this repository in the modules directory
3. Do a Gradle refresh (in the context menu of Eclipse) for the modules directory
4. Build and deploy to you Liferay (DXP) installation

If you need to build the Elasticsearch adapter please see [this repository](https://github.com/peerkar/gsearch-elasticsearch-adapter)

## Step 2 - Install the Custom Elasticsearch Adapter <a name="Installation_2"></a>

This is not mandatory but if you want to have all the features and configurability there, then use this one and use a standalone Elasticsearch server. 

Download following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) and deploy:

* com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar

The VERSION depends on the fixpack or GA level you are running the Liferay on. Two versions are currently available: one basing on default Elasticsearch adapter version 2.1.13 and the other basing on 2.1.24. Fixpack 39 runs only on the newer one.

Please see again from Gogo shell that it's deployed properly. Please note that custom adapter should disable (stop) the default adapter.

```
> telnet localhost 11311
> lb -s gsear

```

 If you get errors in log and search doesn't work, please restart portal so that new adapter loads correctly.
 

## Step 3 - Configuration <a name="Installation_3"></a>

After succesfully deploying the modules portlet has to be configured. Otherwise it doesn't work.

Portlet configuration can be found in Control Panel -> Configuration -> System Settings -> Other -> Gsearch Configuration.

The easy and fast way to get this to work is to download the default configuration file **fi.soveltia.liferay.gsearch.core.configuration.GSearchCoreconfig** from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest/) and copy it to Liferay\_home\_folder/osgi/configs/

There's then just a couple more thing to do:

1. Create a page and put there an Asset Publisher portlet to show any contents that are not bound to any layout (page). By default this pages' friendlyUrl should be "/viewasset". Typically, you would configure this page to be hidden from navigation menu.
1. In the portlet configuration, in Control Panel -> Configuration -> System Settings -> Other -> Gsearch Core, point "Asset Publisher page friendly URL" to the friendly of of the page you just created.
1. Configure the portlet in Control Panel -> Configuration -> System Settings -> Other -> GSearch  Portlet.


You can find the sample configurations in the end of this documentation. 

## Step 5 - Reindex

If you were transitioning from embedded Elasticsearch server to standalone server, please reindex search indexes from Control panel -> Server Administration. 

To be sure that index type mappings have been refreshed please aldo restart the portal and Elasticsearch server and you are ready to go.

# Enabling Audience Targeting Query Contributor <a name="Audience_Targeting_Contributor"></a>

Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) and deploy:

* fi.soveltia.liferay.gsearch.query.contributor.audience.targeting-VERSION.jar

After the module has been installed, please enable that in the portlet configuration Control Panel -> Configuration -> System Settings -> Other -> GSearch Audience Targeting Query Contributor.

With this feature you can boost results falling into current user's user segments. Boost factor can be adjusted in the configuration. Create test segments and contents having those segments and play with the boost to see, how it affects hits relevancy.

# Enabling Geolocation Query Contributor <a name="Geolocation_Contributor"></a>

Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) and deploy:

* fi.soveltia.liferay.gsearch.query.contributor.geolocation-VERSION.jar
* fi.soveltia.liferay.gsearch.indexer.postprocessor-VERSION.jar

After the module has been installed, please enable and tune modules in Control Panel -> Configuration -> System Settings -> Other -> GSearch Geolocation Query Contributor and Control Panel -> Configuration -> System Settings -> Other -> GSearch Indexer Post Processor

With this feature you can boost results geographically closer to you. 

Note that to make this really work to you, you should reindex all with some geodata. The indexer postprocessor allows setting a static IP for that. 

# Enabling Highlighting Result Items by Tag <a name="Result_Item_Highlighter"></a>

Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) and deploy:

* fi.soveltia.liferay.gsearch.highlightresultitembytag-VERSION.jar

After the module has been installed, please See the configuration -> Configuration -> System Settings -> Other -> GSearch Highlight Result Item by Tag.

With this feature you can highlight items on the result list if they are match your criteria. This example implementation highlights items if they are having the configured tag.

Please note that this feature is not the same as result text highlighter, highlighting the keywords from result description. That feature is there out of the box.

# Embedding Search Field into a Theme <a name="Search_Field"></a>
Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) and deploy:

* fi.soveltia.liferay.gsearch.mini.web-VERSION.jar

Please see the gsearch-test-theme templates folder for an example of how to embed that into a theme.
There's also a theme binary in the [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/latest) if you want to test it.

After the module has been installed, please See the configuration -> Configuration -> System Settings -> Other -> GSearch Mini Portlet.

# Sample Configurations <a name="Configurations"></a>

Please see the portlet configuration in Control Panel -> Configuration -> System Settings -> Other -> Gsearch Configuration.

Documentation about the configuration values is sparse at the moment but I'll try to improve it with time. If you want to know what all those fields are about, the best source of information is Elasticsearch documentation and code of this application. 

If you are having problems with configuration (portlet stops working) it's usually because of malformed JSON. Please see Tomcat log first, if you're having problems.  

### Suggester Sample Configuration

If you don't want to use the custom Elasticsearch adapter (loosing much of the suggester functionalities), please use the settings below:


```
[
	{
		"suggesterName": "phrase",
		"suggesterType": "phrase",
		"fieldPrefix": "keywordSearch_",
		"fieldSuffix": "",
		"isLocalized": true,
		"numberOfSuggestions":  5,
		"confidence": "0.1f",
		"gramSize": "2",
		"maxErrors": "2.0f",
		"realWordErrorLikelihood": "0.95f"
	}
]	
```

If you want to use the custom Elasticsearch adapter, please use this one:

```
[
	{
		"suggesterName": "phrase",
		"suggesterType": "phrase",
		"fieldPrefix": "keywordSearch_",
		"fieldSuffix": ".ngram",
		"isLocalized": true,
		"numberOfSuggestions":  5,
		"confidence": "0.1f",
		"gramSize": "2",
		"maxErrors": "2.0f",
		"realWordErrorLikelihood": "0.95f"
	},
	{
		"suggesterName": "completion",
		"suggesterType": "completion",
		"fieldPrefix": "keywordSearch_",
		"fieldSuffix": ".suggest",
		"isLocalized": true,
		"numberOfSuggestions":  5
	}
]	
```

### Search types Sample Configuration

This configuration defines the asset types to search for.

```
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
```

### Facets Sample configuration

This configuration defines the available facets in the secondary filter menu. You can add there any indexed field.

```
[
	{
		"paramName": "entryClassName",
		"fieldName": "entryClassName",
		"icon": "icons/icon-ddm-structure.png"
	},
	{
		"paramName": "ddmStructureKey",
		"fieldName": "ddmStructureKey",
		"icon": "icons/icon-ddm-structure.png"
	},
	{
		"paramName": "fileEntryTypeId",
		"fieldName": "fileEntryTypeId",
		"icon": "icons/icon-file-entry-type.png"
	},
	{
		"paramName": "extension",
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
		"paramName": "userName",
		"fieldName": "userName",
		"icon": "icons/icon-user.png"
	},
	{
		"paramName": "assetCategoryTitles",
		"fieldName": "assetCategoryTitles",
		"icon": "icons/icon-category.png"
	},
	{
		"paramName": "assetTagNames",
		"fieldName": "assetTagNames.raw",
		"icon": "icons/icon-tag.png"
	}
]
```

### Sortfields Sample Configuration

Sort fields. You can add there any indexed field. Translations need to be added to gsearch-core-api module localization file. Please see [Sort.java](https://github.com/liferay/liferay-portal/blob/master/portal-kernel/src/com/liferay/portal/kernel/search/Sort.java) for the available values for "fieldType".

```
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
```

### Sample Query Configuration

This defines the main query. You can have there just a single query or construct it of many queries. The supported types ("queryType") at the moment are query_string, match, term and wildcard. Please see the gsearch-core-impl module QueryBuilders code for more information.

The example below defines two should (OR) queries and one must query. In the first, all the keywords have to match (AND) and it gets a boost of 3. In the second, any of the keywords can match and it gets only a boost of 1.5. Match to this query is the minimum requirement. The third query gives a boost of 1 if any of the keywords match the userName field. In other words, following results can be expected from this query configuration: documents matching all the keywords get to the top of the search results, documents matching just some of the keywords are secondary and any keywords matching the username are tertiary. 


```
[
	{
		"queryType": "query_string",
		"occur": "should",
		"operator": "and",
		"fuzziness": "",
		"boost": 2,
		"fields": [
			{
				"fieldName": "title",
				"localized": true,
				"boost": 1.1,
				"boostForLocalizedVersion": 1.2
			},
			{
				"fieldName": "description",
				"localized": true,
				"boost": 1,
				"boostForLocalizedVersion": 1.1
			},
			{
				"fieldName": "content",
				"localized": true,
				"boost": 1,
				"boostForLocalizedVersion": 1.1
			}
		]
	},
	{
		"queryType": "query_string",
		"occur": "should",
		"operator": "or",
		"fuzziness": "",
		"boost": 1,
		"fields": [
			{
				"fieldName": "title",
				"localized": true,
				"boost": 1.1,
				"boostForLocalizedVersion": 1.1
			},
			{
				"fieldName": "description",
				"localized": true,
				"boost": 1,
				"boostForLocalizedVersion": 1.1
			},
			{
				"fieldName": "content",
				"localized": true,
				"boost": 1,
				"boostForLocalizedVersion": 1.1
			}
		]
	},
	{
		"queryType": "wildcard",
		"occur": "must",
		"fieldName": "userName",
		"boost": "0.5",
		"keywordSplitter":  " ",
		"valuePrefix":  "*",
		"valueSuffix":  "*"
	}
]
```

# Adding Support for Asset Types<a name="Adding_Asset_Type_Support"></a>

The process for adding support for asset types not implemented currently, including any custom, registered asset types:

1. Create a new module
1. Create a service component implementing ResultItemBuilder interface (see samples in core-impl)
1. Add asset type localizations (2) for selection menu and result view in gsearch-core-impl/resources/Language.properties
1. Lastly add your new type to the asset type selection menu in the Configuration. See "Search types Sample Configuration" above in the doc.
1. Deploy the module and refresh the core-impl bundle in case of problems

# Adding a New Query Contributor, in Google terms "signal"<a name="Adding_Query_Contributor"></a>

With this interface you can add your custom clauses to the query to improve relevancy.

1. Create a new module
1. Create a service component implementing QueryContributor interface. See the gsearch-query-contributor-audience-targeting module for example.
1. Deploy the module and refresh the core-impl bundle in case of problems

# Adding a Result Item Processor<a name="Adding_Result_Item_Processor"></a>

With this interface it's possible to manipulate the result item to be sent to the user interface. It can be used for example for setting new properties to the result items.

1. Create a new module
1. Create a service component implementing ResultItemProcessor interface. See the gsearch-hightlight-result-item-by-tag module for example.
1. Deploy the module and refresh the core-impl bundle in case of problems

# Important Notes <a name="Important"></a>

## Permissions
Search result permissions rely currently on three fields in the indexed document: roleId, groupRoleId and userName(current owner). Thes role fields contain content specific the roles that have access to the document. When you create a content these fields contain correctly any inherited role information. However, when you update role permissions to, for example, grant web content view access to a contents on a site, these fields won't update in the index. 

This is how Liferay works at least currently. This issue will be revisited later but it's important to know about it. 

# FAQ <a name="FAQ"></a>

## This Portlet Doesn't Return the Same Results as the Standard Liferay Search Portlet?!

That's right. To improve relevancy the query in this portlet is constructed much differently from the standard portlet. Also, by default it targets the search only to title, description and content fields (with localization support) and not for example username, tags, categories etc. which I think, are generally better suitable for secondary facet filtering. For example, if I want to find documents where my name is mentioned in the content, I don't want to get all the documents where I'm a document author (username field). In the portlet configuration however, you can configure the target fields and their boost factors without any restrictions to your likings.

## Do I Have to Use the Custom Search Adapter?
No you don't but then you loose pretty much all the good stuff improving the relevancy and also the possibility for autocompletion / keyword suggester functionality.

## How Does the Suggester Work?
The suggester works by storing succesfull search keywords/phrases and offering them as autocompletion options. That's the way Google works, too.

Suggestions are bound to the portal instance (companyId) and to the UI language. 

Obviously, after installing there are no stored queries, so you have to teach the suggester to make it offer opions. Have also a look at the configuration options.

Remember that search phrases are not persisted but are index time only. If you reindex, the options have to be rebuilt again. 

## How Can I Make Non-Liferay Assets Findable in Index
To get this portlet to find "external" assets in the Liferay index you have to index those assets to that portal company's index, you want them to be findable at. It's possible to spread the search across multiple indexes but that would require some more customization of the search adapter.

There's a sample non Liferay result item builder (gsearch-core-impl) and here's how you can test it. Add a sample document into your index:

```
curl -XPOST 'localhost:9200/liferay-20116/LiferayDocumentType/?pretty' -H 'Content-Type: application/json' -d'
{
   "title": "Non Liferay Asset In Elasticsearch Index Hey",
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
```

Things to know about the fields:

* title, content, description: the fields which are targeted for search. There can also be localized versions of those.
* modified: modification date. See syntax from other Liferay documents
* entryClassName: this should match the type you configured in GSearch configuration
* treePath: this is just a random field existing in the type mapping we use for the link. That can be any other of the mapping, as long as you implement the getLink() method of the result item builder.
* companyId: your portal instance's id
* stagingGroup: this just tell's that this is a "live" content
* status: 0 = published
* roleId: roles allowed to view this content
* groupRoleId: group roles allowed to view the content
* entryClassPK: any long value. Doesn't really matter here.


## How Do I Connect a Search Field in a Theme to This Portlet?
Please see the gsearch-test-theme for an example how to embed a Miniportlet (with autocompletion) into a theme.

If you want to create your own form/field, just create a search form and make it to redirect (GET) to the page having the search portlet. You can find the parameter list in the source code but only required parameter is "q", having the keywords.


## Can I Use This With an Embedded Elasticsearch Server
If you don't want to use the custom Elasticsearch adapter, you can run this also with an embedded server. However not only the custom adapter brings a lot of extra value to this search portlet, but it makes debugging and tweaking easy or in some cases, even possible. Please remember that using embedded server is not supported option in a production environment anyways.

Installation instructions are [here](https://dev.liferay.com/discover/deployment/-/knowledge_base/7-0/installing-elasticsearch).

ElasticHQ is an excellent lightweight Elasticsearch plugin for managing and monitoring indexes and Elastic cluster. Installation instructions [here](http://www.elastichq.org/support_plugin.html). After installation point your browser (by default) to http://localhost:9200/_plugin/hq/

# Project Roadmap <a name="Roadmap"></a>
Upcoming:

 * Integration tests

# Credits <a name="Credits"></a>
Thanks to Tony Tawk for the Arabic translation!

# Disclaimer <a name="Disclaimer"></a>
This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to serve your own purposes. If you have good ideas and would like me to implement those, please leave ticket or ping me. Also many thanks in advance for any bug findings.
	
# Changelog <a name="Changelog"></a>

## 2018-02-25

* Added result item highlighter API and implementation example. This service allows to highlight result items based on your criteria. Example implementation highlights result items matching a configured tag.
* Added possibility to show result item tags
* Improved facet configuration. Now UI and configuration are completely sync. UI customization is not more needed when new facets are added through JSON configuration.
* Added possibility to use a journal article as the help text (in configuration).
* Audience targeting module configuration moved to the module itself.
* Added possibility to set query keywords directly in the query configuration. With this feature you can for example configure a subquery to always try to match a certain tag and boost documents matching it.

## 2018-02-14
* Added POC kind of support for Knowledge Base Articles

## 2018-02-14
* Scroll page to top on pagination event
* Trigger search instantly on suggestion selection

## 2018-02-12

* Made custom querySuggestion mapping automatic on custom adapter installation
* Made custom adapter to stop standard adapter automatically
* Accepted pull request on case insensitive property handling


## 2017-12-21

* Validated CE support.
* Added Minisearch portlet for embedding a searchfield into a theme.
* Added a test theme for Minisearch portlet.
* Changed searchfield configuration into a more flexible query configuration. Now the main query can be constructed of multiple queries.
* Bugs fixed:
 * Custom ES adapter adding fields twice into QueryStringQuery
 * Query suggester splitting suggestions into a comma separated list


## 2017-12-16

* Removed Audience Targeting requirement and put the functionality in its' own module. 

## 2017-12-4

* Keywords suggester / autocomplete refactoring:
	* UI component changed from Metal.js to Devbridge Autocomplete
	* Suggester configuration changed to a JSON string
	* Switched to aggregate suggester by default now
	* Added configuration for the completion type suggest field
	* Added custom analyzers and filters for the query suggesters in the index-settings.json (see custom Elasticsearch Adapter project)
	* As index field mapping for title, description and content doesn't use asciifolding filter and doesn't recognize accent characters, modified analyzers for these fields to use asciifolding filter in liferay-type-mappings.json (see custom Elasticsearch Adapter project)
