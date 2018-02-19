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

			event.preventDefault();

			let value = $(this).attr('data-value');
			
			if (this.debug) {
				console.log("Going to page " + value);
			}
			
			if (value != _self.getQueryParam('start')) {

				// Scroll to the top. Trying to find browser specific scroll top
				// to avoid firing callback twice like with $('html, body');
				
				let scrollTo = $('.gsearch-portlet').offset().top;
				
			    if ($('html').scrollTop()) {
			        $('html').stop().animate({ 
			        	scrollTop: scrollTo 
			        }, 400, 'swing', function() { 
						_self.setQueryParam('start', value);
			        });
			    } else if ($('body').scrollTop()) {
			        $('body').stop().animate({
			        	scrollTop: scrollTo 
			        }, 400, 'swing', function() { 
						_self.setQueryParam('start', value);
			        });
			    }
			}
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
