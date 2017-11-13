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

		this.debug = opt_config.JSDebugEnabled;

		this.initialQueryParameters = opt_config.initialQueryParameters; 
	}
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearchPaging.attached()");
		}
		
		// Set initial query parameters.
		
		if (this.initialQueryParameters['start']) {
			this.setQueryParam('start', this.initialQueryParameters['start'], false);
		}
	}
		
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchPaging.attached()");
		}

		// Set click events.
		
		this.setClickEvents();
	}

	/**
	 * Set click events
	 */
	setClickEvents() {

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
	setQueryParam: {
		validator: core.isFunction
	},
};

// Register component

Soy.register(GSearchPaging, templates);

export default GSearchPaging;