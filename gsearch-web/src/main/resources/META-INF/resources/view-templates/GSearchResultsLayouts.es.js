import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchResultsLayouts.soy';

/**
 * GSearch results layout component.
 */
class GSearchResultsLayout extends Component {
	
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
			console.log("GSearchResultsLayout.attached()");
		}

		// Set initial query parameters.
		
		if (this.initialQueryParameters['resultsLayout']) {
			this.setQueryParam('resultsLayout', this.initialQueryParameters['resultsLayout'], false);
		}

		// Setup options lists.
		
		this.setupOptionLists();
	}
	
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchResultsLayout.rendered()");
		}

		// Show image layout option if file format filter is "image".

		if (this.getQueryParam('type') == 'file' && this.getQueryParam('df') == 'image') {
			$('#' + this.portletNamespace + 'LayoutOptions .image-layout').removeClass('hide');
		} else {
			$('#' + this.portletNamespace + 'LayoutOptions .image-layout').addClass('hide');
		}
		
		// We might have a forced layout from results
		
		if (this.results) {
			this.setQueryParam('resultsLayout', this.results.meta.resultsLayout, false);
		}
	}
			
	/**
	 * Setup option lists.
	 */
	setupOptionLists() {
		
		GSearchUtils.setupOptionList(
			this.portletNamespace + 'LayoutOptions', 
			null, 
			this.getQueryParam, 
			this.setQueryParam, 
			'resultsLayout'
		);
	}	
}
	
/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchResultsLayout.STATE = {
	getQueryParam: {
		validator: core.isFunction
	},
	results: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	},
};

// Register component

Soy.register(GSearchResultsLayout, templates);

export default GSearchResultsLayout;	