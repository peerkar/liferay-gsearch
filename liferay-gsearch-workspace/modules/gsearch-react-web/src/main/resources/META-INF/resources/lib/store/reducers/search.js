import { FAILURE, REQUEST, SUCCESS } from '../actions/index';
import { SEARCH } from '../actions/search';

import GSearchURLUtil from '../../utils/GSearchURLUtil';
import { RequestParameterNames, clearingParameters, persistedParameters } from '../../constants/requestparameters';
import { ResponseKeys } from '../../constants/responsekeys';

const initialState = {
  error: {},
  isLoading: false,
  response: {},
  searchParams: {}
}

export default function (state = initialState, action) {

  switch (action.type) {

    case SEARCH[REQUEST]:

      let searchParams = mergeSearchParams(action, state);

      return Object.assign({}, state, {
        isLoading: true,
        searchParams: searchParams
      });
    case SEARCH[SUCCESS]:

      // Updates the address bar with the search params

      GSearchURLUtil.buildAddressBarURL(action.searchParams);

      return Object.assign({}, state, {
        error: {},
        isLoading: false,
        response: action.response
      });
    case SEARCH[FAILURE]:
      return Object.assign({}, state, {
        error: action.error,
        isLoading: false
      });
    default:
      return state;
  }
}

/**
 * Combines the new search parameters with the existing ones.
 * 
 * @param {Object} action 
 * @param {Object} state 
 */
function mergeSearchParams(action, state) {

  let params = {};

  // Check whether we should clear the existing searchparams to avoid getting into a deadlock.
  // This is triggered by certain parameters, defined by 'clearingParameters' 
  // Facet change should reset current page.

  let shouldReset = false;
  let shouldResetPage = false;

  Object.keys(action.searchParams).forEach(function (key) {
    if (clearingParameters.includes(key)) {
      if (state.searchParams[key] != action.searchParams[key]) {
        shouldReset = true;
      }
    } else {

    	// Any other parameter is a facet. Changing a value has to reset paging. 

    	shouldResetPage = isFacetVariable(key);
    }    
  });

  if (!shouldReset) {
    Object.keys(state.searchParams).forEach(function (key) {
    	if (key === RequestParameterNames.PAGE && shouldResetPage) {
            params[key] = 1;
    	} else {
    		params[key] = state.searchParams[key];
    	}
    });
  } else {
    Object.keys(state.searchParams).forEach(function (key) {
      if (persistedParameters.includes(key)) {
        params[key] = state.searchParams[key];
      }
    });
  }

  // Add new values

  Object.keys(action.searchParams).forEach(e => params[e] = action.searchParams[e]);

  return params;
}

/**
 * Checks whether the given parameter is a "known", fixed parameter or "unknown", meaning a facet.
 * 
 * @param key
 * @returns
 */
function isFacetVariable(parameter) {

	for (var property in RequestParameterNames) {
        if (RequestParameterNames.hasOwnProperty(property) && RequestParameterNames[property] === parameter) {
            return false;
        }
    }
	return true;
}

// Search selectors.

// Parameters.

export const getFacetValue = (state, facetName) => state.search.searchParams[facetName];
export const getKeywords = (state) => state.search.searchParams[RequestParameterNames.KEYWORDS];
export const getPage = (state) => state.search.searchParams[RequestParameterNames.PAGE];
export const getSearchParams = (state) => state.search.searchParams;
export const getSortDirection = (state) => state.search.searchParams[RequestParameterNames.SORT_DIRECTION];
export const getSortField = (state) => state.search.searchParams[RequestParameterNames.SORT_FIELD];
export const getScope = (state) => state.search.searchParams[RequestParameterNames.SCOPE];
export const getTime = (state) => state.search.searchParams[RequestParameterNames.TIME];
export const getTimeFrom = (state) => state.search.searchParams[RequestParameterNames.TIME_FROM];
export const getTimeTo = (state) => state.search.searchParams[RequestParameterNames.TIME_TO];

// Response.

export const getFacets = (state) => state.search.response[ResponseKeys.FACETS];
export const getItems = (state) => state.search.response[ResponseKeys.ITEMS];
export const getMeta = (state) => state.search.response[ResponseKeys.META];
export const getPagination = (state) => state.search.response[ResponseKeys.PAGINATION];
export const getQuerySuggestions = (state) => state.search.response[ResponseKeys.QUERY_SUGGESTIONS];
export const getResultLayout = (state) => state.search.response[ResponseKeys.RESULT_LAYOUT];
export const getResultLayoutOptions = (state) => state.search.response[ResponseKeys.RESULT_LAYOUT_OPTIONS];
export const getSearchResponse = (state) => state.search.response;

export const getSearchError = (state) => state.search.error;

export const isSearchLoading = (state) => state.search.isLoading;

