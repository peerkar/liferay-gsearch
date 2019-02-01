import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';
import Ajax from 'metal-ajax/src/Ajax';
import MultiMap from 'metal-multimap/src/MultiMap';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchFacets.soy';

/**
 * GSearch facets component.
 */
class GSearchFacets extends Component {

	/**
	 * @inheritDoc
	 */
	attached() {

		if (this.debug) {
			console.log("GSearchFacets.attached()");
		}
		
		// Set initial query parameters from calling url.
		
		GSearchUtils.setInitialQueryParameters(
			this.initialQueryParameters, 
			this.facetFields, 
			this.setQueryParam
		);		
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchFacets.rendered()");
		}
		
		// Setup options lists.

		GSearchUtils.bulkSetupOptionLists(
			'Facets', 
			'optionmenu', 
			this,
			true
		);
		
		// Update (static) filter menu counts if necessary.
		
		this.updateFilterFacetCounts();
				
	}
	
	/**
	 * @inheritDoc 
	 */
	shouldUpdate(changes, propsChanges) {

		if (this.debug) {
			console.log("GSearchFacets.shouldUpdate()");
		}		

		$('#' + this.portletNamespace + 'Facets .optionmenu').remove();

		return true;
    }		
	
	
	/**
	 * Update (static) filter menu facet counts. 
	 */
	updateFilterFacetCounts() {		

		let _self = this;
		
		// Clear current values. Notice that we have to reference by parent element.
		
		$('#' + this.portletNamespace + 'BasicFilters .countable-static-facets-list li .count').html('');

		if (!this.facets || this.facets.length == 0) {
			return;
		}
		
		$('#' + this.portletNamespace + 'BasicFilters .countable-static-facets-list').each(function() {
			
			let values = null;
			
			let length = _self.facets.length;

			for (let i = 0; i < length; i++) {

				if(_self.facets[i].field_name == $(this).attr('data-facetname')) {
					
					values = _self.facets[i].values;
					break;
				}
			}

			if (values) {

				let valueCount = values.length; 
				
				for (let i = 0; i < valueCount; i++) {

					let term =  values[i].term;
					let frequency =  values[i].frequency;

					let element = $('#' + _self.portletNamespace + 'BasicFilters .countable-static-facets-list li a[data-facet="' + term + '"]');
					
					if (element) {
						$(element).find('.count').html('(' + frequency + ')');
					}
				}
			}
		});
	}
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */
GSearchFacets.STATE = {
	debug: {
		value: false
	},
	facetFields: {
		value: null
	},
	getQueryParam: {
		validator: core.isFunction
	},
	initialQueryParameters: {
		value: null
	},
	setQueryParam: {
		validator: core.isFunction
	}
};

// Register component

Soy.register(GSearchFacets, templates);

export default GSearchFacets;