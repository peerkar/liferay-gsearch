import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import Ajax from 'metal-ajax/src/Ajax';
import MultiMap from 'metal-multimap/src/MultiMap';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchFacets.soy';

/**
 * GSearch facets component.
 */
class GSearchFacets extends Component {

	/**
	 * @inheritDoc
	 */
	constructor(opt_config, opt_parentElement) {

		super(opt_config, opt_parentElement);
		
		this.debug = opt_config.JSDebugEnabled;

		this.initialQueryParameters = opt_config.initialQueryParameters; 

		this.portletNamespace = opt_config.portletNamespace;
	}

	/**
	 * @inheritDoc
	 */
	attached() {

		if (this.debug) {
			console.log("GSearchFacets.attached()");
		}
		
		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(this.initialQueryParameters, this.templateParameters, this.setQueryParam);		
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchFacets.rendered()");
		}
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(this.portletNamespace + 'Facets', 'optionmenu', 
				this.getQueryParam, this.setQueryParam);
	}

	/**
	 * @inheritDoc 
	 */
	shouldUpdate(changes, propsChanges) {

		if (this.debug) {
			console.log("GSearchFacets.willUpdate()");
		}		

    	// Detach event listeners and facet element on rerender.

		GSearchUtils.bulkCleanUpOptionListEvents(this.portletNamespace + 'Facets', 'optionmenu');

		$('#' + this.portletNamespace + 'Facets').remove();

		return true;
    }	
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */
GSearchFacets.STATE = {
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
		value: ["extension", "ddmStructureKey", "fileEntryTypeId", "userName", "assetCategoryName", "assetTagNames"]
	}
};

// Register component

Soy.register(GSearchFacets, templates);

export default GSearchFacets;