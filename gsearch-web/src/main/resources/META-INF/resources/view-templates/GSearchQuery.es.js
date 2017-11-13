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
		
		let url = [location.protocol, '//', location.host, location.pathname].join('');		

		url = url
			.concat('?q=')
			.concat(this.getKeywords())
			.concat('&scope=').concat(this.getScopeFilter())
			.concat('&time=').concat(this.getTimeFilter())
			.concat('&type=').concat(this.getTypeFilter())
			.concat('&start=').concat(this.getStart())
			.concat('&sortField=').concat(this.getSortField())
			.concat('&sortDirection=').concat(this.getSortDirection());
		
		if (this.getResultsLayout() != '') {
			url = url.concat('&resultsLayout=').concat(this.getResultsLayout())
		}
		
		if(this.getDocumentFormatFilter() != '') {
			url = url.concat('&df=').concat(this.getDocumentFormatFilter())
		}

		if(this.getDocumentTypeFilter() != '') {
			url = url.concat('&dt=').concat(this.getDocumentTypeFilter())
		}

		if(this.getWebContentStructureFilter() != '') {
			url = url.concat('&wcs=').concat(this.getWebContentStructureFilter())
		}
		
		return encodeURI(url);
	}
	
	/**
	 * Build query params
	 */
	buildQueryParams() {
		let params = new MultiMap();
		params.add('q', this.getKeywords());
		params.add('scope', this.getScopeFilter());
		params.add('time', this.getTimeFilter());
		params.add('type', this.getTypeFilter());
		params.add('start', this.getStart());
		params.add('sortField', this.getSortField());
		params.add('sortDirection', this.getSortDirection());

		if (this.getResultsLayout() != '') {
			params.add('resultsLayout', this.getResultsLayout());
		}
		
		if(this.getDocumentFormatFilter() != '') {
			params.add('df', this.getDocumentFormatFilter());
		}

		if(this.getDocumentTypeFilter() != '') {
			params.add('dt', this.getDocumentTypeFilter());
		}

		if(this.getWebContentStructureFilter() != '') {
			params.add('wcs', this.getWebContentStructureFilter());
		}
		
		return params;
	}

	getStart()  {
    	return this.start;
    }
   
    setStart(start) {
    	this.start = start;
    }

	getSortField()  {
    	return this.sortField;
    }
   
    setSortField(sortField) {
    	this.sortField = sortField;
    }

    getSortDirection()  {
    	return this.sortDirection;
    }
   
    setSortDirection(sortDirection) {
    	this.sortDirection = sortDirection;
    }
    
    getKeywords()  {
    	return this.q;
    }
    
	setKeywords(q)  {
    	this.q = q;
    }

	getQueryMinLength() {
		return this.queryMinLength;
	}

	setQueryMinLength(queryMinLength) {
		this.queryMinLength = queryMinLength;
	}

    getDocumentFormatFilter()  {
    	return this.df;
    }
    
    setDocumentFormatFilter(df)  {
		this.df = df;
    }
    
    getDocumentTypeFilter()  {
    	return this.dt;
    }
    
    setDocumentTypeFilter(dt)  {
		this.dt = dt;
    }
    
    getResultsLayout()  {
    	return this.resultsLayout;
    }
    
    setResultsLayout(resultsLayout)  {
		this.resultsLayout = resultsLayout;
    }

    getScopeFilter()  {
    	return this.scope;
    }
    
    setScopeFilter(scope)  {
		this.scope = scope;
    }
    
    getTimeFilter()  {
    	return this.time;
    }
    
    setTimeFilter(time) {
    	this.time = time;
	}
    
    getTypeFilter()  {
    	return this.type;
    }
	
	setTypeFilter(type) {
		this.type = type;
	}
	
    getWebContentStructureFilter()  {
    	return this.wcs;
    }
    
    setWebContentStructureFilter(wcs)  {
		this.wcs = wcs;
    }
    	
	validate() {
		if (this.q.length < this.getQueryMinLength()) {
			return false;
		}
		return true;
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
	start: {
		value: 0
	},
	sortField: {
		value: 'score'
	},
	sortDirection: {
		value: 'asc'
	},
	q: {
		value: ''
	},
	queryMinLength: {
		value: 3
	},
	df: {
		value: ''
	},
	dt: {
		value: ''
	},
	resultsLayout: {
		value: ''
	},
	scope: {
		value: 'all'
	},
	time: {
		value: ''
	},
	type: {
		value: 'everything'
	},
	wcs: {
		value: ''
	}
}

export default GSearchQuery;
