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

		let menuElement = $(this.element.querySelector('#' + this.portletNamespace + 'LayoutOptions'));
		
		GSearchUtils.setupOptionList(
			this,
			menuElement,
			'resultsLayout',
			false
		);			
	}
	
	/**
	 * @inheritDoc
	 */
	created() {

		// Hide initially
		
		this.visible = false;
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchResultsLayout.rendered()");
		}
	}
	
	setResultLayoutOptions(results) {
		
		// Show image layout option if type filter is "file" or extension is "image".
		
		if (this.getQueryParam('type', true) == 'file' || this.getQueryParam('extension', true) == 'Image') {
			
			$('#' + this.portletNamespace + 'LayoutOptions .image-layout').removeClass('hide');
		} else {

			$('#' + this.portletNamespace + 'LayoutOptions .image-layout').addClass('hide');
		}
		
		// We might have a forced layout from results
		
		if (results.meta.resultsLayout) {
			this.setQueryParam('resultsLayout', results.meta.resultsLayout, false, false);
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
		value: ['resultsLayout']
	}
};

// Register component

Soy.register(GSearchResultsLayouts, templates);

export default GSearchResultsLayouts;	