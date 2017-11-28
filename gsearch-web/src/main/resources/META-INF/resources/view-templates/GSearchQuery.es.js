import core from 'metal/src/core';
import State from 'metal-state/src/State';
import Soy from 'metal-soy/src/Soy';
import Ajax from 'metal-ajax/src/Ajax';
import MultiMap from 'metal-multimap/src/MultiMap';

/**
 * GSearch Query class
 */
class GSearchQuery extends State {

	/**
	 * Build url string for the browser address bar.
	 */
	buildAddressBarURL() {
		
		let url = [location.protocol, '//', location.host, location.pathname, '?'].join('');		

		let _self = this;
		
		let params = '';
		
		Object.keys(this.parameters).forEach(function(key,index) {

			if (_self.parameters[key] != '' && typeof _self.parameters[key] !== 'undefined') {

				if (params.length > 0) {
					params = params.concat('&');
				}

				params = params.concat(key).concat('=').concat(_self.parameters[key]);
			}
		});
		
		url = url.concat(params);
		
		return encodeURI(url);
	}
	
	/**
	 * Build query params
	 */
	buildQueryParams() {

		let params = new MultiMap();

		let _self = this;

		Object.keys(this.parameters).forEach(function(key,index) {
			
			if (_self.parameters[key] != '' && typeof _self.parameters[key] !== 'undefined') {
				params.add(key, _self.parameters[key]);
			}
		});
		
		return params;
	}

	toString() {
		return JSON.stringify(this.values);
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchQuery.STATE = {
		
	parameters: {
		value: Object,
	},
	q: {
		value: ''
	},
	type: {
		value: ''
	}
	
}

export default GSearchQuery;
