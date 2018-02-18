import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import GSearchUtils from '../js/GSearchUtils.es';

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
		
		let paramValue = this.initialQueryParameters['start'];
		
		if (paramValue) {
			this.setQueryParam('start', paramValue, false);
		}
	}
		
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchPaging.rendered()");
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

				// Scroll to the top

				var body = $('html, body');
			
				body.stop().animate({
					scrollTop: ($('.gsearch-portlet').offset().top)
				}, 400, 'swing', function() { 
					_self.setQueryParam('start', value);
				});			
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
	}
};

// Register component

Soy.register(GSearchPaging, templates);

export default GSearchPaging;
