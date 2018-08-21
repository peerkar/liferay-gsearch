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
	attached() {

		if (this.debug) {
			console.log("GSearchFilters.attached()");
		}
		
		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.templateParameters, 
			this.setQueryParam
		);	
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(
			'BasicFilters', 
			'optionmenu', 
			this
		);
		
		// Add results callback
		
		this.addResultsCallback(this.updateAssetTypeFacetCounts);
	}
	
	created() {
		
		// Setup asset type options 
		
		this.setupAssetTypeOptions()
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
		
		let length = this.assetTypeOptions.length;
		
		for (let i = 0; i < length; i++) {

			let item = this.assetTypeOptions[i];

			html += '<li><a data-facet="' + item.entryClassName + '" data-value="' + item.key + '" href="#">';
			html += '<span class="text">' + item.localization + '</span>';
			html += '<span class="count"></span>';
			html += '</a></li>';
		}
		this.typeOptionsMenu = html;
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
	assetTypeOptions: {
		internal: true,
		value: null
	},
	debug: {
		value: false
	},
	getQueryParam: {
		validator: core.isFunction
	},	
	initialQueryParameters: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	},
	templateParameters: {
		value: ['type','scope','time']
	},	
	typeOptionsMenu: {
		internal: true,
		value: null
	}
};

// Register component

Soy.register(GSearchFilters, templates);

export default GSearchFilters;