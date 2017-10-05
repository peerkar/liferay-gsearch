/**
 * GSearch utility class
 */
class GSearchUtils {

	/**
	 * Set initially selected item. Return true if found and correctly set, false if not.
	 * 
	 * @param {String} optionElementId
	 * @param {String} triggerElementId
	 * @param {String} value
	 */
	static setInitialOption(optionElementId, value) {

		let found = false;
		
		$('#' + optionElementId + ' li a').each(function() {

			if ($(this).attr('data-value') == value) {
				
				$('#' + optionElementId + ' li').removeClass('selected');
				$(this).parent().addClass('selected');
				found = true;
			}
		});
		
		return found;
	}
	
	/**
	 * Set dropdown click events for setting query params.
	 *
	 * @param {String} optionElementId
	 * @param {String} triggerElementId
	 * @param {String} queryParamGetter
	 * @param {String} queryParamSetter
	 * @param {String} queryParam
	 */
	static setDropDownClickEvents(optionElementId, triggerElementId, queryParamGetter, queryParamSetter, queryParam) {
				
		$('#' + optionElementId + ' li a').on('click', function(event) {

			let value = $(this).attr('data-value');
			
			if (value != queryParamGetter(queryParam)) {

				queryParamSetter(queryParam, value);

				GSearchUtils.setDropDownSelectedItem(optionElementId, triggerElementId, this);
			}
			event.preventDefault();
		});			
	}
	
	/**
	 * Set dropdown selected item (text).
	 * 
	 * @param {String} optionElementId
	 * @param {String} triggerElementId
	 * @param {Object} selectedItem
	 */
	static setDropDownSelectedItem(optionElementId, triggerElementId, selectedItem=null) {

		if (selectedItem == null && $('#' + optionElementId + ' .selected a')) {
			selectedItem = $('#' + optionElementId + ' .selected a');
		}

		if (selectedItem != null) {
			$('#' + optionElementId + ' li').removeClass('selected');
			$(selectedItem).parent().addClass('selected');
			$('#' + triggerElementId + ' .selection').html($(selectedItem).html());
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