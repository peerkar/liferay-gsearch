import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';

import devbridgeAutocomplete from './js/DevbridgeAutocomplete.es';  

import templates from './MiniView.soy';

/**
 * View component.
 */
class MiniView extends Component {
	
	/**
	 * @inheritDoc
	 */
	attached() {

		// Init autocomplete.

		if (this.autoCompleteEnabled) {
			this.initAutocomplete();
		}
	}
	
	/**
	 * Execute search
	 */
	doSearch() {

		let q = $(this.element.querySelector('#' + this.portletNamespace + 'MiniSearchField')).val();
		
		if (q.length < this.queryMinLength) {
			this.showMessage(Liferay.Language.get('min-character-count-is') + ' ' + 
					this.queryMinLength);
			return false;
		}
		
		let url = this.searchPageURL + "?q=" + q;

		window.location.replace(url);
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
		 
		let searchFieldElement = this.element.querySelector('#' + this.portletNamespace + 'MiniSearchField');
		 
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

				if (response) {
				    return {
	    				suggestions: $.map(response, function(item) {
				    		return {
				    			value: item, 
				    			data: item
				    		};
				        })
				    };
    			} else {
    				return {
    					suggestions: []
    				}
	    		}
			},
			triggerSelectOnValidInput: false
		});
	}	
	
	/**
	 * Show message
	 * 
	 * @param {String} title
	 */
	showMessage(title) {
		
		$(this.element.querySelector('#' + this.portletNamespace + 'MiniSearchFieldMessage')).tooltip({title: title}).tooltip('show');
		
		// Setting delay doesn't work on manual show
		
		setTimeout(function(){
			$('#' + elementId).tooltip('hide');
		}, 2000);		
	}	
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
MiniView.STATE = {

	autoCompleteEnabled: {
		value: false
	},
	autoCompleteRequestDelay: {
		value: 200
	},
	portletNamespace: {
		value: null
	},
	queryMinLength: {
		value: 3 
	},
	requestTimeout: {
		value: 200
	},
	searchPageURL: {
		value: null
	},
	suggestionsURL: {
		value: null
	}		
};

// Register component

Soy.register(MiniView, templates);

export default MiniView;