import { FAILURE, REQUEST, SUCCESS } from '../actions/index';
import { SUGGESTIONS } from '../actions/suggestions';

const initialState = {
    error: {},
    isLoading: false,
    results: []
}

export default function (state = initialState, action) {

    switch (action.type) {

        case SUGGESTIONS[REQUEST]:
            return Object.assign({}, state, {
                isLoading: true,
            	keywords: action.keywords
            });
        case SUGGESTIONS[SUCCESS]:

            return Object.assign({}, state, {
                error: {},
                isLoading: false,
                results: action.response
            });
        case SUGGESTIONS[FAILURE]:
            return Object.assign({}, state, {
                error: action.error,
                isLoading: false
            });
        default:
            return state;
    }
}

// Suggestions selectors.

export const getSuggestionsError = (state) => state.suggestions.error;
export const getSuggestions = (state) => state.suggestions.results;
export const isSuggestionsLoading = (state) => state.suggestions.isLoading;
