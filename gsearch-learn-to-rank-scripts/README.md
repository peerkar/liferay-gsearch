# Liferay GSearch Learning to Rank Demo

Based on https://github.com/o19s/elasticsearch-learning-to-rank.

This is part of the Liferay GSearch LTR functionality, which consists of:

1. __Click tracking service:__ used to gather result ranking data (gsearch-click-tracking module)
1. __LTR Python scripts (here):__ used to update the judgement list and model to ES
1. __The custom LTR query type support in the Liferay GSearch core:__ allows to use SLTR queries as rescorers (example provided in the Rescorer configuration)

# Installation

1. Install the LTR Elasticsearch plugin as instructed [here at the project page](https://github.com/o19s/elasticsearch-learning-to-rank).
1. Install the `gsearch-click-tracking` module, if not yet installed.
1. Enable click tracking in the Liferay GSearch React Portlet settings, in `Control Panel -> System Settings -> Liferay GSearch`
1. For creating the judgements list i.e. running the Python scripts and grabbing the click data from the Click Tracking service, you need to setup Liferay credentials in `settings.cfg` file. There you can also define the click count threshold values for the judgment list rankings.
1. Modify the features to match your requirements. Features are defined in files 1-4.json

# GSearch Specific Scripts

* __gsearch.py__: All in one script. Generates judgement list (using the script below), trains the model and uploads it to ES. 
* __generate_gsearch_judgements.py__: generates the judgement list using the Liferay GSearch clicks tracking service

# Usage

After you've installed and configured elasticsearch, gather some judgement data with the click tracking service and update the model periodically by running scheduled the `python3 gsearch.py`.

Enable, configure and test SLTR clauses in the Liferay GSearch Rescorer configuration. An example is provided. Use Elasticsearch logging and Liferay GSearch portlet `Explain`result view for debugging.
