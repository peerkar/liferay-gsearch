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

		this.portletNamespace = opt_config.portletNamespace;
	}
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		// Check if we are getting selections from initially calling URL.
		
		this.checkCallURLSelections();
		
		
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {

		// Set dropdown selections
			
		this.setDropDownSelectedItems();
		
		// Set click events
		
		this.setDropDownClickEvents();
	}
	
	/**
	 * Check and set selected items based on calling URL.
	 */
	checkCallURLSelections() {

		if (!this.initialURLParameters) {
			return;
		}
		
		let sortDirection = this.initialURLParameters['sortDirection'];
		let sortField = this.initialURLParameters['sortField'];

		if (sortDirection) {
			if (GSearchUtils.setInitialOption(this.portletNamespace + 'SortDirectionOptions', sortDirection)) {
				this.setQueryParam('sortDirection', sortDirection, false);
			}
		} 
		
		if (sortField) {
			if (GSearchUtils.setInitialOption(this.portletNamespace + 'SortFieldOptions', sortField)) {
				this.setQueryParam('sortField', sortField, false);
			}
		}
	}
		
	/**
	 * Set dropdown click events.
	 */
	setDropDownClickEvents() {
		
		GSearchUtils.setDropDownClickEvents(this.portletNamespace + 'SortDirectionOptions', 
				this.portletNamespace + 'SortDirection', this.getQueryParam, 
				this.setQueryParam, 'sortDirection');
		GSearchUtils.setDropDownClickEvents(this.portletNamespace + 'SortFieldOptions', 
				this.portletNamespace + 'SortField', this.getQueryParam, 
				this.setQueryParam, 'sortField');
	}

	/**
	 * Set dropdown selected items
	 */
	setDropDownSelectedItems() {

		GSearchUtils.setDropDownSelectedItem(this.portletNamespace + 'SortDirectionOptions', 
				this.portletNamespace + 'SortDirection');
		GSearchUtils.setDropDownSelectedItem(this.portletNamespace + 'SortFieldOptions', 
				this.portletNamespace + 'SortField');
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchSort.STATE = {
	initialURLParameters: {
		value: null
	},
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