/**
 * GSearch utility class
 */

class GSearchUtils {
	
	/**
	 * Check if object is empty
	 * 
 	 * @param {Object} component
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
	 * @param {String} menuWrapperName
	 * @param {String} menuClass
	 * @param {Object} component
 	 *
	 */
	static bulkSetupOptionLists(menuWrapperName, menuClass, component) {
		
		let menuElement = $(component.element.querySelectorAll('#' + component.portletNamespace + menuWrapperName + ' .' + menuClass));
		
		if (menuElement.length === 0) {
			return;
		}

		menuElement.each(function() {
			
			let paramName = $(this).attr('data-paramname');
			let isMultiValued = $(this).attr('data-ismultivalued');
			
			// Convert to boolean
			
			let isMulti = (isMultiValued == 'true');

			GSearchUtils.setupOptionList(
				component, 
				this,
				paramName,
				isMulti
			);

			/*
			 
			// Dropdown animation  
			 
			$(this).on('show.bs.dropdown', function() {
				$(this).find('.dropdown-menu').first().stop(true, true).slideDown(250);
			});

			$(this).on('hide.bs.dropdown', function() {
				$(this).find('.dropdown-menu').first().stop(true, true).slideUp(200);
			});
			
			*/
		});			
	}
	
	/**
	 * Set initial query parameters.
	 * 
	 * @param {String} parameterArray
	 * @param {Object} queryParamSetter
	 * 
	 */
	static setInitialQueryParameters(initialValues, keyArray, setQueryParam) {
		
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
					setQueryParam(field, value, false);

				} else {
					
					// Reset possibly cached state
					
					setQueryParam(field, '', false);
				}				
			}
		}
	}	
		
	/**
	 * Setup option list.
	 *
	 * @param {Object} component
	 * @param {String} optionMenu
	 * @param {String} paramName
	 * @param {String} initialValue
	 */
	static setupOptionList(component, optionMenu, paramName, isMultiValued = false) {

		let values = component.getQueryParam(paramName);

		// Set initially selected item
		
		let selectedItems = GSearchUtils.setOptionListSelectedItems(optionMenu, values, isMultiValued);

		// Set text
		
		if (selectedItems.length > 0) {
			GSearchUtils.setOptionListTriggerElementText(optionMenu, selectedItems, paramName);
		}
		
		// Set click events
		
		GSearchUtils.setOptionListClickEvents(component, optionMenu, paramName, isMultiValued) 
	}
	
	/**
	 * Set click events for options list for setting query params.
	 *
	 * @param {String} component
	 * @param {String} optionMenu
	 * @param {String} paramName
	 * @param {Boolean} isMultiValued
	 */
	static setOptionListClickEvents(component, optionMenu, paramName, isMultiValued) {
		
		$(optionMenu).find('li a').on('click', function(event) {

			let currentValues = component.getQueryParam(paramName);
			
			let value = $(this).attr('data-value');

			if (currentValues.indexOf(value) < 0) {
				
				component.setQueryParam(paramName, value, true, isMultiValued);

				let selectedItems = GSearchUtils.setOptionListSelectedItems(optionMenu, [value], isMultiValued);
				
				if (selectedItems.length > 0) {
					
					GSearchUtils.setOptionListTriggerElementText(optionMenu, selectedItems, paramName);
				}
				
			} else {

				component.setQueryParam(paramName, null, true, isMultiValued, value);

				GSearchUtils.unsetOptionListSelectedItem(optionMenu, value);
			}
			event.preventDefault();
		});			
	}
	
	/**
	 * Set selected option item.
	 * 
	 * @param {String} optionMenu
	 * @param {Object} values
	 * @param {Boolean} isMultiValued
	 */
	static setOptionListSelectedItems(optionMenu, values, isMultiValued) {

		let selectedItems = [];

		let defaultItem = $(optionMenu).find('li.default a');
				
		let valueFound = false;
		
		$(optionMenu).find('li a').each(function() {
			
			if (!isMultiValued) {

				if ($(this).attr('data-value') == values[0]) {
					
					valueFound = true;

					$(optionMenu).find('li').removeClass('selected');
					
					$(this).parent().addClass('selected');
					
					selectedItems.push(this);
					
					return false;
				}

			} else {
				
				let length = values.length;
				
				for (let i = 0; i < length; i++) {

					let value = values[i];
				
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
	 * @param {String} optionMenu
	 * @param {Object} selectedItems
	 * @param {String} paramName
	 */
	static setOptionListTriggerElementText(optionMenu, selectedItems, paramName) {
						
		if (selectedItems.length > 1) {
			
			let html = $(optionMenu).attr('data-multipleoption');

			// Fallback translation. See ResultItemBuilder.
			
			if (!html) {
				html = "multiple " + paramName.toLowerCase();
			}
			
			$(optionMenu).find(' .selection').html('[<i> ' + html + ' </i>]');
			
			return;
		}

		let textElement = $(selectedItems[0]).find('.text');
		
		let text = null;
		
		if (textElement.length > 0) {
			text = $(textElement).html();
		} else {
			text = $(selectedItems[0]).html();
		}

		$(optionMenu).find('.selection').html(text);
	}
	
	/**
	 * Unset selected option item.
	 * 
	 * @param {String} optionMenu
	 * @param {String} value
	 */
	static unsetOptionListSelectedItem(optionMenu, value) {
		
		$(optionMenu).find('li a').each(function() {
				
			if ($(this).attr('data-value') == value) {

				$(this).parent().removeClass('selected');
				
				return false;
			}
		});

		// If there are no more selections mark the default item ("any") as selected.

		if ($(optionMenu).find('li.selected').length === 0) {
			$(optionMenu).find('li.default').addClass('selected');
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