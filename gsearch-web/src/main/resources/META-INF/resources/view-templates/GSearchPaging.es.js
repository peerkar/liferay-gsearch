import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import templates from './GSearchPaging.soy';

/**
 * GSearch paging component.
 */
class GSearchPaging extends Component {

	/**
	 * @inheritDoc
	 */
	constructor(opt_config, opt_parentElement) {

		super(opt_config, opt_parentElement);
	}
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		// Check if we are getting selections from initially calling URL.
		
		this.checkCallURLSelections();
		
	}
	
	/**
	 * Check and set selected items based on calling URL.
	 */
	checkCallURLSelections() {
		
		if (!this.initialURLParameters) {
			return;
		}

		let start = this.initialURLParameters['start'];

		if (start) {
			this.setQueryParam('start', start, false);
		}
	}	
	
	rendered() {
		
		// Set page click events

		let _self = this;
		
		let element = $('#' + this.portletNamespace + 'Paging');
		
		element.find('span a').on('click', function(event) {

			let value = $(this).attr('data-value');
			
			if (value != _self.getQueryParam('start')) {
				_self.setQueryParam('start', value);
			}
			
			event.preventDefault();
		});			
		
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchPaging.STATE = {
	getQueryParam: {
		validator: core.isFunction
	},
	initialURLParameters: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	},
	results: {
		value: null
	}
};

// Register component

Soy.register(GSearchPaging, templates);

export default GSearchPaging;