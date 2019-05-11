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
	 * Add an option item to list of selections 
	 * 
	 * @param {String} component
	 * @param {String} optionMenu
	 * @param {String} paramName
	 * @param {String} isMultiValued
	 * @param {String} value
	 */
	static addToSelections(component, optionMenu, paramName, isMultiValued, anchor) {

		let text = null;
		let textElement = $(anchor).find('.text');
		
		if (textElement.length > 0) {
			text = $(textElement).html();
		} else {
			text = $(anchor).html();
		}
		
		let html = '<li>';
		html += text;
		html += '<a href="#">';
		html += '<span title="' + Liferay.Language.get('remove') + '" class="glyphicon glyphicon-remove-circle"></span>';
		html += '</a>';
		html += '</li>';
		
		let item = $(html);
		$('#' + component.portletNamespace + 'facet-selections ul').append(item);
		$(item).on('click', function(event) {
			GSearchUtils.unsetOptionListSelectedItem(component, optionMenu, paramName, isMultiValued, $(anchor).attr('data-value'));		
			event.preventDefault();
		});
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
	static bulkSetupOptionLists(menuWrapperName, menuClass, component, bindToSelections=false) {
		
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
				isMulti,
				bindToSelections
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
	static setupOptionList(component, optionMenu, paramName, isMultiValued = false, bindToSelections = false) {

		let values = component.getQueryParam(paramName);

		// Set initially selected item
		
		let selectedItems = GSearchUtils.setOptionListSelectedItems(component, optionMenu, paramName, values, isMultiValued, bindToSelections);

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
			
			let selectedItems = null;

			if (currentValues.indexOf(value) < 0) {

				component.setQueryParam(paramName, value, true, isMultiValued);

				selectedItems = GSearchUtils.setOptionListSelectedItems(component, optionMenu, paramName, [value], isMultiValued);
								
			} else {

				GSearchUtils.unsetOptionListSelectedItem(component, optionMenu, paramName, isMultiValued, value);
			}

			GSearchUtils.setOptionListTriggerElementText(optionMenu, null, paramName);

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
	static setOptionListSelectedItems(component, optionMenu, paramName, values, isMultiValued, bindToSelections) {

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
					
					if (bindToSelections) {
						GSearchUtils.addToSelections(component, optionMenu, paramName, isMultiValued, this);
					}
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
						
						if (bindToSelections) {
							GSearchUtils.addToSelections(component, optionMenu, paramName, isMultiValued, this);
						}
						
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
					
		let text = '';
		
		if (selectedItems && selectedItems.length > 1) {
			
			text = $(optionMenu).attr('data-multipleoption');

			// Fallback translation. See ResultItemBuilder.
			
			if (!text) {
				text = "multiple " + paramName.toLowerCase();
			}
			
			$(optionMenu).find(' .selection').html('[<i> ' + text + ' </i>]');
			
			return;
			
		} else if (selectedItems) {

			let textElement = $(selectedItems[0]).find('.text');
			
			if (textElement.length > 0) {
				text = $(textElement).html();
			} else {
				text = $(selectedItems[0]).html();
			}
			
		} else {
			
			let defaultItem = $(optionMenu).find('li.default a');
			
			text = defaultItem.html();
		}

		$(optionMenu).find('.selection').html(text);
	}
	
	/**
	 * Unset selected option item.
	 * 
	 * @param {String} optionMenu
	 * @param {String} value
	 */
	static unsetOptionListSelectedItem(component, optionMenu, paramName, isMultiValued, value) {
		
		component.setQueryParam(paramName, null, true, isMultiValued, value);

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