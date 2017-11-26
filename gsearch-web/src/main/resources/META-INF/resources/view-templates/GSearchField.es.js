import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import Ajax from 'metal-ajax/src/Ajax';
import MultiMap from 'metal-multimap/src/MultiMap';

import GSearchUtils from '../js/GSearchUtils.es';
import GSearchAutocomplete from '../js/GSearchAutocomplete.es';

import templates from './GSearchField.soy';

/**
 * GSearch searchfield component.
 */
class GSearchField extends Component {

	/**
	 * @inheritDoc
	 */
	constructor(opt_config, opt_parentElement) {
				
		super(opt_config, opt_parentElement);
		
		this.debug = opt_config.JSDebugEnabled;
		
		this.autoCompleteEnabled = opt_config.autoCompleteEnabled;

		this.autoCompleteRequestDelay = opt_config.autoCompleteRequestDelay
		
		this.portletNamespace = opt_config.portletNamespace;

		this.suggestionsURL = opt_config.suggestionsURL;

		this.queryMinLength, opt_config.queryMinLength;

		this.initialQueryParameters = opt_config.initialQueryParameters; 
	}
	
	/**
	 * @inheritDoc
	 */
	attached() {
		
		if (this.debug) {
			console.log("GSearchField.attached()");
		}
		
		// Check if we are getting query parameters from initially calling URL.
		
		this.checkInitialQueryParameters();

		// Set click events.
			
		this.setClickEvents();	

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
		
		if (this.initialQueryParameters['q']) {

			let keywords = this.initialQueryParameters['q'].trim();

			if (this.validateKeywords(keywords)) {
				$('#' + this.portletNamespace + 'SearchField').val(keywords);
				this.setQueryParam("q", keywords, false);
			}
		}
	}
	
	/**
	 * Init autocomplete / suggester.
	 */
	initAutocomplete() {
		
		let _self = this;
		
		let autocomplete = new GSearchAutocomplete ({
			elementClasses: 'gsearch-autocomplete-list',
			inputElement:document.querySelector('#' + this.portletNamespace + 'SearchField'),
			data: function(keywords) {
				if (keywords.length >= _self.queryMinLength &&
						!_self.isSuggesting && keywords.slice(-1) != ' ') {
					return _self.getSuggestions(keywords);
				} else {
					return;
				}
			},
			select: function(keywords, event) {
				$('#' + _self.portletNamespace + 'SearchField').val(keywords.text);
			}
		});
	}
		
	/**
	 * Do search
	 */
	doSearch() {

		let keywords = $('#' + this.portletNamespace + 'SearchField').val().trim();

		// Validate keywords.
		
		if (this.validateKeywords(keywords)) {
			this.setQueryParam("q", keywords, true);
		}
	}
	
	/**
	 * Get suggestions
	 */
	getSuggestions(keywords) {

		// Set this flag to control concurrent suggest requests (delay between requests).
		
		this.isSuggesting = true;
		
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

				_self.releaseSuggesting();

				return suggestions;

		}).catch(function(error) {

			_self.releaseSuggesting();

			console.log(error);
		});
	}
	
	/**
	 * Release isSuggesting flag.
	 */
	releaseSuggesting() {	
		
		let _self = this;
		
		setTimeout(function(){
			_self.isSuggesting = false;
		}, this.autoCompleteRequestDelay);	
	}	
	
	/**
	 * Set click events.
	 */
	setClickEvents() {

		let _self = this;

		// Bind button click event

		$('#' + this.portletNamespace + 'SearchButton').on('click', function (event) {
			_self.doSearch();
		});

		// Bind search field keypress event.

		$('#' + this.portletNamespace + 'SearchField').keypress(function (event) {
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
		
		let elementId = this.portletNamespace + 'SearchFieldMessage';

		$('#' + elementId).tooltip({title: title}).tooltip('show');
		
		// Setting delay doesn't work on manual show
		
		setTimeout(function(){
			$('#' + elementId).tooltip('hide');
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
	isSuggesting: {
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
	}
};

// Register component

Soy.register(GSearchField, templates);

export default GSearchField;