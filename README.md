# Liferay GSearch
The Google like search for Liferay 7 CE and Liferay DXP.


# Table of contents

1. [Background](#Background)
1. [Features](#Features)
1. [Screenshots](#Screenshots)
1. [Requirements](#Requirements)
1. [Project Modules](#Modules)
1. [Installation](#Installation)
1. [Enabling Audience Targeting support](#Audience_Targeting)
1. [Embedding Search Field into a Theme](#Search_Field)
1. [Sample Configurations](#Configurations)
1. [Custom querySuggestion Mapping](#querySuggestion)
1. [Troubleshooting](#Troubleshooting)
1. [Important Notes](#Important)
1. [FAQ](#FAQ)
1. [Project Roadmap](#Roadmap)
1. [Credits](#Credits)
1. [Disclaimer](#Disclaimer)
1. [Changelog](#Changelog)



# 1 Background <a name="Background"></a>

This is the Google like search project for Liferay CE & DXP. The code is originally created for the blog series:

 * [Part 1: Creating a google like search](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search)
 * [Part 2: Filter by structure and document type](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-ii-filter-by-structure-and-document-type)
 * [Part 3: Autocompletion and suggestions](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iii-autocompletion-suggestions)
 * [Part 4: Query tuning and Lucene syntax](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iv-query-tuning-and-lucene-syntax)
 * [Part 5: Finale](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-v-finale)

This project served many purposes for me. I wanted to experiment with SOY & Metal.JS (which are a really great combination), practise writing OSGI compliant code but most importantly: I wanted to have a highly configurable, alternative search portlet for Liferay having support for Boolean operators and Lucene syntax and many other features currently missing in the standard search portlet, like autocompletion. I wanted to have a search portlet with better control over hits relevancy.

# 2 Features <a name="Features"></a>

* Google like appearance and UX
* Completely ajaxed interface (no page transitions)
* 3 selectable search result layouts (image card layout available for files / image)
* Sortable search results (not available in default Liferay search)
* Bookmarkable searches with short urls which can easily be collected to Google Analytics
* Autocompletion using aggregate query suggestions (phrase and completion suggesters)
* Automatic alternate search 
* Support for Boolean operators and Lucene syntax
* Completely configurable:
    * Asset types to search for
    * Facets to retrieve
    * Sort fields
    * Fields to search for and their boosting
    * Suggesters
* Audience targeting support: configurable boosting for content matching user's user segments.
* Ability to include non-Liferay resources in the search results
* Speed ; It's fast

# 3 Screenshots <a name="Screenshots"></a>

## Basic Usage
![Basic Usage](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/basic-usage.gif)

## Image Layout
![Image Layout](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/image-layout.gif)

## Configuration
![Configuration ](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/configuration.gif)

## Non-Liferay Assets in the Search Results
![Non Liferay Assets](https://github.com/peerkar/liferay-gsearch/blob/master/gsearch-doc/screenshots/non-liferay-asset.gif)

# 4 Requirements <a name="Requirements"></a>

## Mandatory <a name="Requirements_Mandatory"></a>

* At least Liferay __DXP SP5__ / fixpack version 28 OR Liferay __CE GA5__

## Optional <a name="Requirements_Optional"></a>

Some of the features require the custom Elasticsearch adapter. If you plan to use that, please also install a standalone Elasticsearch server to be able to configure custom type mappings. Installing a standalone server is recommended anyways.


# 5 Project Modules <a name="Modules"></a>

## gsearch-core-api 
API module for the backend logic.

## gsearch-core-impl 
Implementation module for the backend logic.

## gsearch-query-api 
This module contains contains the extension for portal search API's StringQuery type, the QueryStringQuery query type.  

## gsearch-web 
UI module written using SOY & Metal.js.

## gsearch-mini-web 
Miniportlet for adding a search field into a theme. Please see the gsearch-test-theme for an example.

## gsearch-test-theme
A test theme embedding miniportlet into it.

## gsearch-audience-targeting <a name="Modules_Audience_Targeting"></a>
Module enabling Audience Targeting support.

## gsearch-elasticsearch-adapter <a name="Modules_Adapter"></a>
A custom Elasticsearch adapter implementing Elasticsearch QueryStringQuery translator into Liferay portal search API. It has also index setting customizations: custom analyzers for keyword suggester and ascii folding filter for the title, description and content fields to better support non-ascii characters (for us non English speaking people).

Please see the adapter in its' [own repo](https://github.com/peerkar/gsearch-elasticsearch-adapter).

# 6 Installation <a name="Installation"></a>

__Quick Guide __

This installs the basic functionality. If you're having problems or want to install all the features, see full instructions below.

1) Download following jars from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest)

* com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar
* fi.soveltia.liferay.gsearch.core-api-VERSION.jar
* fi.soveltia.liferay.gsearch.core-api-VERSION.jar
* fi.soveltia.liferay.gsearch.core-impl-VERSION.jar
* fi.soveltia.liferay.gsearch.query-VERSION.jar
* fi.soveltia.liferay.gsearch.web-VERSION.jar

2) Download the default configuration **fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration.config** from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest) file and put it into osgi/configs.

3) Do full reindex.


__If you are updating the modules__:

* Please remember to remove the older versions from osgi/modules and clean up osgi/state folder first.
* Please check the configuration file for changes. Preferably put the sample configuration file in the [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest/) to the osgi/configs folder.


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
> lb -s gsear

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

Download following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest) and deploy:

* com.liferay.portal.search.elasticsearch-VERSION-GSEARCH-PATCHED.jar

Please see again from Gogo shell that it's deployed properly. Please note that custom adapter should disable (stop) the default adapter.

```
> telnet localhost 11311
> lb -s gsear

```

 If you get errors in log and search doesn't work, please restart portal so that new adapter loads correctly.
 

## Step 3 - Configuration <a name="Installation_3"></a>

After succesfully deploying the modules portlet has to be configured. Otherwise it doesn't work.

Portlet configuration can be found in Control Panel -> Configuration -> System Settings -> Other -> Gsearch Configuration.

The easy and fast way to get this to work is to download the default configuration file **fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration.config** from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest/) and copy it to Liferay\_home\_folder/osgi/configs/

There's then just one more thing to do:

1. Create a page and put there an Asset Publisher portlet to show any contents that are not bound to any layout (page). By default this pages' friendlyUrl should be "/viewasset". Typically, you would configure this page to be hidden from navigation menu.
2. In the portlet configuration, in Control Panel -> Configuration -> System Settings -> Other -> Gsearch Configuration, point "Asset Publisher page friendly URL" to the friendly of of the page you just created.

You can find the sample configurations in the end of this documentation. 


## Step 5 - Reindex <a name="Installation_6"></a>

If you were transitioning from embedded Elasticsearch server to standalone server, please reindex search indexes from Control panel -> Server Administration. 

To be sure that index type mappings have been refreshed please aldo restart the portal and Elasticsearch server and you are ready to go.

# 7 Enabling Audience Targeting Support <a name="Audience_Targeting"></a>

Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest) and deploy:

* fi.soveltia.liferay.gsearch.ct-VERSION.jar

After the module has been installed, please enable that in the portlet configuration ontrol Panel -> Configuration -> System Settings -> Other -> Gsearch Configuration.

With this feature you can boost relevancy for the contents falling into current user's user segments. You can adjust the boost factor in the configuration. Create test segments and contents having those segments and play with the boost to see, how it affects hits relevancy.

# 8 Embedding Search Field into a Theme <a name="Search_Field"></a>
Download the following jar from [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest) and deploy:

* fi.soveltia.liferay.gsearch.mini.web-VERSION.jar

Please see the gsearch-test-theme templates folder for an example of how to embed that into a theme.
There's also a theme binary in the [latest folder](https://github.com/peerkar/liferay-gsearch/tree/master/latest) if you want to test it.

# 9 Sample Configurations <a name="Configurations"></a>

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

This defines the main query. You can have there just a single query or construct it of many queries. The supported types ("queryType") at the moment are query_string, match and wildcard. Please see the gsearch-core-impl module QueryBuilders code for more information.

The example below defines three should (OR) queries. In the first, all the keywords have to match (AND) and it gets a boost of 3. In the second, any of the keywords have to match and it gets only a boost of 1.5. The third brings a boost of 1 if any of the keywords match the userName field. In other words, following results can be expected from this query configuration: hits matching all the keywords get to the top of the search results, hits matching just some of the keywords are secondary and any keywords matching the username are tertiary.


```
[
	{
		"queryType": "query_string",
		"occur": "should",
		"operator": "and",
		"fuzziness": "",
		"boost": 3,
		"fields": [
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
	},
	{
		"queryType": "query_string",
		"occur": "should",
		"operator": "or",
		"fuzziness": "",
		"boost": 1.5,
		"fields": [
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
	},	
	{
		"queryType": "wildcard",
		"occur": "should",
		"fieldName": "userName",
		"boost": "1",
		"keywordSplitter":  " ",
		"valuePrefix":  "*",
		"valueSuffix":  "*"
	}
]
```

# 10 Custom querySuggestion Mapping<a name="querySuggestion"></a>

Installing custom Elasticsearch adapter customizes querySuggestion mapping on reindex. The mapping can also be created by running the curl script:

```
curl -XPUT 'localhost:9200/liferay-20116/_mapping/querySuggestion?pretty' -H 'Content-Type: application/json' -d'
{
	"dynamic_templates": [
	{
		"template_keywordSearch": {
			"mapping": {
				"type": "string",
				"fields": {
					"ngram": {
						"type": "string",
					        "search_analyzer": "standard",
						"analyzer": "gsearch_shingle_analyzer"
					},
					"suggest" : {
						"type" : "completion",
						"analyzer" : "simple",
						"search_analyzer" : "simple"
					}
				}
			},
			"match": "keywordSearch_*",
			"match_mapping_type": "string"
		}
	}
	],
	"properties": {
		"companyId": {
			"index": "not_analyzed",
			"store": "yes",
			"type": "string"
		},
		"groupId": {
			"index": "not_analyzed",
			"store": "yes",
			"type": "string"
		},
		"priority": {
			"index": "not_analyzed",
			"type": "float"
		},
		"uid": {
			"index": "not_analyzed",
			"store": "yes",
			"type": "string"
		}
	}
}'
```


# 11 Troubleshooting <a name="Troubleshooting"></a>

## Querysuggester Not Working

This might happen at least of two reasons:

 1. You are not using the custom Elasticsearch adapter but are using the query suggester configuration for the custom adapter. Please check the configuration samples below in this document.
 2. querySuggestion type mapping has not been refreshed, probably because there was an existing one already. Please reindex and immediately after reindexing, run the CURL command from Step 5. 

In any case, if you are having problems, please check both Liferay logs and Elasticsearch logs. Elasticsearch log would be by default ELASTICSEARCH_SERVER_PATH/logs/LiferayElasticsearchCluster.log


# 12 Important Notes <a name="Important"></a>

## Permissions
Search result permissions rely currently on three fields in the indexed document: roleId, groupRoleId and userName(current owner). Thes role fields contain content specific the roles that have access to the document. When you create a content these fields contain correctly any inherited role information. However, when you update role permissions to, for example, grant web content view access to a contents on a site, these fields won't update in the index. 

This is how Liferay works at least currently. This issue will be revisited later but it's important to know about it. 

# 13 FAQ <a name="FAQ"></a>

## This Portlet Doesn't Return the Same Results as the Standard Liferay Search Portlet?!

That's right. By default this portlet targets the search only to title, description and content fields (with localization support) and not for example username, tags, categories etc. which I thought, are generally better suitable for secondary facet filtering. For example, if I want to find documents where my name is mentioned in the content, I don't want to get all the documents where I'm a document author (username field). In the portlet configuration however, you can configure the target fields and their boost factors without any restrictions to your likings.

## Does This Work on CE?

It's not tested but basically there is one thing preventing this to work on CE: new version of Soy bridge. You can however remove this barrier fairly easy:

* Downgrade the Soy bridge dependency in the web module's build.gradle, rebuild  and see if it works. Other way around, you can also try to upgrade the bridge to the portal instance. 

If you need help with creating a CE compatible version, please create a ticket.

## Do I Have to Use the Custom Search Adapter?
No you don't but then you loose the possibility to fine tune search field configuration and their boosts as well as a much better autocompletion / keyword suggester functionality.

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

## There's Audience Targeting Integration Now. How Could I Add Something Like GeoDistance There?

See 'fi.soveltia.liferay.gsearch.core.impl.query.QueryBuilderImpl'. That's where the Audience Targeting condition gets added and the place where any other logic like that should be added. If you want to improve relevancy by a field, please remember, that it has to be in the query, not in the filter.

# 14 Project Roadmap <a name="Roadmap"></a>
Upcoming:

 * Integration tests

# 15 Credits <a name="Credits"></a>
Thanks to Tony Tawk for the Arabic translation!

# 16 Disclaimer <a name="Disclaimer"></a>
This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to serve your own purposes. If you have good ideas and would like me to implement those, please leave ticket or ping me. Also many thanks in advance for any bug findings.
	
# 17 Changelog <a name="Changelog"></a>

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