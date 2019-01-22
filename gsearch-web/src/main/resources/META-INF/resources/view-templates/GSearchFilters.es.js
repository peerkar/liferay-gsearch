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
	attached() {

		if (this.debug) {
			console.log("GSearchFilters.attached()");
		}
		
		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.templateParameters, 
			this.setQueryParam
		);	
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(
			'BasicFilters', 
			'optionmenu', 
			this
		);
		
		// Setup time range filter.
		
		this.setupTimeRangeFilter();
		
		// Set initial range values.
		
		this.setInitialDateRangeParameters();
	}
	
	created() {
		
		// Setup asset type options 
		
		this.setupAssetTypeOptions()
	}

	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchFilters.rendered()");
		}
	}
	
	/**
	 * Setup asset type options
	 */
	setupAssetTypeOptions() {
		
		let html = '';
		
		let length = this.assetTypeOptionsJSON.length;
		
		for (let i = 0; i < length; i++) {

			let item = this.assetTypeOptionsJSON[i];

			html += '<li><a data-facet="' + item.entry_class_name + '" data-value="' + item.key + '" href="#">';
			html += '<span class="text">' + item.localization + '</span>';
			html += '<span class="count"></span>';
			html += '</a></li>';
		}
		this.assetTypeOptions = html;
	}

	/**
	 * Setup time range filters
	 */
	setupTimeRangeFilter() {

		let language = Liferay.ThemeDisplay.getLanguageId().substring(0,2);

		let _self = this;
		
		$('#' + this.portletNamespace + 'FilterByTimeRange').on('click', function(event) {
			$('#' + _self.portletNamespace + 'RangeSelection').removeClass('hide');
		});
		
		Liferay.Loader.require('bootstrap-datepicker', function() {

			// Dateformat is crippled because of conflict between Java and JS yyyy-MM-dd.

			$('#' + _self.portletNamespace + 'RangeSelection .start').datepicker({
				autoclose: true,
				calendarWeeks: true,
			    format: _self.datePickerFormat.toLowerCase(),
			    language: language,
			    startDate: '',
			    todayHighlight: true,
			    toggleActive: true
			}).on('changeDate', function(event) {

				let timeFrom = $('#' + _self.portletNamespace + 'RangeSelection .start').val();
				
				if (_self.getQueryParam('timeTo', true)) {
					
					_self.setQueryParam('time', 'range', false, false);
					_self.setQueryParam('timeFrom', timeFrom, true, false);

					_self.setDateRangeSelected();
					
				} else {
					_self.setQueryParam('timeFrom', timeFrom, false, false);
				}
			});
			
			$('#' + _self.portletNamespace + 'RangeSelection .end').datepicker({
				autoclose: true,
				calendarWeeks: true,
			    format: _self.datePickerFormat.toLowerCase(),
			    language: language,
			    startDate: '',
			    todayHighlight: true,
			    toggleActive: true
			}).on('changeDate', function(event) {

				let timeTo = $('#' + _self.portletNamespace + 'RangeSelection .end').val();

				if (_self.getQueryParam('timeFrom', true)) {
					_self.setQueryParam('time', 'range', false, false);
					_self.setQueryParam('timeTo', timeTo, true, false);

					_self.setDateRangeSelected();
					
				} else {
					_self.setQueryParam('timeTo', timeTo, false, false);
				}
			});
		});
		
		// Set a listener to empty range values on other selection
		
		$('#' + this.portletNamespace + 'TimeFilterOptions').find('a').on('click', function(event) {
			$('#' + _self.portletNamespace + 'RangeSelection .start').val('');
			$('#' + _self.portletNamespace + 'RangeSelection .end').val('');

			_self.setQueryParam('timeFrom', '', false, false);
			_self.setQueryParam('timeTo', '', false, false);

		});
    }
	
	/**
	 * Set initial parameters for date range selector.
	 */
	setInitialDateRangeParameters() {
		
        let timeParam = this.getQueryParam('time', true);
    	
        if ('range' == timeParam) {
        	
            let rangeStart = this.getQueryParam('timeFrom', true);
        	let rangeEnd = this.getQueryParam('timeTo', true);

        	if (rangeStart != null) {
        		this.setQueryParam('timeFrom', rangeStart, false, false);
    			$('#' + this.portletNamespace + 'RangeSelection .start').val(rangeStart);
        	}
        
        	if (rangeStart != null) {
        		this.setQueryParam('timeTo', rangeEnd, false, false);
    			$('#' + this.portletNamespace + 'RangeSelection .end').val(rangeEnd);
        	}
        	
        	this.setDateRangeSelected();
        }
	}

	/**
	 * Set date range as selected menu option
	 */
	setDateRangeSelected() {
		
		let selectedText = $('#' + this.portletNamespace + 'RangeSelectionTitle').html();
		$('#' + this.portletNamespace + 'TimeFilter .selection').html(selectedText);
		
		$('#' + this.portletNamespace + 'TimeFilterOptions').find('li').removeClass('selected');
	}
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */
GSearchFilters.STATE = {
	addResultsCallback: {
		validator: core.isFunction
	},		
	assetTypeOptions: {
		value: null
	},
	assetTypeOptionsJSON: {
		value: null
	},
	datePickerFormat: {
		value: 'dd-mm-yyyy'
	},
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
	templateParameters: {
		value: ['type','scope','time']
	}
};

// Register component

Soy.register(GSearchFilters, templates);

export default GSearchFilters;