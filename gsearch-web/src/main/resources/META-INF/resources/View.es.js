import Component from 'metal-component/src/Component';
import Ajax from 'metal-ajax/src/Ajax';
import Soy from 'metal-soy/src/Soy';

import GSearchUtils from './js/GSearchUtils.es';
import GSearchQuery from './view-templates/GSearchQuery.es';
import GSearchField from './view-templates/GSearchField.es';
import GSearchFilters from './view-templates/GSearchFilters.es';
import GSearchHelp from './view-templates/GSearchHelp.es';
import GSearchPaging from './view-templates/GSearchPaging.es';
import GSearchResults from './view-templates/GSearchResults.es';
import GSearchSort from './view-templates/GSearchSort.es';

import templates from './View.soy';

/**
 * View component.
 */
class View extends Component {

	constructor(opt_config, opt_parentElement) {
		
		super(opt_config, opt_parentElement);
		
		this.searchResultsURL = opt_config.searchResultsURL;

		this.portletNamespace = opt_config.portletNamespace;
		
		this.requestTimeout = opt_config.requestTimeout;

		// If keywords parameter was from the call in the child template then execute search.
		// Notice that nested templates are processed before parent.
		
		if (this.query.getKeywords() != '') {
			this.executeQuery();
		}
	}

	created() {

		// Create query object. 
		// Need to create the query object here to be available 
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
		return this.query[key];
	}

	/**
	 * Set query param
	 * 
	 * @param {String} key
	 * @param {String} value
	 * @param {boolean} refresh Refresh results
	 */
	setQueryParam(key, value, refresh=true) {
		this.query[key] = value;
		
		if (refresh) {
			this.executeQuery();
		}
	}
	
	/**
	 * Execute query.
	 */
	executeQuery() {
		
		// Validate keywords
		
		if (!this.query.validate()) {
			return;
		}

		// Hide elements and show loader image

		this.setLoading(true);

		// Build params
		
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
				
				// Set results object
				
				this.results =  JSON.parse(response.responseText);
				
				// Update stats
				
				if (this.results.items.length > 0) {
				
					this.stats =  Liferay.Language.get('search-stats')
						.replace('{currentPage}', this.results.paging.currentPage)
						.replace('{executionTime}', this.results.meta.executionTime)
						.replace('{totalHits}', this.results.meta.totalHits)
						.replace('{totalPages}', this.results.meta.totalPages);
				} else {
					
					this.stats = '';
				}
										
				// Remove loading placeholder

				this.setLoading(false);
				
				// Update browser address bar

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
	 * Update address bar
	 * 
	 * @param {address} key
	 */
	updateAddressBar(address) {
		if (window.history.pushState) {
			window.history.pushState(null, this.query.getKeywords() + '-' + Liferay.Language.get('search'), address);
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