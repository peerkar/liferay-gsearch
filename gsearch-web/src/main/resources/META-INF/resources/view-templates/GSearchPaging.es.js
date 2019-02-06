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
		
		$('#' + this.portletNamespace + 'Paging').find('span a').on('click', function(event) {

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
	
	/**
	 * @inheritDoc 
	 */
	shouldUpdate(changes, propsChanges) {

		if (this.debug) {
			console.log("GSearchPaging.shouldUpdate()");
		}		

		$('#' + this.portletNamespace + 'Paging .optionmenu').remove();

		return true;
    }		
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchPaging.STATE = {
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
	}
};

// Register component

Soy.register(GSearchPaging, templates);

export default GSearchPaging;
