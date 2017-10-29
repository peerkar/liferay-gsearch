# Liferay GSearch

This is the Google Like search for Liferay DXP by Petteri Karttunen. The code is originally created for the blog series https://web.liferay.com/web/petteri.karttunen/blog/-/blogs/creating-a-google-like-search.

The portlet is written using SOY and Metal.JS. 

## Why?
Creating this application served originally two purposes: two experiment with SOY & Metal.JS (which are a really great combination) but more importantly: to make a simple, fast and bookmarkable Liferay search with optimized querying and filtering. 

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
* It's fast

## Requirements
It's originally written to Liferay DXP FP 31 but it should work with CE (not tested).

## Installation
1. Have a Liferay workspace (requirement) in you Liferay IDE. 
2. Clone this repository in the modules directory
3. Do a Gradle refresh (in the context menu of Eclipse) for the modules directory
4. Build and deploy to you Liferay (DXP) installation

## Installing Elasticsearch Server
For tweaking, debugging and monitoring purposes it's highly recommended to run the application against separate Elasticsearch server (i.e. not embedded mode).

Installation instructions are [here](https://dev.liferay.com/discover/deployment/-/knowledge_base/7-0/installing-elasticsearch).

ElasticHQ is an excellent Elasticsearch plugin for managing and monitoring indexes and Elastic cluster. Installation instructions [here](http://www.elastichq.org/support_plugin.html). After installation point your browser (by default) to http://localhost:9200/_plugin/hq/


## Credits
Thanks to Tony Tawk for the Arabic translation!

## Disclaimer
This portlet hasn't been thoroughly tested and is provided as is. You can freely develop it further to your own purposes but if you like it and have ideas you would like to see in this application I'd be glad to hear about those. Also many thanks in advance for any reported bug findings.

I hope 
Enjoy!
