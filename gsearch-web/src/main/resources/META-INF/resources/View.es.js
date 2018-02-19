import Component from 'metal-component/src/Component';
import Ajax from 'metal-ajax/src/Ajax';
import Soy from 'metal-soy/src/Soy';

import GSearchUtils from './js/GSearchUtils.es';
import GSearchQuery from './view-templates/GSearchQuery.es';
import GSearchField from './view-templates/GSearchField.es';
import GSearchFilters from './view-templates/GSearchFilters.es';
import GSearchFacets from './view-templates/GSearchFacets.es';
import GSearchHelp from './view-templates/GSearchHelp.es';
import GSearchPaging from './view-templates/GSearchPaging.es';
import GSearchQuerySuggestions from './view-templates/GSearchQuerySuggestions.soy';
import GSearchResults from './view-templates/GSearchResults.es';
import GSearchResultsLayouts from './view-templates/GSearchResultsLayouts.es';
import GSearchSort from './view-templates/GSearchSort.es';

import templates from './View.soy';

/**
 * View component.
 */
class View extends Component {

	constructor(opt_config) {
		
		super(opt_config);		
		
		this.debug = opt_config.JSDebugEnabled;

		this.initialQueryParameters = opt_config.initialQueryParameters; 

		this.searchResultsURL = opt_config.searchResultsURL;

		this.portletNamespace = opt_config.portletNamespace;
		
		this.queryMinLength = opt_config.queryMinLength;

		this.requestTimeout = opt_config.requestTimeout;

		// If this was linked call i.e. if keyword parameter was present in the calling url, 
		// then execute search. Notice that nested templates are processed before parent.
		
		if (this.initialQueryParameters['q']) {

			this.executeQuery();
			
		} else {

			// Reset possibly cached state
			
			this.query.parameters = new Object();
			
		}
	}

	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("View.attached()");
		}
	}
	
	/**
	 * Check parameters.
	 *
     * As asset type is here separated from other facet selections  
     * we check here selections not to get into deadlock kind of user 
     * experience.
	 *  
	 */
	checkParameters() {

		// Clear facet selections if asset type changes.
				
		let typeSelectionChanged = this.query.type != '' && 
				typeof this.query.parameters['type'] !== 'undefined' && 
					this.query.type != this.query.parameters['type']
		
		// Facet selection have to be invalidated also on keywords change. 
		
		let keywordsChanged = this.query.q != this.query.parameters['q']
			
		if (typeSelectionChanged || keywordsChanged) {

			let oldParameters = this.query.parameters;
			
			this.query.parameters = new Object();
			this.query.parameters['q'] = oldParameters['q'];
			this.query.parameters['type'] = oldParameters['type'];
			
			if (typeof oldParameters['scope'] !== 'undefined') {
				this.query.parameters['scope'] = oldParameters['scope'];
			}

			if (typeof oldParameters['time'] !== 'undefined') {
				this.query.parameters['time'] = oldParameters['time'];
			}

			if (typeof oldParameters['resultsLayout'] !== 'undefined') {
				this.query.parameters['resultsLayout'] = oldParameters['resultsLayout'];
			}

			if (typeof oldParameters['sortField'] !== 'undefined') {
				this.query.parameters['sortField'] = oldParameters['sortField'];
			}

			if (typeof oldParameters['sortDirection'] !== 'undefined') {
				this.query.parameters['sortDirection'] = oldParameters['sortDirection'];
			}

			// Persist current values
			
			this.query.type = oldParameters['type'];
			this.query.q = oldParameters['q'];

		} else if (typeof this.query.parameters['type'] !== 'undefined'){

			this.query.type = this.query.parameters['type'];
		}
	}
	
	/**
	 * @inheritDoc
	 */
	created() {
		
		if (this.debug) {
			console.log("View.created()");
		}

		// Create query object. 
		// Need to create the query object here for that to be available 
		// for the nested templates. Constructor of this template
		// will be run after child templates.
			
		this.query = new GSearchQuery();
	}
		
	/**
	 * Get query param
	 * 
	 * @param {String} key
	 */
	getQueryParam(key) {
		return this.query.parameters[key];
	}

	/**
	 * Set query param
	 * 
	 * @param {String} key
	 * @param {String} value
	 * @param {boolean} refresh Refresh results
	 */
	setQueryParam(key, value, refresh=true) {

		this.query.parameters[key] = value;
		
		if (refresh) {
			this.executeQuery();
		}
	}
	
	/**
	 * Execute query.
	 */
	executeQuery() {
		
		if (this.debug) {
			console.log("Executing query.");
		}

		// Check that there's a query 
		
		if (typeof this.query.parameters['q'] == 'undefined' ||
			this.query.parameters['q'].length < this.queryMinLength) {		
			
			return;
		}
		
		// Hide elements and show loader image.

		this.setLoading(true);

		// Check parameters
		
		this.checkParameters();
		
		// Build params.
		
		let params = this.query.buildQueryParams();
				
		Ajax.request(
				this.searchResultsURL,
				'GET',
				null,
				null,
				params,
				this.requestTimeout
		).then((response) => {

			if (response.responseText) {
				
				// Set results object.
				
				this.results =  JSON.parse(response.responseText);
														
				// Remove loading placeholder.

				this.setLoading(false);
				
				// Update browser address bar.

				this.updateAddressBar(this.query.buildAddressBarURL());
			} else {
				
				// Assume here simply that there was an error if response was empty.
				// Make better by sending proper response codes from backend etc.
				
				alert(Liferay.Language.get('there-was-an-error'));
			}
		}).catch(function(reason) {
			console.log(reason);
			alert(Liferay.Language.get('there-was-an-error'));
		});
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		if (this.debug) {
			console.log("View.rendered()");
		}
	}

	/**
	 * Show/hide ajax loader image.
	 * 
	 * @param {boolean} isLoading
	 */
	setLoading(isLoading) {

		let resultElement = $('#' + this.portletNamespace + 'SearchResults');
		
		if (isLoading) {
			resultElement.html('');
			resultElement.addClass('ajax-loader-placeholder');
		} else {
			resultElement.removeClass('ajax-loader-placeholder');
		}
	}
	
	/**
	 * Update address bar.
	 * 
	 * @param {address} key
	 */
	updateAddressBar(address) {
		if (window.history.pushState) {
			window.history.pushState(null, this.query.parameters['q'] + '-' + Liferay.Language.get('search'), address);
		} else {
			document.location.hash = address;
		}		
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
View.STATE = {
	query: {
		value: null
	}
};

// Register component

Soy.register(View, templates);

export default View;