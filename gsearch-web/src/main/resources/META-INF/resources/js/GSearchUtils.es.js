/**
 * GSearch utility class
 */

class GSearchUtils {

	/**
	 * Check if object is empty
	 *
 	 * @param {Object} obj
	 */
	static isEmptyObject(obj) {

		if (!obj) {
			return true;
		}

		for (var x in obj) {
			return false;
		}
		return true;
	}

	/**
	 * Remove event listeners
	 *
	 * @param {String} menuWrapperElementId
	 * @param {String} menuClass
	 */
	static bulkCleanUpOptionListEvents(menuWrapperElementId, menuClass) {

		$('#' + menuWrapperElementId + ' .' + menuClass + ' li a').each(function() {
			$(this).unbind();
		});
	}

	/**
	 * Bulk setup option lists.
 	 *
	 * @param {String} menuWrapperElementId
	 * @param {String} triggerElementId
	 * @param {Object} queryParamGetter
     * @param {Object} queryParamSetter
 	 *
	 */
	static bulkSetupOptionLists(menuWrapperElementId, menuClass, queryParamGetter, queryParamSetter) {

		$('#' + menuWrapperElementId + ' div.filter-dropdowns button.dropdown-toggle').each(function() {
			$(this).on('click', function(event) {
				$(event.currentTarget.parentNode).toggleClass('open');
			});
		});

        $('#' + menuWrapperElementId + ' .' + menuClass).each(function() {

			let options = $(this).find('.dropdown-menu').attr('id');
			if (!options) {
                options = $(this).find('.tabs-menu').attr('id');
			}
			let trigger = $(this).find('button').attr('id');
			let paramName = $(this).attr('data-paramname');
			let isMultiValued = $(this).attr('data-ismultivalued');
			let isClearOnClickDisabled = $(this).attr('data-isclearonclickdisabled');

			// Convert to boolean

			let isMulti = (isMultiValued == 'true');
			let isClearDisabled = (isClearOnClickDisabled == 'true');

			GSearchUtils.setupOptionList(
				options,
				trigger,
				queryParamGetter,
				queryParamSetter,
				paramName,
				isMulti,
                isClearDisabled
			);

		});
	}

	/**
	 * Set initial query parameters.
	 *
	 * @param {String} parameterArray
	 * @param {Object} queryParamSetter
	 *
	 */
	static setInitialQueryParameters(initialValues, keyArray, queryParamSetter) {

		if (!(initialValues && keyArray)) {
			return;
		}

		let length = keyArray.length;

		for (let i = 0; i < length; i++) {

			let field = keyArray[i];

			let values = initialValues[field];

			if (!values) {
				continue
			}

			let length2 = values.length;

			for (let j = 0; j < length2; j++) {

				let value = values[j];

				if (value) {
					queryParamSetter(field, value, false);

				} else {

					// Reset possibly cached state

					queryParamSetter(field, '', false);
				}
			}
		}
	}

	/**
	 * Setup option list.
	 *
	 * @param {String} optionElementId
	 * @param {String} triggerElementId
	 * @param {Object} queryParamGetter
	 * @param {Object} queryParamSetter
	 * @param {String} queryParam
	 * @param {Boolean} isMultiValued
     * @param {Boolean} isClearDisabled
     */
	static setupOptionList(optionElementId, triggerElementId, queryParamGetter,
			queryParamSetter, queryParam, isMultiValued = false, isClearDisabled = false) {

		let values = queryParamGetter(queryParam);

		// Set initially selected item

		GSearchUtils.setOptionListSelectedItems(optionElementId,
				triggerElementId, values, isMultiValued);

		// Set click events
		GSearchUtils.setOptionListClickEvents(optionElementId, triggerElementId,
				queryParamGetter, queryParamSetter, queryParam, isMultiValued, isClearDisabled)
	}

	/**
	 * Set click events for options list for setting query params.
	 *
	 * @param {String} optionElementId
	 * @param {String} triggerElementId
	 * @param {Object} queryParamGetter
	 * @param {Object} queryParamSetter
	 * @param {String} queryParam
     * @param {Boolean} isMultiValued
     * @param {Boolean} isClearDisabled
	 */
	static setOptionListClickEvents(optionElementId, triggerElementId, queryParamGetter,
			queryParamSetter, queryParam, isMultiValued, isClearDisabled) {
		$('#' + optionElementId + ' li a, #' + optionElementId + ' li :checkbox').on('click', function(event) {

			let isClickDisabled = false;
            let parent = event.currentTarget.parentElement;

            if (isClearDisabled) {
                // disable clearing current filter when it is clicked again
                isClickDisabled = $(parent).hasClass('selected');
			}

            let currentValues = queryParamGetter(queryParam);
            let value = $(this).attr('data-value');

            let clickTarget = $(event.currentTarget);

            // when default is clicked:
			//    - clear all filters of this type
			//    - remove all selected classes except default
			//    - remove all checked checkboxes except check default
            if (isMultiValued && $(this).closest('li').hasClass('default')) {
				for (let i = 0; i < currentValues.length; i++) {
                    queryParamSetter(queryParam, null, false, isMultiValued, currentValues[i]);
				}
                $('#' + optionElementId + ' li.selected').removeClass('selected');
                queryParamSetter(queryParam, value, true, isMultiValued);
                GSearchUtils.setOptionListSelectedItems(optionElementId,
                    triggerElementId, [value], isMultiValued);
                $('#' + optionElementId + ' li :checkbox').prop('checked', false);
                $('#' + optionElementId + ' li.default :checkbox').prop('checked', true);
            } else if (!isClickDisabled) {

            	if (isMultiValued) {
                    $('#' + optionElementId + ' li.default').removeClass('selected');
                    $('#' + optionElementId + ' li.default :checkbox').prop('checked', false);
                    let defaultValue = $('#' + optionElementId + ' li.default :checkbox').attr('data-value');
                    queryParamSetter(queryParam, null, false, isMultiValued, defaultValue);
                }
				if (currentValues.indexOf(value) < 0) {

					let isRefresh = true;
                    if (clickTarget.hasClass('time-range')) {
                        $(parent).find('div.time-range').removeClass('hidden');
						isRefresh = false;
                    } else if (clickTarget.hasClass('time')) {
                        $('#' + optionElementId + ' div.time-range').addClass('hidden');
                        $('#' + optionElementId + ' input.time-range').val('');
                        queryParamSetter('timeStart', '', false, false);
                        queryParamSetter('timeEnd', '', false, false);

                    }
					queryParamSetter(queryParam, value, isRefresh, isMultiValued);

					GSearchUtils.setOptionListSelectedItems(optionElementId,
						triggerElementId, [value], isMultiValued);


				} else {

					queryParamSetter(queryParam, null, true, isMultiValued, value);

					GSearchUtils.unsetOptionListSelectedItem(optionElementId, value);

					if (clickTarget.hasClass('time-range')) {
                        $(parent).find('div.time-range').addClass('hidden');
                    }
				}

			}

            if (event.currentTarget.nodeName === 'A') {
                event.preventDefault();
			}
		});
	}

	/**
	 * Set selected option item.
	 *
	 * @param {String} optionElementId
	 * @param {String} value
	 */
	static setOptionListSelectedItems(optionElementId, triggerElementId, values, isMultiValued) {

		let defaultItem = $('#' + optionElementId + ' li.default');

		let valueFound = false;

		$('#' + optionElementId + ' li a, #' + optionElementId + ' li :checkbox').each(function() {

			let parentLi = $(this).parent();
			if (parentLi.prop('tagName') !== 'LI') {
				parentLi = parentLi.parent();
        	}
			if (!isMultiValued) {

				if ($(this).attr('data-value') == values[0]) {

					valueFound = true;

					$('#' + optionElementId + ' li').removeClass('selected');

					parentLi.addClass('selected');

					return false;
				}

			} else {

				let length = values.length;

				for (let i = 0; i < length; i++) {

					let value = values[i];

					if ($(this).attr('data-value') == value) {

						valueFound = true;

						defaultItem.removeClass('selected');

						parentLi.addClass('selected');

					}
				}
			}
		});

	}

	/**
	 * Set text for the trigger element.
	 *
	 * If there's a text element in the selected element,
	 * use contents of that as a source.
	 *
	 * @param {String} triggerElementId
	 * @param {Object} selectedItem
	 */
	static setOptionListTriggerElementText(triggerElementId, selectedItems, queryParam) {

		if (selectedItems.length > 1) {

			let html = $('#' + triggerElementId ).parent().attr('data-multipleoption');

			// Fallback translation. See ResultItemBuilder.

			if (!html) {
				html = "multiple " + queryParam.toLowerCase();
			}

			$('#' + triggerElementId + ' .selection').html('[<i> ' + html + ' </i>]');

			return;
		}

		let textElement = $(selectedItems[0]).find('.text');

		let text = null;

		if (textElement.length > 0) {
			text = $(textElement).html();
		} else {
			text = $(selectedItems[0]).html();
		}

		$('#' + triggerElementId + ' .selection').html(text);
	}

	/**
	 * Unset selected option item.
	 *
	 * @param {String} optionElementId
	 * @param {String} value
	 */
	static unsetOptionListSelectedItem(optionElementId, value) {

		$('#' + optionElementId + ' li a, #' + optionElementId + ' li :checkbox').each(function() {

			if ($(this).attr('data-value') == value) {
                let parentLi = $(this).parent();
                if (parentLi.nodeName !== 'LI') {
                    parentLi = parentLi.parent();
                }
				parentLi.removeClass('selected');

				return false;
			}
		});

		// If there are no more selections mark the default item ("any") as selected.

		if ($('#' + optionElementId + ' li.selected').length === 0) {
			$('#' + optionElementId + ' li.default').addClass('selected');
            $('#' + optionElementId + ' li.default :checkbox').prop('checked', true);
		}
	}

	/**
	 * Get URL parameter
	 *
 	 * See https://stackoverflow.com/questions/901115/how-can-i-get-query-string-values-in-javascript
     *
 	 * @param {String} name
	 */
	static getURLParameters() {

		let urlParams;

		let match,
	        pl     = /\+/g,
	        search = /([^&=]+)=?([^&]*)/g,
	        decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
	        query  = window.location.search.substring(1);

	    urlParams = {};

	    while (match = search.exec(query)) {
	       urlParams[decode(match[1])] = decode(match[2]);
	    }

	    return urlParams;
	}
}

export default GSearchUtils;