# Liferay GSearch

This is the Google Like search project for Liferay DXP. The code is originally created for the blog series:

 * [Part 1](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search)
 * [Part 2](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-ii-filter-by-structure-and-document-type)
 * [Part 3](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iii-autocompletion-suggestions)
 * [Part 4](https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search-part-iv-query-tuning-and-lucene-syntax)

The portlet is written using SOY and Metal.JS. 

## Why?
Creating this application served originally for two purposes: to experiment with SOY & Metal.JS (which are a really great combination) but more importantly: to make a simple, fast and bookmarkable Liferay search with optimized querying and filtering. 

## Features

* Completely Ajaxed: no page refreshes needed
* Bookmarkables searches (using GET parameters)
* Simple and short search parameters
* Support for Boolean operators and Lucene syntax (Elasticsearch StringQuery)
* Filter by asset type (configurable), scope and modification time
* Subfiltering by Web Content Structure and File Document Type and Extension (configurable)
* Sorting by score, title and modification date
* Autocomplete / keyword suggester (configurable)
* Query suggester (configurable) & alternate search
* Selectable result layouts, image layout for image search
* It's fast

## Requirements
This application is written against Liferay DXP fixpack level 28. It requires version 2.1.0 of the Soy portlet bridge.
If you are using Liferay DXP service pack bundles, the minimum requirement is SP5.

## Installation
1. Have a Liferay workspace (requirement) in you Liferay IDE. 
2. Clone this repository in the modules directory
3. Do a Gradle refresh (in the context menu of Eclipse) for the modules directory
4. Build and deploy to you Liferay (DXP) installation

## Configuration
After deploying there are two things to do:
1. Create a page and put there an Asset Publisher portlet. Typically, you would configure this page to be hidden from navigation menu.
2. In the portlet configuration, in Control Panel -> Configuration -> System Settings -> Other -> Gsearch display configuration, point "Asset Publisher page friendly URL" to the friendly of of the page you just created.

That page is needed for showing any contents that do not have a display page but are in the CMS only. By default the portlet searches for a page with friendlyURL "/viewasset" but you can change that in the  configuration.

Please also see the other configuration settings and tune them to your likings.

## Installing Elasticsearch Server
For tweaking, debugging and monitoring purposes it's highly recommended to run the application against separate Elasticsearch server (i.e. not embedded mode).

Installation instructions are [here](https://dev.liferay.com/discover/deployment/-/knowledge_base/7-0/installing-elasticsearch).

ElasticHQ is an excellent Elasticsearch plugin for managing and monitoring indexes and Elastic cluster. Installation instructions [here](http://www.elastichq.org/support_plugin.html). After installation point your browser (by default) to http://localhost:9200/_plugin/hq/

## Roadmap
This is an ongoing project. Next upcoming features:

 * Support for Elasticsearch 5.6
 * Considerably better support for Elasticsearch StringQuery type.
 * Support for audience targeting. Dynamic field boosting by means of machine learning.
 * Integration tests


## Credits
Thanks to Tony Tawk for the Arabic translation!

## Disclaimer
This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to your own purposes but if you like it and have ideas you would like to see in this application I'd be glad to hear about those. Also many thanks in advance for any reported bug findings.
	


