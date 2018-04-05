import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchResultsLayouts.soy';

/**
 * GSearch results layout component.
 */
class GSearchResultsLayouts extends Component {
	
	/**
	 * @inheritDoc
	 * 
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
			console.log("GSearchResultsLayouts.attached()");
		}

		// Set initial query parameters from calling url.
		
		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.templateParameters, 
			this.setQueryParam
		);		
		
		// Setup options lists.

		GSearchUtils.setupOptionList(
			this.portletNamespace + 'LayoutOptions', 
			null, 
			this.getQueryParam, 
			this.setQueryParam, 
			'resultsLayout',
			false
		);
	}

	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchResultsLayout.rendered()");
		}
	}
}
	
/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchResultsLayouts.STATE = {
	getQueryParam: {
		validator: core.isFunction
	},
	templateParameters: {
		value: ['resultsLayout']
	},
	setQueryParam: {
		validator: core.isFunction
	}
};

// Register component

Soy.register(GSearchResultsLayouts, templates);

export default GSearchResultsLayouts;	