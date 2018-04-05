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
		
		// Setup asset type options
		
		this.setupAssetTypeOptions()
		
		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.templateParameters, 
			this.setQueryParam
		);	
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(
			this.portletNamespace + 'BasicFilters', 
			'optionmenu', 
			this.getQueryParam, 
			this.setQueryParam
		);
		
		// Add results callback
		
		this.addResultsCallback(this.updateAssetTypeFacetCounts);
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchFilters.rendered()");
		}
	}
	
	/**
	 * Setup asset type options
	 */
	setupAssetTypeOptions() {
		
		let html = '';

		for (let item of this.assetTypeOptions) {
			
			html += '<li><a data-facet="' + item.entryClassName + '" data-value="' + item.key + '" href="#">';
			html += '<span class="text">' + item.localization + '</span>';
			html += '<span class="count"></span>';
			html += '</a></li>';
		}
		$('#' + this.portletNamespace + 'TypeFilterOptions').append(html);
	}
	
	/**
	 * Update asset type facet counts. 
	 */
	updateAssetTypeFacetCounts(portletNamespace, results) {		

		// Clear current values
		
		$('#' + portletNamespace + 'TypeFilterOptions li .count').html('');
		
		if (results && results.facets) {
			
			let entryClassNameFacets = null;
			
			let length = results.facets.length;

			for (let i = 0; i < length; i++) {

				if(results.facets[i].paramName == 'entryClassName') {
					entryClassNameFacets = results.facets[i].values;
					break;
				}
			}
			
			if (entryClassNameFacets) {

				let valueCount = entryClassNameFacets.length;
				
				for (let i = 0; i < valueCount; i++) {

					let term =  entryClassNameFacets[i].term;
					let frequency =  entryClassNameFacets[i].frequency;
					let element = $('#' + portletNamespace + 'TypeFilterOptions li a[data-facet="' + term + '"]');
				
					if (element) {
						$(element).find('.count').html('(' + frequency + ')');
					}
				}
			}
		}			
	}
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */
GSearchFilters.STATE = {
	addResultsCallback: {
		validator: core.isFunction
	},
	getQueryParam: {
		validator: core.isFunction
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