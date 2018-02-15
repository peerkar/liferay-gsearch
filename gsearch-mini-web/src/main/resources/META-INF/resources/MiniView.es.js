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
	constructor(opt_config) {

		super(opt_config);

		this.autoCompleteEnabled = opt_config.autoCompleteEnabled;

		this.autoCompleteRequestDelay = opt_config.autoCompleteRequestDelay

		this.portletNamespace = opt_config.portletNamespace;
		
		this.queryMinLength = opt_config.queryMinLength;

		this.requestTimeout = opt_config.requestTimeout;

		this.searchPageURL = opt_config.searchPageURL;

		this.suggestionsURL = opt_config.suggestionsURL;
		
		// Init autocomplete.

		if (this.autoCompleteEnabled) {
			this.initAutocomplete();
		}
		
		// Set click events
		
		this.setClickEvents();
	}

	/**
	 * Execute search
	 */
	doSearch() {

		let q = $('#' + this.portletNamespace + 'MiniSearchField').val();
		
		if (q.length < this.queryMinLength) {
			this.showMessage(Liferay.Language.get('min-character-count-is') + ' ' + 
					this.queryMinLength);
			return false;
		}
		
		let url = this.searchPageURL + "?q=" + q;

		window.location.replace(url);
	}
	
	/**
	 * Init autocomplete / suggester.
	 */
	initAutocomplete() {

		let _self = this;
		 
		$('#' + this.portletNamespace + 'MiniSearchField').devbridgeAutocomplete({
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
	 * Set click events.
	 */
	setClickEvents() {

		let _self = this;

		// Bind button click event

		$('#' + this.portletNamespace + 'MiniSearchButton').on('click', function (event) {
			_self.doSearch();
		});

		// Bind search field keypress event.

		$('#' + this.portletNamespace + 'MiniSearchField').keypress(function (event) {
	        var keycode = (event.keyCode ? event.keyCode : event.which);
	        if(keycode === 13){
				_self.doSearch();
	        }
	    });	
	}
	
	/**
	 * Show message
	 * 
	 * @param {String} title
	 */
	showMessage(title) {
		
		let elementId = this.portletNamespace + 'MiniSearchFieldMessage';

		$('#' + elementId).tooltip({title: title}).tooltip('show');
		
		// Setting delay doesn't work on manual show
		
		setTimeout(function(){
			$('#' + elementId).tooltip('hide');
		}, 2000);		
	}	
}

// Register component

Soy.register(MiniView, templates);

export default MiniView;