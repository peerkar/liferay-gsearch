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
		
		let length = this.parameters.length;
		
		for (let i = 0; i < length; i++) {

			let parameter = this.parameters[i];
		
			if (this.isValueNotNull(parameter.value)) {

				if (params.length > 0) {
					params = params.concat('&');
				}

				let value = parameter.value.replace(/ /g, '%20');
				value = parameter.value.replace(/\"/g, '%22');
				
				params = params.concat(parameter.key).concat('=').concat(value);
			}
		}
		
		url = url.concat(params);

		return url;
	}
	
	/**
	 * Build query parameters.
	 */
	buildQueryParams() {

		let params = new MultiMap();

		let length = this.parameters.length;
		
		for (let i = 0; i < length; i++) {

			let parameter = this.parameters[i];
					
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
		
		let length = this.queryClearingParameters.length;
		
		for (let i = 0; i < length; i++) {

			let param = this.queryClearingParameters[i];
		
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

			let length2 = oldParameters.length;
			
			for (let i = 0; i < length2; i++) {

				let oldParameter = oldParameters[i];
							
				if (this.basicParameters.indexOf(oldParameter.key) > -1 && typeof oldParameter.value !== 'undefined') {
					this.setParameter(oldParameter.key, oldParameter.value);
				}
			}
			
			// This flags the facet menus to be updated (of use only if menus are persisted).
			
			this.needsFacetsUpdate = true;
			
		} else {

			this.needsFacetsUpdate = false;
		}

		// Persist current values

		let length3 = this.queryClearingParameters.length;
		
		for (let i = 0; i < length3; i++) {

			let param = this.queryClearingParameters[i];
		
			this.setOldParameter(param, this.getParameterValue(param));
		}

		// Should we reset paging
		
		
		if (this.clearPaging && !this.isInitialQuery) {
			
			this.setParameter('start', '0');
			this.clearPaging = false;
		} else {
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

	getMultiParameterValue(key) {
		let vals = [];
		for(let i = 0; i < this.parameters.length; i++) {
			let param = this.parameters[i];
			if(param.key === key) {
				vals.push(param.value);
			}
		}
		return vals.join(',');
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
		
		let length = valueArray.length;
		
		for (let i = 0; i < length; i++) {

			let parameter = valueArray[i];

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

		let length = this.parameters.length;
		
		for (let i = 0; i < length; i++) {

			let parameter = this.parameters[i];
		
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

		let length = this.parameters.length;
		
		for (let i = 0; i < length; i++) {

			let parameter = this.parameters[i];
		
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
		
		let length = valueArray.length;
		
		for (let i = 0; i < length; i++) {

			let parameter = valueArray[i];
		
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
		value: ['q']
	},
	basicParameters: {
		value: ['q', 'resultsLayout', 'sortField', 'sortDirection']
	},
	isInitialQuery: {
		value: false
	},
	needsFacetsUpdate: {
		value: false
	},
	transparentParameters: {
		value: ['resultsLayout', 'sortField', 'sortDirection']
	},
	clearPaging: {
		value: false
	}
};

export default GSearchQuery;