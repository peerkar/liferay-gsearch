import Component from 'metal-component/src/Component';
import Ajax from 'metal-ajax/src/Ajax';
import Soy from 'metal-soy/src/Soy';

import GSearchQuery from './view-templates/GSearchQuery.es';
import GSearchField from './view-templates/GSearchField.es';
import GSearchHYMenus from './view-templates/GSearchHYMenus.es';
import GSearchPaging from './view-templates/GSearchPaging.es';
import GSearchResults from './view-templates/GSearchResults.es';
import GSearchResultLayoutOptions from './view-templates/GSearchResultLayoutOptions.es';

import templates from './GSearch.soy';

/**
 * View component.
 */
class GSearch extends Component {
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearch.attached()");
		}
				
		// This component is hidden initially to get all the subcomponents to render fully before shown.
		
		this.visible = true;

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

		let context = this;

		function onPopstate() {
			if (window.history.state != null) {
				let searchfield = $('#' + context.portletNamespace + 'SearchField');
				let searchbutton = $('#' + context.portletNamespace + 'SearchButton');
				if (searchfield.length && searchbutton.length) {
					searchfield.val(window.history.state);
					searchbutton.click();
				}
			} else {
				window.history.back();
			}
		}
		$(window).on('popstate', function(event) {
			window.setTimeout(onPopstate, 0);
		});

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
				
				let results =  JSON.parse(response.responseText);
														
				// Remove loading placeholder.

				this.setLoading(false);
				
				// Update browser address bar.

				this.updateAddressBar(this.query.buildAddressBarURL());
				
				// Run callbacks
				
				let length = this.resultsCallbacks.length;
				
				for (let i = 0; i < length; i++) {

					let f = this.resultsCallbacks[i];
									
					f(this.portletNamespace, results);
				}
				
				// Component updates
				

				this.components.resultsComponent.results = results;
				this.components.pagingComponent.paging = results.paging;
				
				if (results.items && results.items.length > 0) {
					this.components.hyComponent.results = results;
					this.components.resultLayoutOptionsComponent.resultLayoutOptions = results.resultLayoutOptions;
					$('#' + this.portletNamespace + 'Stats').show();
				} else {
					$('#' + this.portletNamespace + 'Stats').hide();
					this.components.resultLayoutOptionsComponent.resultLayoutOptions = null;
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
			console.log("GSearch.rendered()");
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
			let q = this.query.getParameterValue('q');
			if (window.history.state !== q) {
				window.history.pushState(q, this.query.getParameterValue('q') + '-' + Liferay.Language.get('search'), address);
			}
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
GSearch.STATE = {
	debug: {
		value: false
	},
	initialQueryParameters: {
		value: null
	},
	portletNamespace: {
		value: null
	},
	query: {
		value: null 
	},
	queryMinLength: {
		value: 3
	},
	requestTimeout: {
		value: 10000
	},
	resultsCallbacks: {
		value: [] 
	},
	searchResultsURL: {
		value:null
	}
};

// Register component

Soy.register(GSearch, templates);

export default GSearch;