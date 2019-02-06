import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import GSearchQuerySuggestions from './GSearchQuerySuggestions.soy';
import GSearchImageResultLayout from './GSearchImageResultLayout.soy';
import GSearchMapsResultLayout from './GSearchMapsResultLayout.es';
import templates from './GSearchResults.soy';

/**
 * GSearch results component.
 */
class GSearchResults extends Component {
		
	/**
	 * @inheritDoc
	 * 
	 */
	attached() {		
		
		if (this.debug) {
			console.log("GSearchResults.attached()");
		}
	}
	
	decodeEntities(encodedString) {
	    var textArea = document.createElement('textarea');
	    textArea.innerHTML = encodedString;
	    return textArea.value;
	}
	
	/**
	 * Decode highlight tags in results.
	 * 
	 */
	decodeHighlightHTML() {
		
		let _self = this;
		
		if (this.results.items.length) {
			
			$('#' + this.portletNamespace + 'SearchResults .item .highlightable').each(function() {
				$(this).html(_self.decodeEntities($(this).html()));
			});
		}
	}
	
	/**
	 * @inheritDoc
	 * 
	 */
	rendered() {		
		
		if (this.debug) {
			console.log("GSearchResults.rendered()");
		}
		
		if (this.results) {

			this.decodeHighlightHTML();
		
			// Set up tags (if present) links
			
			if (this.showAssetTags) {
				this.setupTagLinks();
			}
		}
	}
	
	/**
	 * Set up asset tags links
	 */
	setupTagLinks() {

		let _self = this;

		let tagsValues = _self.getQueryParam('assetTagNames');

		$('#' + this.portletNamespace + 'SearchResults .item .tags .tag').each(function() {

			if (tagsValues && tagsValues.indexOf($(this).html()) > -1) {
				$(this).addClass('active');
			}
			
			$(this).on('click', function(event) {
				
				// If this is not active set the param, if not then unset.
				
				if ($(this).hasClass('active')) {
					_self.setQueryParam(_self.assetTagParam, null, true, true, $(this).html());
					$(this).removeClass('active');
				} else {
					_self.setQueryParam(_self.assetTagParam, $(this).html(), true, true);
				}
			}); 
		}); 
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchResults.STATE = {
		
	assetTagParam: {
		value: null
	},
	debug: {
		value: false
	},
	getQueryParam: {
		validator: core.isFunction
	},
	portletNamespace: {
		value: null
	},
	resultLayoutOptions: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	},
	showAssetTags: {
		value: false
	}
};

// Register component

Soy.register(GSearchResults, templates);

export default GSearchResults;	