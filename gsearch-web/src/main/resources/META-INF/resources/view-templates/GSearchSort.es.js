import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchSort.soy';

/**
 * GSearch sort component.
 */
class GSearchSort extends Component {
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearchSort.attached()");
		}

		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.templateParameters, 
			this.setQueryParam
		);		
		
		// Setup options lists.
		
		GSearchUtils.bulkSetupOptionLists(
			'Sort', 
			'optionmenu', 
			this
		);		
	}

	created() {

		// Setup options.
		
		this.setupSortFieldOptions();

		// Hide initially
		
		this.visible = false;
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchSort.rendered()");
		}
	}
	
	/**
	 * Setup sortfield options.
	 */
	setupSortFieldOptions() {
			
		let html = '';
		
		let length = this.sortOptions.length;
		
		for (let i = 0; i < length; i++) {

			let item = this.sortOptions[i];
            			
			let itemClass = item.default ? 'selected default' : '';
				
			html += '<li class="' + itemClass + '">';
			html += '<a data-value="' + item.key + '" href="#">';
			html += '<span class="text">' + item.localization + '</span>';
			html += '</a></li>';
		}

		this.sortFieldOptionsMenu = html;
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchSort.STATE = {
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
	sortOptions: {
		value: null
	},
	sortFieldOptionsMenu: {
		value: null
	},
	templateParameters: {
		value: ['sortField','sortDirection']
	}
};

// Register component

Soy.register(GSearchSort, templates);

export default GSearchSort;