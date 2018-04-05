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
	constructor(opt_config, opt_parentElement) {

		super(opt_config, opt_parentElement);
		
		this.debug = opt_config.JSDebugEnabled;

		this.initialQueryParameters = opt_config.initialQueryParameters; 

		this.portletNamespace = opt_config.portletNamespace;

		this.sortOptions = opt_config.sortOptions;
	}
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearchSort.attached()");
		}

		// Setup options.
		
		this.setupSortFieldOptions();
		
		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.templateParameters, 
			this.setQueryParam
		);		

		// Setup options lists.
		
		GSearchUtils.bulkSetupOptionLists(
			this.portletNamespace + 'Sort', 
			'optionmenu', 
			this.getQueryParam, 
			this.setQueryParam
		);
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

		for (let item of this.sortOptions) {
			
			let itemClass = item.default ? 'selected default' : '';
				
			html += '<li class="' + itemClass + '">';
			html += '<a data-value="' + item.key + '" href="#">';
			html += '<span class="text">' + item.localization + '</span>';
			html += '</a></li>';
		}

		$('#' + this.portletNamespace + 'SortFieldOptions').html(html);			
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchSort.STATE = {
	getQueryParam: {
		validator: core.isFunction
	},
	setQueryParam: {
		validator: core.isFunction
	},
	sortOptions: {
		value: null
	},
	templateParameters: {
		value: ['sortField','sortDirection']
	}
};

// Register component

Soy.register(GSearchSort, templates);

export default GSearchSort;