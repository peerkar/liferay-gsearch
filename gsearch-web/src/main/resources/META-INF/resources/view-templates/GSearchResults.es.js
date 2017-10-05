import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import templates from './GSearchResults.soy';

/**
 * GSearchResults component.
 */
class GSearchResults extends Component {
	
	/**
	 * @inheritDoc
	 * 
	 */
	constructor(opt_config, opt_parentElement) {
	
		super(opt_config, opt_parentElement);
		this.portletNamespace = opt_config.portletNamespace;
	}

	/**
	 * Highlight query terms in results
	 * 
	 */
	doHighlight() {
		
		let self = this;

		require('gsearch-web/js/mark', function(Mark) {
			if (self.results.items.length > 0 & self.results.meta.queryTerms.length > 0) {
				new Mark($('#' + self.portletNamespace + 'SearchResults .item .highlightable').toArray()).mark(self.results.meta.queryTerms);
			}
		});
	}
	
	/**
	 * @inheritDoc
	 * 
	 */
	rendered() {
		this.doHighlight();
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
	}
};

// Register component

Soy.register(GSearchResults, templates);

export default GSearchResults;	