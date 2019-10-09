# Liferay GSearch Geolocation

Provides location data, a location clause condition handler and a query context contributor. Enables using Google Maps result layout in Liferay GSearch React portlet.

## Geolocation Clause Condition Handler

Example clause configuration using the `geolocation` handler. In the example condition below,  the `clauses` are applied only if user's located city is either (`any`) *Kuopio* or *Helsinki*. Effectively this clause configuration says: "if you are located in either Helsinki or Kuopio, contents having a tag 'Finland' will be boosted by 10.0":

```
{
  "description": "Example of using the geolocation clause condition.",
  "enabled": true,
  "conditions": [
    {
      "handler_name": "geolocation",
      "occur": "must",
      "configuration": {
        "match_property": "city",
        "match_type": "any",
        "match_values": [
          "Kuopio",
          "Helsinki",
          "Sibbo"
        ]
      }
    }
  ],
  "clauses": [
    {
      "query_type": "term",
      "occur": "should",
      "configuration": {
        "boost": 10.0,
        "field_name": "assetTagNames.raw",
        "query": "finland"
      }
    }
  ]
}
```

See `fi.soveltia.liferay.gsearch.geolocation.clause.condition.GeolocationClauseConditionHandler` for the implementation.

## Geolocation Configuration Variables

See `fi.soveltia.liferay.gsearch.geolocation.constants.GeolocationConfigurationVariables` for available variables.


Example configuration using the `$_geolocation.city_$` variable. Effectively the following configuration says: "contents having a tag for your city will be boosted by 10.0":

```
{
  "description": "Example of using a geolocation configuration variable.",
  "enabled": true,
  "conditions": [],
  "clauses": [
    {
      "query_type": "term",
      "occur": "should",
      "configuration": {
        "boost": 100.0,
        "field_name": "assetTagNames_$_context.language_id_$",
        "query": "$_geolocation.country_name_$"
      }
    }
  ]
}
```

## Requirements

IPStack API key is required (https://ipstack.com). Set the key in the configuration.

Sample IPStack API response:

```
{
  "ip": "134.201.250.155",
  "hostname": "134.201.250.155",
  "type": "ipv4",
  "continent_code": "NA",
  "continent_name": "North America",
  "country_code": "US",
  "country_name": "United States",
  "region_code": "CA",
  "region_name": "California",
  "city": "Los Angeles",
  "zip": "90013",
  "latitude": 34.0453,
  "longitude": -118.2413,
  "location": {
    "geoname_id": 5368361,
    "capital": "Washington D.C.",
    "languages": [
        {
          "code": "en",
          "name": "English",
          "native": "English"
        }
    ],
    "country_flag": "https://assets.ipstack.com/images/assets/flags_svg/us.svg",
    "country_flag_emoji": "🇺🇸",
    "country_flag_emoji_unicode": "U+1F1FA U+1F1F8",
    "calling_code": "1",
    "is_eu": false
  }
}
```

## Enabling Google Maps Result Layout

To enable the Maps result layout, you have to:

1. Have a geolocation field
2. Define the field name in the configuration
3. Have the maps result layout enabled in the GSearch React Portlet configuration (true by default)

The easy way to have a geolocation field is to use the Liferay custom fields. As an example, define a custom field named `gsearch_geolocation` for Web Content Article. Go now to Liferay GSearch configuration and in `Name of the location index field used for Maps result layout.` enter the generated index field name `expando__keyword__custom_fields__gsearch_geolocation_geolocation`. Now edit and save an article with and try Maps result layout with the GSearch search portlet. You should see your content on the map.

In case needed, check the name of the generated index field in the `Raw Document` result layout view of the GSearch search portlet. You can see the all the indexed fields.

## Configuration

After the module has been deployed succesfully, see the configuration options in `Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Geolocation`.

This feature is disabled by default.

## Changelog

(Major changes only)

### 2019-10-01 (Version 4.0.0)

* Upgrade to Liferay 7.2
* Variable parser support, allowing to use user's location data variables in the clauses at query time.