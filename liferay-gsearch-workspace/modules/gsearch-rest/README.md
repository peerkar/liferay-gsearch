# Liferay GSearch REST API

Liferay GSearch REST API providing methods for keyword suggestions, search and recommendations:

* /recommendations/{languageId}
* /search/{languageId}/{keywords}
* /suggestions/{companyId}/{groupId}/{languageId}/{keywords}

Example: `http://localhost:8080/o/gsearch-rest/search/en_US/lorem`

See the available query parameters in the GSearchRestApplication class source code.

Authentication is required for the calls. For using OAuth 2.0 for authorization, see [https://portal.liferay.dev/docs/7-1/tutorials/-/knowledge_base/t/jax-rs](https://portal.liferay.dev/docs/7-1/tutorials/-/knowledge_base/t/jax-rs) for more information about enabling the OAuth 2.0.


## Known Issues

REST API doesn't have all the features of pure Java API and cannot for example, provide links for the results. This is a "feature" because Asset Renderers, which generally provide URLs for the assets, are dependent on PortletRequest object not available in REST calls. Among the possible ways to overcome this are to index a link url and include that in search result items or generating the links in your REST client. entryClassPK and entryClassName fields are provided in the results for this purpose.

## Changelog

(Major changes only)

### 2019-06-13 (Version 3.0.0)

* Upgrade to 7.2

### 2019-06-13 (Version 1.0.0)

* Initial release.