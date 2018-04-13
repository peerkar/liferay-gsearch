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
		
		let q = this.initialQueryParameters['q'];

		if (q && q.length > 0) {

			this.query.isInitialQuery = true;
			this.executeQuery();
			
		} else {

			// Reset possibly cached state
			
			this.query.oldParameters = [];
			this.query.parameters = [];
			
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
	 * Add a callback function to be called after results
	 * retrieval.
	 */
	addResultsCallback(func) {
		this.resultsCallbacks.push(func);
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
	 * @param {Boolean} singleValue
	 * 
	 */
	getQueryParam(key, singleValue) {
		
		if (singleValue) {
			return this.query.getParameterValue(key);
		} else {
			return this.query.getParameterValues(key);
		}
	}

	/**
	 * Set query param
	 * 
	 * @param {String} key
	 * @param {String} value
	 * @param {Boolean} refresh
	 * @param {Boolean} isMultiValued 
	 * @param {String} previousValue 
	 */
	setQueryParam(key, value, refresh=true, isMultiValued=true, previousValue=null) {

		if (!isMultiValued) {

			this.query.setParameter(key, value);

		} else {

			this.query.setMultiValueParameter(key, value, previousValue);
		}
				
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
		
		let q = this.query.getParameterValue('q');
		
		if (!q || q.length < this.queryMinLength) {		
			
			return;
		}
		
		// Hide elements and show loader image.

		this.setLoading(true);

		// Clean filter parameters on subsequent queries
		
		this.query.cleanFilterParameters();
		
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
				
				// Set result layout options
				
				this.setResultLayoutOptions();

				// Run callbacks
				
				let length = this.resultsCallbacks.length;
				
				for (let i = 0; i < length; i++) {

					let f = this.resultsCallbacks[i];
									
					f(this.portletNamespace, this.results);
				}
				
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
	 * @inheritDoc
	 */
	setResultLayoutOptions() {
		
		// Show image layout option if type filter is "file" or extension is "image".
		
		if (this.getQueryParam('type', true) == 'file' || this.getQueryParam('extension', true) == 'Image') {
			
			$('#' + this.portletNamespace + 'LayoutOptions .image-layout').removeClass('hide');
		} else {

			$('#' + this.portletNamespace + 'LayoutOptions .image-layout').addClass('hide');
		}
		
		// We might have a forced layout from results
		
		if (this.results) {
			this.setQueryParam('resultsLayout', this.results.meta.resultsLayout, false, false);
		}
	}
	
	/**
	 * Update address bar.
	 * 
	 * @param {address} key
	 */
	updateAddressBar(address) {
		if (window.history.pushState) {
			window.history.pushState(null, this.query.getParameterValue('q') + '-' + Liferay.Language.get('search'), address);
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
	},
	resultsCallbacks: {
		value: [] 
	}
};

// Register component

Soy.register(View, templates);

export default View;