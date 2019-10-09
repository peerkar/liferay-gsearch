# Liferay GSearch Open NLP

Exctracts metadata from users' queries making it available for a query clause conditions and clause configuration variables.

This plugin uses the natural language processing (NLP) provided by the *Elasticsearch OpenNLP Ingest Processor* project. When users sends a request, the keywords are processed by an Elasticsearch Ingestion endpoint and the extracted metadata put to query context. The metadata contains:

* locations
* persons
* dates

Be aware that this plugin has currently some limitations:
1. Capitalization: persons and locations in the search phrase need to be capitalized. For example "What happens in Helsinki" works but "What happens in helsinki" not.
1. English: persons identification works only for english names.

## Open NLP Clause Condition Handler and Configuration Variables

Example clause configuration using the `open_nlp` handler. 

```
{
  "description": "Example for using Open NLP condition handler and configuration variables: if 'Steve Jobs'  is extracted as persons data in the keyword, the clause is applied.",
  "enabled": true,
  "conditions": [
    {
      "handler_name": "open_nlp",
      "occur": "must",
      "configuration": {
        "match_property": "person",
        "match_type": "any",
        "match_values": [
          "Steve Jobs"
        ]
      }
    }
  ],
  "clauses": [
    {
      "query_type": "match",
      "occur": "should",
      "configuration": {
        "boost": 10.0,
        "field_name": "assetTagNames",
        "query": "$_opennlp.persons_$"
      }
    }
  ]
}
```

See the available configuration variables in `fi.soveltia.liferay.gsearch.opennlp.constants.OpenNlpConfigurationVariables` and the clause condition implementation in `fi.soveltia.liferay.gsearch.opennlp.query.clause.condition.OpenNlpClauseCondition`.

## Requirements

The *Elasticsearch OpenNLP Ingest Processor* Elasticsearch plugin needs to be installed. Follow the instruction at [https://github.com/spinscale/elasticsearch-ingest-opennlp](https://github.com/spinscale/elasticsearch-ingest-opennlp).

For creatin the Liferay GSearch pipeline, use this:

```
PUT _ingest/pipeline/opennlp-pipeline
{
  "description": "A pipeline to do named entity extraction",
  "processors": [
    {
      "opennlp" : {
        "field" : "gsearch_metadata"
      }
    }
  ]
}
```

## Configuration

After the module has been deployed succesfully, see the configuration options in `Control Panel -> Configuration -> System Settings -> Liferay GSearch -> Open NLP`.

This feature is disabled by default.

## Changelog

(Major changes only)

### 2019-10-01 (Version 1.0.0)

* Initial version