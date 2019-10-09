# Liferay GSearch Weather

Provides a weather clause condition handler and a query context contributor.

## Weather Clause Condition Handler

Compares users' weather condition attributes in the query context to the given value(s). See  `fi.soveltia.liferay.gsearch.weather.constants.WeatherClauseConditionProperties` for possible match properties. See `fi.soveltia.liferay.gsearch.weather.constants.Weather` for possible match values for temperature.

Example clause configuration using the `weather` handler:

```
{
  "description": "Example of using weather clause condition and configuration variables.",
  "enabled": true,
  "conditions": [
    {
      "handler_name": "weather",
      "occur": "must",
      "configuration": {
        "match_property": "weather",
        "match_type": "any",
        "match_values": [
          "clear"
        ]
      }
    }
  ],
  "clauses": [
    {
      "query_type": "term",
      "occur": "should",
      "configuration": {
        "boost": "10.0",
        "field_name": "assetTagNames.raw",
        "query": "clear_weather_clothes"
      }
    }
  ]
}
```

## Weather Configuration Variables

See `fi.soveltia.liferay.gsearch.weather.constants.WeatherConfigurationVariables` for available variables.

Example configuration using the `$_weather.weather_$` variable:

```
{
  "description": "Example of using weather clause condition and configuration variables.",
  "enabled": true,
  "conditions": [],
  "clauses": [
    {
      "query_type": "term",
      "occur": "should",
      "configuration": {
        "boost": "10.0",
        "field_name": "assetTagNames.raw",
        "query": "$_weather.weather_$"
      }
    }
  ]
}
```

## Requirements

`gsearch-geolocation` module and [OpenWeatherMap](https://openweathermap.org/api) API key are required.

Sample API response: [https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139](https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139)

```
{"coord": { "lon": 139,"lat": 35},
  "weather": [
    {
      "id": 800,
      "main": "Clear",
      "description": "clear sky",
      "icon": "01n"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 289.92,
    "pressure": 1009,
    "humidity": 92,
    "temp_min": 288.71,
    "temp_max": 290.93
  },
  "wind": {
    "speed": 0.47,
    "deg": 107.538
  },
  "clouds": {
    "all": 2
  },
  "dt": 1560350192,
  "sys": {
    "type": 3,
    "id": 2019346,
    "message": 0.0065,
    "country": "JP",
    "sunrise": 1560281377,
    "sunset": 1560333478
  },
  "timezone": 32400,
  "id": 1851632,
  "name": "Shuzenji",
  "cod": 200
}
```

## Configuration

After the module has been deployed succesfully, see the configuration options in `Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Weather`.

Be aware that IP doesn't resolve when running on localhost. When testing, use the test IP address in the configuration.

This feature is disabled by default.

## Changelog

(Major changes only)

### 2019-10-01 (Version 1.0.0)

* Initial version