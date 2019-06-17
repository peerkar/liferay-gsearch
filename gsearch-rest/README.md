# Liferay GSearch REST API

Liferay GSearch REST API providing methods for keyword suggestions, search and recommendations:

* /recommendations/{languageId}
* /search/{languageId}/{keywords}
* /suggestions/{companyId}/{groupId}/{languageId}/{keywords}

See the available query parameters in the GSearchRestApplication source code.

Authentication is required. You can call the methods either as a logged in user or use OAuth 2.0 for authorization. See https://portal.liferay.dev/docs/7-1/tutorials/-/knowledge_base/t/jax-rs for more information about enabling the OAuth 2.0

## Known Issues

REST API cannot provide links for the results. This is a "feature" because Asset Renderers, which generally provide URLs for the assets, are highly dependent on PortletRequest object, not available in a REST call. 
There are ways to overcome this by customization but at the moment, you have take care of generating the links in your REST client, if using this API. entryClassPK and entryClassName fields are provided in the results.

## Requirements

The provided, custom Elasticsearch adapter is required for the recommender and suggestions.

## Changelog

### 2019-06-13 (Version 1.0)

* Initial release.