import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import Ajax from 'metal-ajax/src/Ajax';
import MultiMap from 'metal-multimap/src/MultiMap';

import devbridgeAutocomplete from '../js/DevbridgeAutocomplete.es';  
import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchField.soy';

/**
 * GSearch searchfield component.
 */
class GSearchField extends Component {
	
	/**
	 * @inheritDoc
	 */
	attached() {
				
		if (this.debug) {
			console.log("GSearchField.attached()");
		}

		// Check if we are getting query parameters from initially calling URL.
		
		this.checkInitialQueryParameters();

		// Init autocomplete.

		if (this.autoCompleteEnabled) {
			this.initAutocomplete();
		}
	}
		
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchField.rendered()");
		}
	}

	/**
	 * Check intial query parameter.
	 */
	checkInitialQueryParameters() {
		
		let keywords = null;
		
		let q = this.initialQueryParameters['q'];
		
		if (q) {
			keywords = q[0];
		}

		if (keywords) { 
 			
			if (this.validateKeywords(keywords)) {
				this.element.querySelector('#' + this.portletNamespace + 'SearchField').value = keywords;
				this.setQueryParam('q', keywords, false, false);
			}
		}
	}
	
	/**
	 * Handle keyup  events.
	 */
	handleKeyUp(event) {

        var keycode = (event.keyCode ? event.keyCode : event.which);
        
        if(keycode === 13){
			this.doSearch();
	    }
	}	
	
	/**
	 * Init autocomplete / suggester.
	 */
	initAutocomplete() {
		
		let _self = this;
		
		let searchFieldElement = this.element.querySelector('#' + this.portletNamespace + 'SearchField');
				 
		$(searchFieldElement).devbridgeAutocomplete({
			dataType: 'json',
			deferRequestBy: _self.autoCompleteRequestDelay,
			minChars: _self.queryMinLength,
			noCache: false,
		    onSelect: function (suggestion) {
				_self.doSearch();
		    },
			paramName: 'q',
			serviceUrl: _self.suggestionsURL,
			transformResult: function(response) {

				if (response && !_self.isSearching) {
				    return {
	    				suggestions: $.map(response, function(item) {
				    		return {
				    			value: item, 
				    			data: item
				    		};
				        })
				    };
				} else {
					_self.isSearching = false;

    				return {
    					suggestions: []
    				}
	    		}
			},
			triggerSelectOnValidInput: false
		});
	}
		
	/**
	 * Do search
	 */
	doSearch() {

		// It can happen that search is triggered before autocompletion request has been finished.
		// This flag prevents transforming autocomplete request after search is done.
		// Effectively it prevents autocomplete suggestions box to open after search.
		
		this.isSearching = true;
		
		let searchFieldElement = this.element.querySelector('#' + this.portletNamespace + 'SearchField');

		let keywords = $(searchFieldElement).val().trim();

		// Validate keywords.
		
		if (this.validateKeywords(keywords)) {
			this.setQueryParam('q', keywords, true, false);
		}
	}
	
	/**
	 * Get suggestions
	 * 
	 * @deprecated
	 */
	getSuggestions(keywords) {
		
		let _self = this;
		
		let params = new MultiMap();
		
		params.add(this.portletNamespace + 'q', keywords);
		
		return Ajax.request(
			this.suggestionsURL,
			'GET',
			null,
			null,
			params,
			this.requestTimeout
		).then((response) => {
				let suggestions = JSON.parse(response.responseText);

				return suggestions;

		}).catch(function(error) {

			console.log(error);
		});
	}
		
	/**
	 * Show message
	 * 
	 * @param {String} title
	 */
	showMessage(title) {
		
		let messageElement = this.element.querySelector('#' + this.portletNamespace + 'SearchFieldMessage');
		
		$(messageElement).tooltip({title: title}).tooltip('show');
		
		// Setting delay doesn't work on manual show
		
		setTimeout(function(){
			$(messageElement).tooltip('hide');
		}, 2000);		
	}
	
	/**
	 * Validate keywords. 
	 * 
	 * First phase of validation here, second in the query object
	 * and third in the backend.
	 *
	 * @param {String} keywords
	 */
	validateKeywords(keywords) {

		// Minimum length is defined in portlet configuration
		
		if (keywords.length < this.queryMinLength) {
			this.showMessage(Liferay.Language.get('min-character-count-is') + ' ' + 
					this.queryMinLength);
			return false;
		}
		
		return true;
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchField.STATE = {
	autoCompleteEnabled: {
		value: false
	},
	autoCompleteRequestDelay: {
		value: 500
	},
	debug: {
		value: false
	},
	initialQueryParameters: {
		value: null
	},
	isSearching: {
		internal: true,
		value: false
	},
	getQueryParam: {
		validator: core.isFunction
	},
	queryMinLength: {
		value: 3
	},
	setQueryParam: {
		validator: core.isFunction
	},
	suggestionsURL: {
		value: null
	}
};

// Register component

Soy.register(GSearchField, templates);

export default GSearchField;
