import requests
from utils import elastic_connection, INDEX_NAME, JUDGMENTS_FILE, LIFERAY_USERNAME,LIFERAY_PASSWORD

#
# The base query for finding the docId for an entryClassPK.
#
entryClassPKQuery = {
  "query": {
      "term": {
          "entryClassPK": "test",
       }
   }
}


#
# Tries to find a docID for an entryClassPK.
#
def _find_doc_id (es_connection, entry_class_pk):
	entryClassPKQuery['query']['term']['entryClassPK'] = entry_class_pk
	res = es_connection.search(index=INDEX_NAME, body=entryClassPKQuery)
	if res is not None:
		doc_id = res['hits']['hits'][0]['_id']
		return doc_id
	else: 
		return None


#
# Writes judgements file.
#
def _write_file(es_connection, data):

	keywords_list = []
	queries = ""
	judgements = ""
	
	with open(JUDGMENTS_FILE, 'w') as judgmentFile:
		for record in data:
			entry_class_pk = record['entryClassPK']
			doc_id = _find_doc_id(es_connection, entry_class_pk)
			
			qid = "qid:1"
			
			if doc_id is not None:
				grade = 1
				keywords = record['keywords']
				if keywords not in keywords_list:
					index = len(keywords_list)
					qid = "qid:%s" % (index+1)
				
					keywords_list.append(keywords)
					#
					# Create keyword entry. Example:
					#
					# # qid:1: rambo
					#
					queries += "# %s:\t%s\n" % (qid, keywords)
									
				
				# 4	qid:1 #	7555	Rambo
				judgements += "%s\t%s\t # %s#\n"% (grade, qid, doc_id)
				
		judgmentFile.write(queries)
		judgmentFile.write(judgements)


#
# Gets Clicks data from Liferay.
#
def _get_json():
	data = {
	  'start': '0',
	  'end': '100'
	}
	response = requests.post('http://localhost:8080/api/jsonws/gsearchclicktracking.clicks/get-clicks', data=data, auth=('test@liferay.com', 'test'))
	return response.json()


#
# Generate Liferay GSearch judgements.
#
def generate_gsearch_judgements():
	es_connection = elastic_connection()
	data = _get_json()
	_write_file(es_connection,data)

if __name__ == "__main__":
	generate_gsearch_judgements()

