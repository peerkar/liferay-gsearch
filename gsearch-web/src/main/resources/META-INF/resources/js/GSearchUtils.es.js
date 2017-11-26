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
			let paramName = $(this).attr('data-paramname')
			
			GSearchUtils.setupOptionList(
				options, 
				trigger, 
				queryParamGetter, 
				queryParamSetter, 
				paramName
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
	static setInitialQueryParameters(valueArray, keyArray, queryParamSetter) {

		if (!(valueArray && keyArray)) {
			return;
		}
		
		for (let field of keyArray) {

			let value = valueArray[field];

			if (value) {
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
	static setupOptionList(optionElementId, triggerElementId, queryParamGetter, queryParamSetter, queryParam) {

		// Set initially selected item
		
		let initialValue = queryParamGetter(queryParam);

		let selectedItem = GSearchUtils.setOptionListSelectedItem(optionElementId, 
				triggerElementId, initialValue);

		if (selectedItem) {
			GSearchUtils.setOptionListTriggerElementText(triggerElementId, selectedItem);
		}
		
		GSearchUtils.setOptionListClickEvents(optionElementId, triggerElementId, 
				queryParamGetter, queryParamSetter, queryParam) 
	}
	
	/**
	 * Set click events for options list for setting query params.
	 *
	 * @param {String} optionElementId
	 * @param {String} triggerElementId
	 * @param {String} queryParamGetter
	 * @param {String} queryParamSetter
	 * @param {String} queryParam
	 * @param {String} initialValue
	 */
	static setOptionListClickEvents(optionElementId, triggerElementId, queryParamGetter, queryParamSetter, queryParam, initialValue) {
		
		// Set click events
		
		$('#' + optionElementId + ' li a').on('click', function(event) {

			let value = $(this).attr('data-value');
			
			if (value != queryParamGetter(queryParam)) {

				queryParamSetter(queryParam, value);

				let selectedItem = GSearchUtils.setOptionListSelectedItem(optionElementId, triggerElementId, value);
				
				if (selectedItem) {
					GSearchUtils.setOptionListTriggerElementText(triggerElementId, selectedItem);
				}
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
	static setOptionListSelectedItem(optionElementId, triggerElementId, value) {

		let selectedItem = null;

		$('#' + optionElementId + ' li a').each(function() {

			// Try to find a default selected item
			
			if ($(this).parent().hasClass('selected')) {
				selectedItem = this;
			}
			
			if ($(this).attr('data-value') == value) {
				
				$('#' + optionElementId + ' li').removeClass('selected');
				$(this).parent().addClass('selected');
				
				selectedItem = this;
			}
		});

		return selectedItem;
	}
	
	/**
	 * Set text for the trigger element.
	 * 
	 * @param {String} triggerElementId
	 * @param {Object} selectedItem
	 */
	static setOptionListTriggerElementText(triggerElementId, selectedItem) {
		$('#' + triggerElementId + ' .selection').html($(selectedItem).html());
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