# Liferay GSearch Result Item Tagger

Allows to dynamically set extra properties to result items. This can be useful for adding properties to result item based on their index document attributes. These properties can be used for example for styling.

## Configuration

After the module has been deployed succesfully, see the configuration options in `Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Result Item Tagger`.

This feature is disabled by default.

### Example Configuration

The following example adds a property `official_content = true` to result items having an assetTag `official`. As the `assetTagNames` is a multivalued field, we are setting `match_field_multi_valued: true`.

The example below is implemented in the UI of Liferay GSearch React portlet. Result items having the  property "official_content" are "tagged" with a green icon on the UI:

```
{
	description: "Adds a property 'official_content=true' to result items having a tag 'official'",
	match_field_name: "assetTagNames",
	match_field_multi_valued: true,
	match_value: "official",
	tag_property_name: "official_content",
	tag_property_value: true
}
```

## Changelog

(Major changes only)

### 2019-10-01 (Version 2.0.0)

* Upgrade to Liferay 7.2
* Switched to rule based configuration