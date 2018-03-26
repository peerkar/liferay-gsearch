import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import Mark from '../js/mark.es';

import templates from './GSearchResults.soy';

/**
 * GSearch results component.
 */
class GSearchResults extends Component {
	
	/**
	 * @inheritDoc
	 * 
	 */
	constructor(opt_config, opt_parentElement) {
	
		super(opt_config, opt_parentElement);
		
		this.debug = opt_config.JSDebugEnabled;

		this.portletNamespace = opt_config.portletNamespace;
		
		this.assetTagParam = opt_config.assetTagParam;

		this.showAssetTags = opt_config.showAssetTags;
	}
	
	/**
	 * Highlight keywords in results
	 * 
	 */
	doHighlightKeywords() {
		
		if (this.results.items.length > 0 & this.results.meta.queryTerms.length > 0) {
			new Mark($('#' + this.portletNamespace + 'SearchResults .item .highlightable').toArray()).mark(this.results.meta.queryTerms);
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
		
		this.doHighlightKeywords();
		
		// Set up tags (if present) links
		
		if (this.showAssetTags) {
			this.setupTagLinks();
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
	
	/**
	 * @inheritDoc 
	 */
	shouldUpdate(changes, propsChanges) {

		if (this.debug) {
			console.log("GSearchResults.shouldUpdate()");
		}		

		$('#' + this.portletNamespace + 'SearchResults .item .tags .tag').each(function() {	
			$(this).unbind();
		});

		return true;
    }
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchResults.STATE = {
	results: {
		value: null
	},
	getQueryParam: {
		validator: core.isFunction
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