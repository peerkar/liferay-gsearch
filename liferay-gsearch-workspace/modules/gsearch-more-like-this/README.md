# Liferay GSearch More Like This

The Liferay GSearch More Like This -portlet is a recommender portlet, meant to show similar contents to the contents currently shown on Asset Publisher, Wiki or Blogs portlet.

The primary method for recommendations is the Elasticsearch More Like This query but as this portlet consumes the same configuration syntax as the Liferay GSearch core it has all its' options and for example configuration template variables available. In the default configuration for example, users' previous, successful keywords are used for recommending.

## Configuration

This portlet has  an instance configuration which you can access from portlet's option menu.

Default configuration is provided but notice that you have to save it once.

## Changelog

(Major changes only)

### 2019-10-01 (Version 4.0.0)

* Upgrade to 7.2
* Simplified configuration and backend logic. Moved UID resolving to the Recommender service module.

### 2019-06-13 (Version 3.0.0)

* Recommendations logic moved to a separate *gsearch-recommender* module.