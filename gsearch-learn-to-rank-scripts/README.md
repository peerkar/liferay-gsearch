# Liferay GSearch Learning to Rank Demo

Based on https://github.com/o19s/elasticsearch-learning-to-rank.

This is part of the Liferay GSearch LTR functionality, which consists of:

1. __Click tracking service:__ used to gather result ranking data (gsearch-click-tracking module)
1. __LTR Python scripts (here):__ used to update the judgement list and model to ES
1. __The custom LTR query type support in the Liferay GSearch core:__ allows to use SLTR queries as rescorers (example provided in the Rescorer configuration)

# Installation

For general installation instructions, see the [plugin project page](https://github.com/o19s/elasticsearch-learning-to-rank) 

For the Liferay GSearch part you need:

* Liferay credentials to access the clicks tracking service via web service. See `settings.cfg` file.

# GSearch Specific Scripts

* __gsearch.py__: All in one script. Generates judgement list (using the script below), trains the model and uploads it to ES. 
* __generate_gsearch_judgements.py__: generates the judgement list using the Liferay GSearch clicks tracking service

# Usage

After you've installed and configured elasticsearch, gather some judgement data with the click tracking service and update the model periodically by running scheduled the `python3 gsearch.py`.

Enable and configure SLTR clauses in the Liferay GSearch Rescorer configuration. An example is provided.
