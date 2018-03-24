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

		$('#' + menuWrapperElementId + ' .' + menuClass).each(function() {

			let options = $(this).find('.dropdown-menu').attr('id');
			let trigger = $(this).find('button').attr('id');
			let paramName = $(this).attr('data-paramname');
			let isMultiValued = $(this).attr('data-ismultivalued');
			
			// Convert to boolean
			
			let isMulti = (isMultiValued == 'true');
					
			GSearchUtils.setupOptionList(
				options, 
				trigger, 
				queryParamGetter, 
				queryParamSetter, 
				paramName,
				isMulti
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

		for (let field of keyArray) {

			let values = initialValues[field];

			if (!values) {
				continue
			}
			
			for (let value of values) {
				queryParamSetter(field, value, false);
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
	 * @param {String} initialValue
	 */
	static setupOptionList(optionElementId, triggerElementId, queryParamGetter, 
			queryParamSetter, queryParam, isMultiValued = false) {

		let values = queryParamGetter(queryParam);

		// Set initially selected item
		
		let selectedItems = GSearchUtils.setOptionListSelectedItems(optionElementId, 
				triggerElementId, values, isMultiValued);

		// Set text
		
		if (selectedItems.length > 0) {
			GSearchUtils.setOptionListTriggerElementText(triggerElementId, selectedItems, queryParam);
		}
		
		// Set click events
		
		GSearchUtils.setOptionListClickEvents(optionElementId, triggerElementId, 
				queryParamGetter, queryParamSetter, queryParam, isMultiValued) 
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
	 */
	static setOptionListClickEvents(optionElementId, triggerElementId, queryParamGetter, 
			queryParamSetter, queryParam, isMultiValued) {
		
		let currentValues = queryParamGetter(queryParam);
		
		$('#' + optionElementId + ' li a').on('click', function(event) {

			let value = $(this).attr('data-value');
			
			if (currentValues.indexOf(value) < 0) {

				queryParamSetter(queryParam, value, true, isMultiValued);

				let selectedItems = GSearchUtils.setOptionListSelectedItems(optionElementId, 
						triggerElementId, value, isMultiValued);
				
				if (selectedItems.length > 0) {
					
					GSearchUtils.setOptionListTriggerElementText(triggerElementId, selectedItems, queryParam);
				}
				
			} else {

				queryParamSetter(queryParam, null, true, isMultiValued, value);

				GSearchUtils.unsetOptionListSelectedItem(optionElementId, value);
			}
			event.preventDefault();
		});			
	}
	
	/**
	 * Set selected option item.
	 * 
	 * @param {String} optionElementId
	 * @param {String} value
	 */
	static setOptionListSelectedItems(optionElementId, triggerElementId, values, isMultiValued) {

		let selectedItems = [];

		let defaultItem = $('#' + optionElementId + ' li.default a');
		
		let valueFound = false;
		
		$('#' + optionElementId + ' li a').each(function() {

			if (!isMultiValued) {

				if ($(this).attr('data-value') == values[0]) {
					
					valueFound = true;

					$('#' + optionElementId + ' li').removeClass('selected');
					
					$(this).parent().addClass('selected');
					
					selectedItems.push(this);
					
					return false;
				}

			} else {

				for (let value of values) {
									
					if ($(this).attr('data-value') == value) {
						
						valueFound = true;
						
						$(defaultItem).parent().removeClass('selected');

						$(this).parent().addClass('selected');
						
						selectedItems.push(this);
					}
				}
			}
		});
			
		if (!valueFound && defaultItem.length > 0) {

			selectedItems.push(defaultItem);
		}

		return selectedItems;
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
		
		$('#' + optionElementId + ' li a').each(function() {

			if ($(this).attr('data-value') == value) {
				
				$(this).parent().removeClass('selected');
				
				return false;
			}
		});

		// If there are no more selections mark the default item ("any") as selected.
		
		if ($('#' + optionElementId + ' li.selected').length === 0) {
			$('#' + optionElementId + ' li.default').addClass('selected');
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