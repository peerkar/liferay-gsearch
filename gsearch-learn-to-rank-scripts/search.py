from log_conf import Logger

baseQuery = {
  "query": {
      "multi_match": {
          "query": "test",
          "fields": ["title_en_US", "content_en_US"]
       }
   },
  "rescore": {
      "query": {
        "rescore_query": {
            "sltr": {
                "params": {
                    "keywords": ""
                },
                "model": "",
            }
         }
      }
   }
}


def ltr_query(keywords, model_name):
    import json
    baseQuery['rescore']['query']['rescore_query']['sltr']['model'] = model_name
    baseQuery['query']['multi_match']['query'] = keywords
    baseQuery['rescore']['query']['rescore_query']['sltr']['params']['keywords'] = keywords
    Logger.logger.info("%s" % json.dumps(baseQuery))
    return baseQuery


if __name__ == "__main__":
    from sys import argv
    from utils import elastic_connection, INDEX_NAME

    es = elastic_connection(timeout=1000)
    model = "gsearch_model_6"
    if len(argv) > 2:
        model = argv[2]
    results = es.search(index=INDEX_NAME, doc_type='LiferayDocumentType', body=ltr_query(argv[1], model))
    for result in results['hits']['hits']:
        Logger.logger.info(result['_source']['title_en_US'])

