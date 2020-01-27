
# Liferay GSearch

![Liferay GSearch](https://github.com/peerkar/liferay-gsearch/raw/master/screenshots/gsearch.gif)

Liferay GSearch is a highly configurable, modular and multi module Google-like search application for Liferay 7.0+  bringing many features missing in the standard Liferay search portlets available.

This application works on the low level of Liferay search adapter, has a rule based query building engine giving you full control over queries sent from Liferay to the search engine. User contextual information for personalizing the search is available for both the query conditions and in the clause configuration template variables. With this application you can elevate certain search results based for example on content's freshness and length (or any other attribute) as well as current user's segments, geolocation data, frequently used previous keywords, NLP metadata extracted from the search query or pure keyword matching.

Imagine that you'd like to for example to elevate certain contents for female users 30+ in age, from Helsinki, Finland, belonging to a user group XYZ and Liferay Analytics Cloud tied user segments and only if there's was a rainy weather, where they are at. Also you might want to check whether they have been frequently using a word stem 'Liferay'. With this engine you can configure certain clause or clauses to get applied only if one, some or all of the previous conditions are satisfied. In another situation, you might for example want to inject users' location or other contextual data to the queries sent. This is also possible.

It uses natural language processing (NLP) for contents and query keywords metadata extraction and has a semi automated Learn To Rank (LTR) machine learning integration available.

The main search portlet UI is built on React and provides several result layouts out of the box, like raw document and explain views for debugging or relevancy improvement purposes. A configurable recommendations as well as a search bar mini portlets are also available.

While this application can be used with the Liferay embedded Elasticsearch or any search engine and search engine adapter supported by Liferay, it's designed to be used with the provided, custom Elasticsearch adapter which not only has an improved index configuration but it also extends and improves the standard query type support. 

The application is modular and has multiple extension points for customizations. Headless REST API is also available.

## News
* __(2019-10-09)__ [Liferay 7.2 version available](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/7.2/)
* __(2019-02-06)__ [REST API available](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/7.1/2019-06-19)
* __(2019-01-29)__ [New Custom Elasticsearch adapters for 7.0 and 7.1 (CE and DXP) available](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)

## Documentation

* [Installation](https://github.com/peerkar/liferay-gsearch/wiki/Installation)
* [Configuration](https://github.com/peerkar/liferay-gsearch/wiki/Configuration)
* [Modules](https://github.com/peerkar/liferay-gsearch/wiki/Modules)
* [About the project](https://github.com/peerkar/liferay-gsearch/wiki/About)

## License

Liferay GSearch is, except the forked Learning the Rank demonstration which is Apache 2.0, licensed under LGPL v.3.

## Disclaimer

This application hasn't been thoroughly tested, is to be considered experimental and is provided as is. If you have good development ideas, find bugs or need help in installing it, please file a ticket.
