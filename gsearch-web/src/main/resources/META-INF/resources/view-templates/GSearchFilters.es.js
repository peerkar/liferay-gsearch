import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchFilters.soy';

/**
 * GSearch filters component.
 */
class GSearchFilters extends Component {

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

	rendered() {

		// Set dropdown selections
		
		this.setDropDownSelectedItems();
		
		// Set click events
		
		this.setDropDownClickEvents();
		
		// Set facets
		
		this.setTypeFacetCounts();
	}
	
	/**
	 * Check and set selected items based on calling URL.
	 */
	checkCallURLSelections() {
		
		if (!this.initialURLParameters) {
			return;
		}

		let scope = this.initialURLParameters['scope'];
		let time = this.initialURLParameters['time'];
		let type = this.initialURLParameters['type'];

		if (scope) {
			if (GSearchUtils.setInitialOption(this.portletNamespace + 'ScopeFilterOptions', scope)) {
				this.setQueryParam('scopeFilter', scope, false);
			}
		} 
		
		if (time) {
			if (GSearchUtils.setInitialOption(this.portletNamespace + 'TimeFilterOptions', time)) {
				this.setQueryParam('timeFilter', time, false);
			}
		}

		if (type) {
			if (GSearchUtils.setInitialOption(this.portletNamespace + 'TypeFilterOptions', type)) {
				this.setQueryParam('typeFilter', time, false);
			}
		}
	}
	
	/**
	 * Set dropdown click events.
	 */
	setDropDownClickEvents() {
		
		GSearchUtils.setDropDownClickEvents(this.portletNamespace + 'ScopeFilterOptions', 
				this.portletNamespace + 'ScopeFilter', 
				this.getQueryParam, this.setQueryParam, 'scopeFilter');
		GSearchUtils.setDropDownClickEvents(this.portletNamespace + 'TimeFilterOptions', 
				this.portletNamespace + 'TimeFilter', 
				this.getQueryParam, this.setQueryParam, 'timeFilter');
		GSearchUtils.setDropDownClickEvents(this.portletNamespace + 'TypeFilterOptions',
				this.portletNamespace + 'TypeFilter', 
				this.getQueryParam, this.setQueryParam, 'typeFilter');
		
	}
	
	/**
	 * Set dropdown selected items
	 */
	setDropDownSelectedItems() {
		GSearchUtils.setDropDownSelectedItem(this.portletNamespace + 'ScopeFilterOptions', 
				this.portletNamespace + 'ScopeFilter');
		GSearchUtils.setDropDownSelectedItem(this.portletNamespace + 'TimeFilterOptions', 
				this.portletNamespace + 'TimeFilter');
		GSearchUtils.setDropDownSelectedItem(this.portletNamespace + 'TypeFilterOptions', 
				this.portletNamespace + 'TypeFilter');
	}

	/**
	 * Set type facets.
	 */
	setTypeFacetCounts() {

		// Populate counts
		
		if (this.results && this.results.facets) {

			let length = this.results.facets.length;

			for (let i = 0; i < length; i++) {

				// We are matching the term with list item class name
				// Dots are replaced by underscores
				
				let term =  this.results.facets[i].term.replace(/\./g, '_');
				let frequency =  this.results.facets[i].frequency;

				if ($('#' + this.portletNamespace + 'TypeFilterOptions .' + term)) {
					$('#' + this.portletNamespace + 'TypeFilterOptions .' + term + ' .count').html('(' + frequency + ')');
				}
			}
		}
	}
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */
GSearchFilters.STATE = {
	getQueryParam: {
		validator: core.isFunction
	},
	initialURLParameters: {
		value: null
	},
	results: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	}
};

// Register component

Soy.register(GSearchFilters, templates);

export default GSearchFilters;