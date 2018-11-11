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

		this.language = opt_config.language;

		this.assetTypeOptions = opt_config.assetTypeOptions;

		this.unitFilters = opt_config.unitFilters;
	}

	/**
	 * @inheritDoc
	 */
	attached() {

		if (this.debug) {
			console.log("GSearchFilters.attached()");
		}

		this.setupAssetTypeOptions();

		this.setupUnitFilters();

		// Set initial query parameters from calling url.

		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters,
			this.templateParameters,
			this.setQueryParam
		);

		// Setup options lists.
		GSearchUtils.bulkSetupOptionLists(
			this.portletNamespace + 'BasicFilters',
			'optionmenu',
			this.getQueryParam,
			this.setQueryParam
		);

        // Add results callback

		this.addResultsCallback(this.updateAssetTypeFacetCounts);

        this.setupTimeFilterRanges(this.portletNamespace, this.language, this.setQueryParam, this.getQueryParam, this.getDateStringForUrlParam);
	}

	/**
	 * @inheritDoc
	 */
	rendered() {

		if (this.debug) {
			console.log("GSearchFilters.rendered()");
		}
	}

	getDateStringForUrlParam(date) {
        let day = '' + date.getDate();
        day.length === 1 ? day = '0' + day : day;
        let month = '' + (date.getMonth() + 1);
        month.length === 1 ? month = '0' + month : month;
        let year = date.getFullYear();
        return day + '-' + month + '-' + year;
    }


    setupTimeFilterRanges(portletNamespace, language, queryParamSetter, queryParamGetter, getDateString) {

		AUI({lang: language}).use('aui-datepicker', function(A) {
            new A.DatePicker(
                {
                    trigger: '#' + portletNamespace + 'timeRangeStart',
                    mask: '%d.%m.%Y',
					popover: {
                    	zIndex: 10000
					},
                    popoverCssClass: 'datepicker-popover',
                    on: {
                        selectionChange: function(event) {
							queryParamSetter('timeStart', getDateString(new Date(event.newSelection)), true, false);
                        }
                    }
                }
            );
		});

        AUI({lang: language}).use('aui-datepicker', function(A) {
            new A.DatePicker(
                {
                    trigger: '#' + portletNamespace + 'timeRangeEnd',
                    mask: '%d.%m.%Y',
                    popover: {
                        zIndex: 10000
                    },
                    popoverCssClass: 'datepicker-popover',
                    on: {
                        selectionChange: function(event) {
                            queryParamSetter('timeEnd', getDateString(new Date(event.newSelection)), true, false);
                        }
                    }
                }
            );
        });

        let timeParam = queryParamGetter('time', true);
        if ((timeParam !== undefined) && (timeParam !== null) && (timeParam === 'range')) {
            let dateRangeDiv = $('#' + portletNamespace + 'TimeFilterOptions div.time-range');
        	dateRangeDiv.removeClass('hidden');
            let rangeStart = queryParamGetter('timeStart', true);
            let rangeEnd = queryParamGetter('timeEnd', true);
            if ((rangeStart !== undefined) && (rangeStart !== null) && (rangeStart !== '')) {
                rangeStart = rangeStart.replace(/-/g, '.');
                dateRangeDiv.find('#' + portletNamespace + 'timeRangeStart').val(rangeStart);
            }
            if ((rangeEnd !== undefined) && (rangeEnd !== null) && (rangeEnd !== '')) {
                rangeEnd = rangeEnd.replace(/-/g, '.');
                dateRangeDiv.find('#' + portletNamespace + 'timeRangeEnd').val(rangeEnd);
            }
        }
    }

	/**
	 * Setup asset type options
	 */
	setupAssetTypeOptions() {

		let html = '';

		let length = this.assetTypeOptions.length;

		for (let i = 0; i < length; i++) {

			let item = this.assetTypeOptions[i];

			html += '<li><a data-value="' + item.key + '" href="#">';
			html += '<span class="text">' + item.localization + '</span>';
            html += '&nbsp;';
			html += '<span class="count"></span>';
			html += '</a></li>';
		}
		$('#' + this.portletNamespace + 'TypeFilterOptions').append(html);
	}

	/**
	 * Update asset type facet counts.
	 */
	updateAssetTypeFacetCounts(portletNamespace, results) {

		// Clear current values

		$('#' + portletNamespace + 'TypeFilterOptions li .count').html('');

		if (results && results.meta.typeCounts) {

            $('#' + portletNamespace + 'TypeFilterOptions li a').each(function(element) {
                let key = $(this).attr('data-value');
            	if (key in results.meta.typeCounts) {
					let frequency = results.meta.typeCounts[key];
                    $(this).find('.count').html('(' + frequency + ')');
				}
			});


		}
	}

	setupUnitFilters() {
		this.createUnitFilters(this.unitFilters, $('#' + this.portletNamespace + 'UnitFilterUl'));
	}

	createUnitFilters(units, element) {
		let initialUnitParams = this.initialQueryParameters.unit !== null ? this.initialQueryParameters.unit : null;
        for (let i = 0; i < units.length; i++) {
            let unit = units[i];
            let li = null;
            li = $(document.createElement('li'));

            let label = $('<label />', { 'for': this.portletNamespace + 'unitCategory-' + unit.categoryId, text: unit.name });
            label.addClass('unit-selection checkbox');

            let checkbox = $('<input />', {
                type: 'checkbox',
                id: this.portletNamespace + 'unitCategory-' + unit.categoryId,
                value: unit.name ,
                'data-value': unit.categoryId,
                checked: initialUnitParams !== null && initialUnitParams.indexOf(unit.categoryId) > -1
            });
            checkbox.appendTo(label);
            if (unit.categoryId === '0') {
            	li.addClass('default');
			}

            $('<span class="checkmark"></span>').appendTo(label);

            label.appendTo(li);

            element.append(li);

        }

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
	getQueryParam: {
		validator: core.isFunction
	},
	setQueryParam: {
		validator: core.isFunction
	},
	templateParameters: {
		value: ['type','scope','time','unit', 'timeStart', 'timeEnd']
	}
};

// Register component

Soy.register(GSearchFilters, templates);

export default GSearchFilters;