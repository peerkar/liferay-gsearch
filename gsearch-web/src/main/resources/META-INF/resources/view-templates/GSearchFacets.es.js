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
	attached() {

		if (this.debug) {
			console.log("GSearchFacets.attached()");
		}
		
		// Set initial query parameters from calling url.
		
		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.facetFields, 
			this.setQueryParam
		);		
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchFacets.rendered()");
		}
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(
			'Facets', 
			'optionmenu', 
			this,
			true
		);
	}
	
	/**
	 * @inheritDoc 
	 */
	shouldUpdate(changes, propsChanges) {

		if (this.debug) {
			console.log("GSearchFacets.shouldUpdate()");
		}		

		$('#' + this.portletNamespace + 'Facets .optionmenu').remove();

		return true;
    }		
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */
GSearchFacets.STATE = {
	debug: {
		value: false
	},
	facetFields: {
		value: null
	},
	getQueryParam: {
		validator: core.isFunction
	},
	initialQueryParameters: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	}
};

// Register component

Soy.register(GSearchFacets, templates);

export default GSearchFacets;