# Adapter Installation



## Step 1 - Voikko

The adapter is configured to use Voikko libraries from the following locations:

```
	"libraryPath": "/usr/lib/x86_64-linux-gnu",
	"dictionaryPath": "/opt/software/voikko/dictionary"
```
Before installing, ensure that this configuration is valid

## Step 2 - Synonym Dictionaries

Copy folder gsearch-synonyms to every active Elasticsearch server node under the *config* folder.

## Step 3 - Start Elasticsearch 

Ensure that Elasticsearch server starts with Voikko env variables. Example from command line:

```
	ES_JAVA_OPTS="-Djava.security.policy=file:/opt/software/voikko/voikko.policy 	-Xmx4096m" ./bin/elasticsearch
```
## Step 3 - Deploy

Remove the old adapter modules and deploy the 2 new ones:

* com.liferay.portal.search.elasticsearch6.GSEARCH-PATCHED-API-70-DXP-FP60.jar

* com.liferay.portal.search.elasticsearch6.GSEARCH-PATCHED-IMPL-70-DXP-FP60.jar

Deploy and check that adapter installed properly

## Step 4 - Test Voikko

Test as instructed on https://github.com/EvidentSolutions/elasticsearch-analysis-voikko

## Step 5 - Reindex

Reindex from Liferay control panel and check log for any errors.

# Updating Synonym Dictionaries

1. Edit the synonym files as required
1. Close and open the index to refresh the analyzers. This can be done directly from cURL or Kibana. From Kibana console run (replace the index name with the right one):

```
	POST /liferay-20099/_close
	POST /liferay-20099/_open
```

For more information about the synonym filter, see Elasticsearch documentation at https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-synonym-tokenfilter.html 



