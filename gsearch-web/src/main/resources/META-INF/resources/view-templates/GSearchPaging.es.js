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
		
		this.portletNamespace = opt_config.portletNamespace;
	}
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearchPaging.attached()");
		}
		
		// Set initial query parameters.
		
		let start = this.initialQueryParameters['start'];
		
		if (start && start.length > 0) {
			this.setQueryParam('start', start[0], false, false);
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
			
			if (_self.debug) {
				console.log("Going to page " + value);
			}
			
			if (value != _self.getQueryParam('start' , true)) {

				// Scroll to the top. Trying to find browser specific scroll top
				// to avoid firing callback twice like with $('html, body');
				
				let scrollTo = $('.gsearch-portlet').offset().top;
				
			    if ($('html').scrollTop()) {
			        $('html').stop().animate({ 
			        	scrollTop: scrollTo 
			        }, 400, 'swing', function() { 
						_self.setQueryParam('start', value, true, false);
			        });
			    } else if ($('body').scrollTop()) {
			        $('body').stop().animate({
			        	scrollTop: scrollTo 
			        }, 400, 'swing', function() { 
						_self.setQueryParam('start', value, true, false);
			        });
			    } else {
					_self.setQueryParam('start', value, true, false);
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
