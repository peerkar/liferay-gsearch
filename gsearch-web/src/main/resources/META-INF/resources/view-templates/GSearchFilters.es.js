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
		
		this.debug = opt_config.JSDebugEnabled;

		this.initialQueryParameters = opt_config.initialQueryParameters; 

		this.portletNamespace = opt_config.portletNamespace;

		this.assetTypeOptions = opt_config.assetTypeOptions;
		
		this.documentFormatOptions = opt_config.documentFormatOptions;

		this.documentTypeOptions = opt_config.documentTypeOptions;
	
		this.webContentStructureOptions = opt_config.webContentStructureOptions;
	}

	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearchFilters.attached()");
		}
		
		// Set initial query parameters.
		
		if (this.initialQueryParameters['df']) {
			this.setQueryParam('df', this.initialQueryParameters['df'], false);
		}

		if (this.initialQueryParameters['dt']) {
			this.setQueryParam('dt', this.initialQueryParameters['dt'], false);
		}

		if (this.initialQueryParameters['scope']) {
			this.setQueryParam('scope', this.initialQueryParameters['scope'], false);
		}

		if (this.initialQueryParameters['time']) {
			this.setQueryParam('time', this.initialQueryParameters['time'], false);
		}

		if (this.initialQueryParameters['type']) {
			this.setQueryParam('type', this.initialQueryParameters['type'], false);
		}

		if (this.initialQueryParameters['wcs']) {
			this.setQueryParam('wcs', this.initialQueryParameters['wcs'], false);
		}
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchFilters.rendered()");
		}
		
		// Setup options lists.

		this.setupOptionLists();

		// Show / hide option items based on scope selection.
		
		this.setAdditionalFiltersVisibility();

		// Show / hide option items based on scope selection.
		
		this.setAdditionalFilterItemsVisibility(this.getQueryParam('scope'));
		
		// Update asset type facet counts.
		
		this.updateAssetTypeFacetCounts();
	}

	/**
	 * Set additional filter menu visibility.
	 */
	setAdditionalFiltersVisibility() {

		// Additional filters visibility
		
		if (this.getQueryParam('type') == 'file') {
			$('#' + this.portletNamespace + 'AdditionalFilters .filefilter').removeClass('hide');
			$('#' + this.portletNamespace + 'AdditionalFilters .filefilter').removeClass('hide');
		} else if (this.getQueryParam('type') == 'web-content') {
			$('#' + this.portletNamespace + 'AdditionalFilters .wcfilter').removeClass('hide');
		}		
	}
	
	/**
	 * 	Show / hide option items based on scope selection.
	 *  .all-classed items are from all sites. Others from current site.
	 * 
 	 * @param {String} scope
	 */
	setAdditionalFilterItemsVisibility(scope) {

		if (scope == 'all') {
			$('#' + this.portletNamespace + 'DocumentFormatFilterOptions li,' +
					'#' + this.portletNamespace + 'DocumentTypeFilterOptions li,' +
					'#' + this.portletNamespace + 'WebContentStructureFilterOptions li').removeClass('hide');
		} else {
			$('#' + this.portletNamespace + 'DocumentFormatFilterOptions li .all,' +
				'#' + this.portletNamespace + 'DocumentTypeFilterOptions li .all,' +
				'#' + this.portletNamespace + 'WebContentStructureFilterOptions li .all').addClass('hide');
		}
	}	

	/**
	 * Setup option lists.
	 */
	setupOptionLists() {

		let _self = this;
		
		GSearchUtils.setupOptionList(
			this.portletNamespace + 'DocumentFormatFilterOptions',
			this.portletNamespace + 'DocumentFormatFilter', 
			this.getQueryParam, 
			this.setQueryParam, 
			'df'
		);

		GSearchUtils.setupOptionList(
			this.portletNamespace + 'DocumentTypeFilterOptions',
			this.portletNamespace + 'DocumentTypeFilter', 
			this.getQueryParam, 
			this.setQueryParam, 
			'dt'
		);
		
		GSearchUtils.setupOptionList(
			this.portletNamespace + 'ScopeFilterOptions', 
			this.portletNamespace + 'ScopeFilter', 
			this.getQueryParam, 
			this.setQueryParam, 
			'scope'
		);

		GSearchUtils.setupOptionList(
			this.portletNamespace + 'TimeFilterOptions', 
			this.portletNamespace + 'TimeFilter', 
			this.getQueryParam, 
			this.setQueryParam, 
			'time'
		);

		GSearchUtils.setupOptionList(
			this.portletNamespace + 'TypeFilterOptions',
			this.portletNamespace + 'TypeFilter', 
			this.getQueryParam, 
			this.setQueryParam, 
			'type'
		);

		GSearchUtils.setupOptionList(
			this.portletNamespace + 'WebContentStructureFilterOptions',
			this.portletNamespace + 'WebContentStructureFilter', 
			this.getQueryParam, 
			this.setQueryParam, 
			'wcs'
		);
		
		// Set events for showing additional filters
		
		$('#' + this.portletNamespace + 'ScopeFilterOptions li a').on('click', function(event) {

			let scope = $(this).attr('data-value');

			_self.setAdditionalFilterItemsVisibility(scope);
			
			event.preventDefault();		
		});

		$('#' + this.portletNamespace + 'TypeFilterOptions li a').on('click', function(event) {

			$('#' + _self.portletNamespace + 'AdditionalFilters .filter').addClass('hide');

			let type = $(this).attr('data-value');
			
			if (type == 'web-content') {
				$('#' + _self.portletNamespace + 'AdditionalFilters .wcsfilter').removeClass('hide');
			} else if (type == 'file') {
				$('#' + _self.portletNamespace + 'AdditionalFilters .filefilter').removeClass('hide');
			}
			event.preventDefault();		
		});
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
		} else {
			$('#' + optionsElementId + ' li .count').html('');
		}
	}
	
	/**
	 * Update asset type facet counts.
	 */
	updateAssetTypeFacetCounts() {		

		let refresh = false;
		
		// Check if parameters have changed so that we need to update type facets
		
		let currentState = this.getQueryParam('scope') + this.getQueryParam('q');
		let currentType = this.getQueryParam('type');
		
		if (this.previousState != currentState) {
			refresh = true;
		} else if (this.previousType != currentType && currentType == 'everything') {
			refresh = true;
		}
		
		this.previousState = currentState;
		this.previousType = currentType;

		if (this.debug) {
			console.log("Facets need refresh: " + refresh);
		}		
		
		// Change counts only when there's a new search (=keywords or scope change).
		// Also, don't show counts if we're coming to the page from a bookmark url
		// and already having there a filter. In that case we don't get all the counts.
		
		if (refresh) { 	
			
			if (this.getQueryParam('type') == 'everything') {
				if (this.results && this.results.facets) {
					this.typefacets = this.results.facets;
				}
			} else {
				this.typefacets = null;
			}
		}
		this.setFacetCounts(this.portletNamespace + 'TypeFilterOptions', this.typefacets);
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