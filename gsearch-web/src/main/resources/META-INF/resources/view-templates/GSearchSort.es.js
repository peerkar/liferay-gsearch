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
	}
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearchSort.attached()");
		}
		
		// Set initial query parameters.
		
		if (this.initialQueryParameters['sortDirection']) {
			this.setQueryParam('sortDirection', this.initialQueryParameters['sortDirection'], false);
		}

		if (this.initialQueryParameters['sortField']) {
			this.setQueryParam('sortField', this.initialQueryParameters['sortField'], false);
		}
		
		// Setup options lists.
		
		this.setupOptionLists();
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {

		if (this.debug) {
			console.log("GSearchSort.attached()");
		}
	}
	
	/**
	 * Setup option lists.
	 */
	setupOptionLists() {

		GSearchUtils.setupOptionList(
			this.portletNamespace + 'SortDirectionOptions', 
			this.portletNamespace + 'SortDirection', 
			this.getQueryParam, 
			this.setQueryParam, 
			'sortDirection'
		);
		
		GSearchUtils.setupOptionList(
			this.portletNamespace + 'SortFieldOptions', 
			this.portletNamespace + 'SortField', 
			this.getQueryParam, 
			this.setQueryParam, 
			'sortField'
		);
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
	}
};

// Register component

Soy.register(GSearchSort, templates);

export default GSearchSort;