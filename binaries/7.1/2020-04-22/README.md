# 2020-04-22

Liferay GSearch application binaries. This build has been tested with the following portal versions:

* DXP 7.1 fixpack 17

# Installation Instructions


## With the Custom Adapter

1. Shutdown portal
1. If exists, remove old adapter version JARS from LIFERAY_HOME/osgi/modules
1. If exists, remove the custom adapter bundle blacklist file from LIFERAY_HOME/osgi/configs. It's not needed anymore.
1. Copy the two adapter JARS from  *custom-elasticsearch-adapter/fp17* into LIFERAY_HOME/osgi/marketplace/override 
1. Do NOT include thefi.soveltia.liferay.gsearch.query-2.0.60.jar in the deployment (it's provided in the adapter)

## Without the Custom Adapter

1.If exists, remove the old custom application JARS
1.Copy all the JARs in LIFERAY_HOME/deploy
