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

		let params = '';
		
		for (let parameter of this.parameters) {
			
			if (this.isValueNotNull(parameter.value)) {

				if (params.length > 0) {
					params = params.concat('&');
				}
			
				params = params.concat(parameter.key).concat('=').concat(parameter.value);
			}
		}
		
		url = url.concat(params);
		
		return encodeURI(url);
	}
	
	/**
	 * Build query parameters.
	 */
	buildQueryParams() {

		let params = new MultiMap();

		for (let parameter of this.parameters) {
			
			if (this.isValueNotNull(parameter.value)) {
				
				params.add(parameter.key, parameter.value);
			}
		}
		return params;
	}
	
	/**
	 * Clean facet filter parameters 
	 *
     * Check selections to not to get into deadlock.
	 *  
	 */
	cleanFilterParameters() {

		// Clear filters if clearing parameters are changed
		
		let clear = false;
		
		for (let param of this.queryClearingParameters) {
			
			if (this.getOldParameterValue(param) &&
					this.getOldParameterValue(param) != this.getParameterValue(param) && 
					this.isParameterNotNull(param)) {
				
				clear = true;
			}
		}
			
		if (clear) {

			let oldParameters = this.parameters;
						
			this.parameters = [];
			
			// Copy old parameters 
			
			for (let oldParameter of oldParameters) {
				
				if (this.basicParameters.indexOf(oldParameter.key) > -1 && typeof oldParameter.value !== 'undefined') {
					this.setParameter(oldParameter.key, oldParameter.value);
				}
			}
				
			// Persist current values
			
			for (let param of this.queryClearingParameters) {
				this.setOldParameter(param, this.getParameterValue(param));
			}

		} else if (this.isParameterNotNull('type')) {

			this.setOldParameter('type', this.getParameterValue('type'));
		}

		// Should we reset paging
		
		if (this.clearPaging) {
			
			this.setParameter('start', '0');
			this.clearPaging = false;
		}
		
	}		

	/**
	 * Get old parameter value.
	 *
 	 * @param {String} key
	 */
	getOldParameterValue(key) {
		return this.getValue(key, this.oldParameters);
	}
	
	/**
	 * Get old parameter value array.
	 *
 	 * @param {String} key
	 */
	getOldParameterValues(key) {
		return this.getValues(key, this.oldParameters);
	}
	
	/**
	 * Get parameter value
	 *
 	 * @param {String} key
	 */
	getParameterValue(key) {
		return this.getValue(key, this.parameters);
	}

	/**
	 * Get parameter values array.
	 *
 	 * @param {String} key
	 */
	getParameterValues(key) {
		return this.getValues(key, this.parameters);
	}
	
	/**
	 * Get single parameter value
	 * 
 	 * @param {String} key
	 */
	getValue(key, valueArray) {
		
		let values = this.getValues(key, valueArray);
		
		if (values.length > 0) {
			return values[0];
		} 
		
		return null;
	}
	
	/**
	 * Get parameter values.
	 * 
 	 * @param {String} key
	 */
	getValues(key, valueArray) {
		
		// Check if this key value pair is set already.
		
		let values = [];
		
		for (let parameter of valueArray) {

			if (parameter.key == key) {
				values.push(parameter.value);
			}
		}
		
		return values;
	}
	
	/**
	 * Check if parameter is not null;
	 * 
 	 * @param {String} key
	 */
	isParameterNotNull(key) {

		for (let parameter of this.parameters) {

			if (parameter.key == key) {
				return this.isValueNotNull(parameter.value);
			}
		}
		return false;
	}
	
	/**
	 * Is value not null
	 * 
	 * @param{String} value
	 */
	isValueNotNull(value) {
		return value && typeof value !== 'undefined' && value.length > 0;
	}	
	
	/**
	 * Remove parameter.
	 *  
 	 * @param {String} key
 	 * @param {String} value
	 */
	removeParameter(key, value) {
		this.filters = this.filters.filter(item => (item.key == key && item.value == value));
	}

	/**
	 * Set old parameter value.
	 * 
 	 * @param {String} key
 	 * @param {String} value
	 * 
	 */
	setOldParameter(key, value) {
		this.setValue(key, value, this.oldParameters)
	}

	/**
	 * Set single valued parameter.
	 * 
 	 * @param {String} key
 	 * @param {String} value
	 * 
	 */
	setParameter(key, value) {
		
		this.setValue(key, value, this.parameters)
	}

	/**
	 * Set multi valued parameter.
	 * 
 	 * @param {String} key
 	 * @param {String} value
 	 * @param {String} previousValue
	 * 
	 */
	setMultiValueParameter(key, value, previousValue) {

		let newValues = [];

		for (let parameter of this.parameters) {

			// Add other parameters
			
			if (parameter.key != key && this.isValueNotNull(parameter.value)) {

				newValues.push(parameter)
				
			} else 	if (parameter.key == key && this.isValueNotNull(value) && parameter.value != value) {

				// Add other values for the same parameter
				
				newValues.push(parameter)


			} else 	if (parameter.key == key && !this.isValueNotNull(value) && this.isValueNotNull(previousValue) && 
					parameter.value != previousValue) {

				// Add other values for the same parameter
				
				newValues.push(parameter)

			}
		}

		// Add new values for for the same parameter
		
		if (this.isValueNotNull(value)) {
		
			var param = new Object();
			
			param.key = key;
			param.value = value;
		
			newValues.push(param)
		}
		
		// Do we have to clear the paging parameter
		
		if (this.transparentParameters.indexOf(key) < 0) {
			this.clearPaging = true;
		}
		
		this.parameters = newValues;
	}	
	
	/**
	 * Set single valued parameters
	 * 
 	 * @param {String} key
 	 * @param {String} value
	 */
	setValue(key, value, valueArray) {
		
		for (let parameter of valueArray) {

			if (parameter.key == key) {

				parameter.value = value;

				return;
			}	
		}
		
		// Set new value
		
		var param = new Object();
		param.key = key;
		param.value = value;
		
		valueArray.push(param);
		
		// Do we have to clear the paging parameter
		
		if (this.transparentParameters.indexOf(key) < 0) {
			this.clearPaging = true;
		}
	}
	
	/**
	 * To string
	 */
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
	oldParameters: {
		value: []
	},
	parameters: {
		value: []
	},
	queryClearingParameters: {
		value: ['q', 'type']
	},
	basicParameters: {
		value: ['q', 'type', 'resultsLayout', 'sortField', 'sortDirection']
	},
	transparentParameters: {
		value: ['resultsLayout', 'sortField', 'sortDirection']
	},
	clearPaging: {
		value: false
	}
}

export default GSearchQuery;
