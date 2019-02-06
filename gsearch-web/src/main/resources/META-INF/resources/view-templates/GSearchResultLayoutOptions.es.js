import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchResultLayoutOptions.soy';

/**
 * GSearch results layout component.
 */
class GSearchResultLayoutOptions extends Component {
	
	/**
	 * @inheritDoc
	 */
	attached() {

		if (this.debug) {
			console.log("GSearchResultLayoutOptions.attached()");
		}

		// Set initial query parameters from calling url.
		
		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.templateParameters, 
			this.setQueryParam
		);		
	}
		
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchResultLayoutOptions.rendered()");
		}
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(
			'ResultLayouts', 
			'optionmenu', 
			this
		);
	}
	
	/**
	 * @inheritDoc 
	 */
	shouldUpdate(changes, propsChanges) {

		if (this.debug) {
			console.log("GSearchResultLayouts.shouldUpdate()");
		}		

		$('#' + this.portletNamespace + 'ResultLayouts .optionmenu').remove();

		return true;
    }		
}
	
/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchResultLayoutOptions.STATE = {
	debug: {
		value: false
	},
	getQueryParam: {
		validator: core.isFunction
	},
	initialQueryParameters: {
		value: null
	},
	resultLayoutOptions: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	},
	templateParameters: {
		value: ['resultsLayout']
	}
};

// Register component

Soy.register(GSearchResultLayoutOptions, templates);

export default GSearchResultLayoutOptions;	