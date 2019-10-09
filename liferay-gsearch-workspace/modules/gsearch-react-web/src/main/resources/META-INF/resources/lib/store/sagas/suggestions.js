import { takeLatest } from 'redux-saga/effects';
import { REQUEST } from '../actions/index';
import * as suggestionActions from '../actions/suggestions';
import * as api from '../api/index';
import { fetchEntity } from './index';

/**
 * Fetches suggestions.
 */
export function* fetchSuggestions(payload) {
	let keywords = payload.keywords
	const request = api.getSuggestions;
	yield fetchEntity(request, suggestionActions.suggestions, keywords);
}

/**
 * Suggestions watcher.
 */
export function* watchSuggestions() {
	yield takeLatest(suggestionActions.SUGGESTIONS[REQUEST], fetchSuggestions);
}

