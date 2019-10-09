
# Liferay GSearch

Liferay GSearch is a highly configurable, modular and multi module Google-like search application for Liferay 7.0+  bringing many [features](https://github.com/peerkar/liferay-gsearch/wiki/About) missing in the standard Liferay search portlet available.

This application works on the low level of Liferay search adapter, has a rule based query building engine giving you full control over queries sent from Liferay to the search engine. User contextual information is available for both the query conditions and in the clause configuration template variables. With this application you can bring certain result higher on the list based for example on user's segments, location, frequently used previous keywords or pure keyword matching.

Imagine for example that there's a user of 30+ in age, female, from Helsinki, Finland. She's belonging to a user group XYZ and there's a rainy weather, where she's at. She's been frequently searching with a certain word stem and possibly belonging to Liferay Analytics cloud based segments. With this engine you can for example configure that certain clause or clauses get applied only if one, some or all of the previous conditions are satisfied. In another situation, you might for example want to inject users' location or other contextual data to the queries sent. This is also possible.

It users natural language processing (NLP) for contents and query keywords metadata extraction and has a semi automated Learn To Rank (LTR) machine learning integration available.

The main search portlet UI is built on React and provides several result layouts out of the box, like raw document and explain views for debugging or relevancy improvement purposes. A configurable recommendations as well as a search bar mini portlets are also available.

While this application can be used with Liferay embedded Elasticsearch or any search engine supported by Liferay, it's designed to be used with the provided, custom Elasticsearch adapter which not only has an improved index configuration but highly increases it's configurability and possibilities.

The application is modular and has multiple extension points for customizations. Headless REST is also available.

![Liferay GSearch](https://github.com/peerkar/liferay-gsearch/raw/master/gsearch-doc/screenshots/gsearch.gif)

## News
* __(2019-10-09)__ [Liferay 7.2 version available](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/7.2/2019-10-09)
* __(2019-02-06)__ [REST API available](https://github.com/peerkar/liferay-gsearch/tree/master/binaries/7.1/2019-06-19)
* __(2019-01-29)__ [New Custom Elasticsearch adapters for 7.0 and 7.1 (CE and DXP) available](https://github.com/peerkar/liferay-gsearch/wiki/Changelog)

## Documentation

* [Installation](https://github.com/peerkar/liferay-gsearch/Installation)
* [Configuration](https://github.com/peerkar/liferay-gsearch/Configuration)
* [About the project](https://github.com/peerkar/liferay-gsearch/wiki/About)
* [Modules](https://github.com/peerkar/liferay-gsearch/wiki/Project-Modules)

## Disclaimer

This application hasn't been thoroughly tested, is to be considered experimental and is provided as is. If you have good development ideas, find bugs or need help in installaing, please leave ticket.
