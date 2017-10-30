import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import Ajax from 'metal-ajax/src/Ajax';
import MultiMap from 'metal-multimap/src/MultiMap';

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
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		// Show / hide option items based on scope selection.
		
		this.setAdditionalFilterItemsVisibility(this.getQueryParam('scopeFilter'));

		// Set selected items in dropdowns.
		
		this.setDropDownSelectedItems();
		
		// Set dropdown click events.
		
		this.setDropDownClickEvents();
		
		// Set asset type facet counts.

		
		// Set asset type facet counts.
		// Change counts only when there's a new search (=keywords or scope change).
		// Also, don't show counts if we're coming to the page from a bookmark url
		// and already having there a filter. In that case we don't get all the counts.
		
		if (this.shouldRefreshFacets()) { 	

			console.log(this.initialURLParameters && 
					this.initialURLParameters['type'] == 'everything');
			
			if (!this.initialURLParameters || (this.initialURLParameters && 
					this.initialURLParameters['type'] == 'everything')) {
				
				if (this.results && this.results.facets) {
					this.typefacets = this.results.facets;
				}
			}
		}
		this.setFacetCounts(this.portletNamespace + 'TypeFilterOptions', this.typefacets);
	}
	
	/**
	 * Check and set selected items based on calling URL.
	 */
	checkCallURLSelections() {
		
		if (!this.initialURLParameters) {
			return;
		}

		let documentExtension = this.initialURLParameters['extension'];
		let documentType = this.initialURLParameters['filetype'];
		let scope = this.initialURLParameters['scope'];
		let time = this.initialURLParameters['time'];
		let type = this.initialURLParameters['type'];
		let webContentStructure = this.initialURLParameters['wcs'];

		if (type == 'file') {
			if (documentExtension) {
				if (GSearchUtils.setInitialOption(this.portletNamespace + 'DocumentExtensionFilterOptions', documentExtension)) {
					this.setQueryParam('documentExtensionFilter', documentExtension, false);
				}
			} 
			$('#' + this.portletNamespace + 'AdditionalFilters .filefilter').removeClass('hide');

			if (documentType) {
				if (GSearchUtils.setInitialOption(this.portletNamespace + 'DocumentTypeFilterOptions', documentType)) {
					this.setQueryParam('documentTypeFilter', documentType, false);
				}
			} 
			$('#' + this.portletNamespace + 'AdditionalFilters .filefilter').removeClass('hide');
		}
		
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
				this.setQueryParam('typeFilter', type, false);
			}
		}

		if (type == 'web-content') {
			if (webContentStructure) {
				if (GSearchUtils.setInitialOption(this.portletNamespace + 'WebContentStructureFilterOptions', webContentStructure)) {
					this.setQueryParam('webContentStructureFilter', webContentStructure, false);
				}
			} 
			$('#' + this.portletNamespace + 'AdditionalFilters .wcfilter').removeClass('hide');
		}
	}
	
	/**
	 * 	Show / hide option items based on scope selection.
	 * 
 	 * @param {String} scope
	 */
	setAdditionalFilterItemsVisibility(scope) {

		if (scope == 'all') {
			$('#' + this.portletNamespace + 'DocumentExtensionFilterOptions li,' +
					'#' + this.portletNamespace + 'DocumentTypeFilterOptions li,' +
					'#' + this.portletNamespace + 'WebContentStructureFilterOptions li').removeClass('hide');
		} else {
			$('#' + this.portletNamespace + 'DocumentExtensionFilterOptions li .all,' +
				'#' + this.portletNamespace + 'DocumentTypeFilterOptions li .all,' +
				'#' + this.portletNamespace + 'WebContentStructureFilterOptions li .all').addClass('hide');
		}
	}	

	/**
	 * Set dropdown click events.
	 */
	setDropDownClickEvents() {
		
		let _self = this;
		
		GSearchUtils.setDropDownClickEvents(
				this.portletNamespace + 'DocumentExtensionFilterOptions',
				this.portletNamespace + 'DocumentExtensionFilter', 
				this.getQueryParam, 
				this.setQueryParam, 
				'documentExtensionFilter');
		GSearchUtils.setDropDownClickEvents(
				this.portletNamespace + 'DocumentTypeFilterOptions',
				this.portletNamespace + 'DocumentTypeFilter', 
				this.getQueryParam, 
				this.setQueryParam, 
				'documentTypeFilter');
		GSearchUtils.setDropDownClickEvents(
				this.portletNamespace + 'ScopeFilterOptions', 
				this.portletNamespace + 'ScopeFilter', 
				this.getQueryParam, 
				this.setQueryParam, 
				'scopeFilter');
		GSearchUtils.setDropDownClickEvents(
				this.portletNamespace + 'TimeFilterOptions', 
				this.portletNamespace + 'TimeFilter', 
				this.getQueryParam, 
				this.setQueryParam, 
				'timeFilter');
		GSearchUtils.setDropDownClickEvents(
				this.portletNamespace + 'TypeFilterOptions',
				this.portletNamespace + 'TypeFilter', 
				this.getQueryParam, 
				this.setQueryParam, 
				'typeFilter');
		GSearchUtils.setDropDownClickEvents(
				this.portletNamespace + 'WebContentStructureFilterOptions',
				this.portletNamespace + 'WebContentStructureFilter', 
				this.getQueryParam, 
				this.setQueryParam, 
				'webContentStructureFilter');
		
		// Set events for showing additional filters

		$('#' + this.portletNamespace + 'ScopeFilterOptions li a').on('click', function(event) {

			let value = $(this).attr('data-value');

			_self.setAdditionalFilterItemsVisibility();
			event.preventDefault();		
		});

		$('#' + this.portletNamespace + 'TypeFilterOptions li a').on('click', function(event) {

			$('#' + _self.portletNamespace + 'AdditionalFilters .filter').addClass('hide');

			let value = $(this).attr('data-value');
			
			if (value == 'web-content') {
				$('#' + _self.portletNamespace + 'AdditionalFilters .wcfilter').removeClass('hide');
			} else if (value == 'file') {
				$('#' + _self.portletNamespace + 'AdditionalFilters .filefilter').removeClass('hide');
			}
			event.preventDefault();		
		});
	}
	
	/**
	 * Set dropdown selected items (on render).
	 */
	setDropDownSelectedItems() {
		
		GSearchUtils.setDropDownSelectedItem(
				this.portletNamespace + 'DocumentExtensionFilterOptions', 
				this.portletNamespace + 'DocumentExtensionFilter');
		GSearchUtils.setDropDownSelectedItem(
				this.portletNamespace + 'DocumentTypeFilterOptions', 
				this.portletNamespace + 'DocumentTypeFilter');
		GSearchUtils.setDropDownSelectedItem(
				this.portletNamespace + 'ScopeFilterOptions', 
				this.portletNamespace + 'ScopeFilter');
		GSearchUtils.setDropDownSelectedItem(
				this.portletNamespace + 'TimeFilterOptions', 
				this.portletNamespace + 'TimeFilter');
		GSearchUtils.setDropDownSelectedItem(
				this.portletNamespace + 'TypeFilterOptions', 
				this.portletNamespace + 'TypeFilter');
		GSearchUtils.setDropDownSelectedItem(
				this.portletNamespace + 'WebContentStructureFilterOptions', 
				this.portletNamespace + 'WebContentStructureFilter');

	}

	/**
	 * Set facets counts.
	 * 
 	 * @param {String} optionsElementId
	 */
	setFacetCounts(optionsElementId, facets) {

		// Populate counts
		
		if (facets) {

			let length = facets.length;

			for (let i = 0; i < length; i++) {

				let term =  facets[i].term;
				let frequency =  facets[i].frequency;
				let element = $('#' + optionsElementId + ' li a[data-facet="' + term + '"]');

				if (element) {
					$(element).find('.count').html('(' + frequency + ')');
				}
			}
		}
	}
	
	/**
	 * Check (at render) if we should refresh facet counts.
	 */
	shouldRefreshFacets() {

		let queryState = this.getQueryParam('scopeFilter') + this.getQueryParam('keywords');

		if (this.queryState != queryState) {
			this.queryState = queryState;
			return true;
		}
		return false;
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