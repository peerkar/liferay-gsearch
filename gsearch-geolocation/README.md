# Liferay GSearch Geolocation Query Contributor

This is the GSearch geolocation query contibutor module.

With this feature you can boost documents which smaller geodistance. 

# How it works

when saving a portal asset, the indexer postprocessor extracts current user's geolocation data and stores it to index. When searching this plugin can be used to boost documents having smaller geodistance to the user doing the search.

## Configuration

After the module has been deployed succesfully, please see the configuration options in Control Panel -> Configuration -> System Settings -> Other -> GSearch Geolocation


