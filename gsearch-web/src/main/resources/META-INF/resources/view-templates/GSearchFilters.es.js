import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import Ajax from 'metal-ajax/src/Ajax';
import MultiMap from 'metal-multimap/src/MultiMap';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchFilters.soy';

/**
 * GSearch filters component.
 */
class GSearchFilters extends Component {

	/**
	 * @inheritDoc
	 */
	constructor(opt_config, opt_parentElement) {

		super(opt_config, opt_parentElement);
		
		this.debug = opt_config.JSDebugEnabled;

		this.initialQueryParameters = opt_config.initialQueryParameters; 

		this.portletNamespace = opt_config.portletNamespace;

		this.assetTypeOptions = opt_config.assetTypeOptions;
	}

	/**
	 * @inheritDoc
	 */
	attached() {

		if (this.debug) {
			console.log("GSearchFilters.attached()");
		}
		
		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(this.initialQueryParameters, this.templateParameters, this.setQueryParam);		
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchFilters.rendered()");
		}
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(this.portletNamespace + 'BasicFilters', 'optionmenu', 
				this.getQueryParam, this.setQueryParam);

		// Update asset type facet counts.
		
		this.updateAssetTypeFacetCounts();
	}
		
	/**
	 * Update asset type facet counts. 
	 */
	updateAssetTypeFacetCounts() {		
		
		if (this.results && this.results.facets) {
			
			let entryClassNameFacets = null;
			
			let length = this.results.facets.length;

			for (let i = 0; i < length; i++) {

				if(this.results.facets[i].fieldName == 'entryClassName') {
					entryClassNameFacets = this.results.facets[i].values;
					break;
				}
			}
			
			if (entryClassNameFacets) {

				let valueCount = entryClassNameFacets.length;
				
				for (let i = 0; i < valueCount; i++) {

					let term =  entryClassNameFacets[i].term;
					let frequency =  entryClassNameFacets[i].frequency;
					let element = $('#' + this.portletNamespace + 'TypeFilterOptions li a[data-facet="' + term + '"]');
				
					if (element) {
						$(element).find('.count').html('(' + frequency + ')');
					}
				}
			} else {
				$('#' + this.portletNamespace + 'TypeFilterOptions li .count').html('');
			}
		}			
	}
	
	/**
	 * @inheritDoc 
	 */
	shouldUpdate(changes, propsChanges) {

		if (this.debug) {
			console.log("GSearchFilters.willUpdate()");
		}		

    	// Detach event listeners and facet element on rerender.

		GSearchUtils.bulkCleanUpOptionListEvents(this.portletNamespace + 'BasicFilters', 'optionmenu');

		return true;
    }	
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */
GSearchFilters.STATE = {
	getQueryParam: {
		validator: core.isFunction
	},
	results: {
		value: null
	},	
	setQueryParam: {
		validator: core.isFunction
	},
	templateParameters: {
		value: ['type','scope','time']
	}	
};

// Register component

Soy.register(GSearchFilters, templates);

export default GSearchFilters;