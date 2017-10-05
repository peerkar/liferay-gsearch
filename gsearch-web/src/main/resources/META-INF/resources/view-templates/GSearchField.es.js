import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import GSearchUtils from '../js/GSearchUtils.es';

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
		
		this.portletNamespace = opt_config.portletNamespace;

		this.setQueryParam('queryMinLength', opt_config.queryMinLength);
	}
	
	/**
	 * @inheritDoc
	 */
	prepareStateForRender(states) {
		
		// Check if we are getting selections from initially calling URL.
		
		this.checkCallURLSelections();

		// Set click events
			
		this.setClickEvents();	
	}

	/**
	 * Check and set query value based on calling URL.
	 */
	checkCallURLSelections() {
		
		if (!this.initialURLParameters) {
			return;
		}
		
		if (this.initialURLParameters['q']) {

			let keywords = this.initialURLParameters['q'].trim();

			if (this.validateKeywords(keywords)) {
				$('#' + this.portletNamespace + 'SearchField').val(keywords);
				this.setQueryParam("keywords", keywords, false);
			}
		}
	}
	
	/**
	 * Init autocomplete
	 */
	initAutoComplete() {
		// Coming soon
	}
	
	/**
	 * Do search
	 */
	doSearch() {

		let keywords = $('#' + this.portletNamespace + 'SearchField').val().trim();

		// Validate before setting
		
		if (this.validateKeywords(keywords)) {
			this.setQueryParam("keywords", keywords, true);
		}
	}
	
	/**
	 * Set click events
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
		
		if (keywords.length < this.getQueryParam('queryMinLength')) {
			this.showMessage(Liferay.Language.get('min-character-count-is') + ' ' + this.queryMinLength);
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
	initialURLParameters: {
		value: null
	},
	queryMinLength: {
		validator: core.isNumber
	},
	getQueryParam: {
		validator: core.isFunction
	},
	setQueryParam: {
		validator: core.isFunction
	}
};

// Register component

Soy.register(GSearchField, templates);

export default GSearchField;